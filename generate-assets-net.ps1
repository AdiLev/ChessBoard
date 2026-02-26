# Generate Google Play Store Assets using .NET System.Drawing
# This script creates the required assets for Google Play Store submission

Write-Host "Generating Google Play Store Assets using .NET..." -ForegroundColor Cyan

Add-Type -AssemblyName System.Drawing

$assetsDir = "google-play-assets"
$iconDir = "$assetsDir\icon"
$featureDir = "$assetsDir\feature-graphic"
$screenshotsDir = "$assetsDir\screenshots"

# Ensure directories exist
New-Item -ItemType Directory -Force -Path $iconDir | Out-Null
New-Item -ItemType Directory -Force -Path $featureDir | Out-Null
New-Item -ItemType Directory -Force -Path $screenshotsDir | Out-Null

# Source icon (use the highest resolution available)
$sourceIcon = "app\src\main\res\mipmap-xxxhdpi\ic_launcher.png"

if (-not (Test-Path $sourceIcon)) {
    $alternatives = @(
        "app\src\main\res\mipmap-xxhdpi\ic_launcher.png",
        "app\src\main\res\mipmap-xhdpi\ic_launcher.png",
        "app\src\main\res\mipmap-hdpi\ic_launcher.png"
    )
    
    foreach ($alt in $alternatives) {
        if (Test-Path $alt) {
            $sourceIcon = $alt
            break
        }
    }
}

if (-not (Test-Path $sourceIcon)) {
    Write-Host "Source icon not found!" -ForegroundColor Red
    exit 1
}

Write-Host "Source icon: $sourceIcon" -ForegroundColor Green

try {
    # Load source image
    $sourceImage = [System.Drawing.Image]::FromFile((Resolve-Path $sourceIcon).Path)
    Write-Host "Loaded source image: $($sourceImage.Width)x$($sourceImage.Height)" -ForegroundColor Green
    
    # Generate 512x512 app icon
    Write-Host "`nGenerating 512x512 app icon..." -ForegroundColor Cyan
    $icon512 = New-Object System.Drawing.Bitmap(512, 512)
    $iconGraphics = [System.Drawing.Graphics]::FromImage($icon512)
    $iconGraphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $iconGraphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    
    # Calculate scaling to fit icon in 512x512
    $scale = [Math]::Min(512.0 / $sourceImage.Width, 512.0 / $sourceImage.Height)
    $newWidth = [int]($sourceImage.Width * $scale)
    $newHeight = [int]($sourceImage.Height * $scale)
    $x = (512 - $newWidth) / 2
    $y = (512 - $newHeight) / 2
    
    $iconGraphics.DrawImage($sourceImage, $x, $y, $newWidth, $newHeight)
    $icon512.Save("$iconDir\app-icon-512x512.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $iconGraphics.Dispose()
    $icon512.Dispose()
    Write-Host "Created: $iconDir\app-icon-512x512.png" -ForegroundColor Green
    
    # Generate feature graphic (1024x500)
    Write-Host "`nGenerating feature graphic (1024x500)..." -ForegroundColor Cyan
    $featureGraphic = New-Object System.Drawing.Bitmap(1024, 500)
    $featureGraphics = [System.Drawing.Graphics]::FromImage($featureGraphic)
    $featureGraphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $featureGraphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    
    # Background color (from icon description: #6B8492)
    $bgBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(107, 132, 146))
    $featureGraphics.FillRectangle($bgBrush, 0, 0, 1024, 500)
    
    # Draw icon in center (scaled to ~300x300)
    $iconScale = 300.0 / [Math]::Max($sourceImage.Width, $sourceImage.Height)
    $iconWidth = [int]($sourceImage.Width * $iconScale)
    $iconHeight = [int]($sourceImage.Height * $iconScale)
    $iconX = (1024 - $iconWidth) / 2
    $iconY = (500 - $iconHeight) / 2 - 40  # Slightly above center for text below
    
    $featureGraphics.DrawImage($sourceImage, $iconX, $iconY, $iconWidth, $iconHeight)
    
    # Add text
    $titleFont = New-Object System.Drawing.Font("Arial", 48, [System.Drawing.FontStyle]::Bold)
    $taglineFont = New-Object System.Drawing.Font("Arial", 24, [System.Drawing.FontStyle]::Regular)
    $textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
    
    $titleText = "ChessBoard"
    $taglineText = "Classic Chess, Modern Features"
    
    # Measure text for centering
    $titleSize = $featureGraphics.MeasureString($titleText, $titleFont)
    $taglineSize = $featureGraphics.MeasureString($taglineText, $taglineFont)
    
    # Draw title
    $titleX = (1024 - $titleSize.Width) / 2
    $titleY = 500 - 120
    $featureGraphics.DrawString($titleText, $titleFont, $textBrush, $titleX, $titleY)
    
    # Draw tagline
    $taglineX = (1024 - $taglineSize.Width) / 2
    $taglineY = 500 - 60
    $featureGraphics.DrawString($taglineText, $taglineFont, $textBrush, $taglineX, $taglineY)
    
    $featureGraphic.Save("$featureDir\feature-graphic-1024x500.png", [System.Drawing.Imaging.ImageFormat]::Png)
    
    # Cleanup
    $titleFont.Dispose()
    $taglineFont.Dispose()
    $textBrush.Dispose()
    $bgBrush.Dispose()
    $featureGraphics.Dispose()
    $featureGraphic.Dispose()
    
    Write-Host "Created: $featureDir\feature-graphic-1024x500.png" -ForegroundColor Green
    
    # Cleanup source image
    $sourceImage.Dispose()
    
    Write-Host "`n=== Asset Generation Complete ===" -ForegroundColor Green
    Write-Host "`nAssets created:" -ForegroundColor Cyan
    Write-Host "  - App Icon (512x512): $iconDir\app-icon-512x512.png" -ForegroundColor Yellow
    Write-Host "  - Feature Graphic (1024x500): $featureDir\feature-graphic-1024x500.png" -ForegroundColor Yellow
    Write-Host "`nNote: Screenshots need to be taken manually from the running app." -ForegroundColor Yellow
    
} catch {
    Write-Host "Error generating assets: $_" -ForegroundColor Red
    Write-Host "Stack trace: $($_.ScriptStackTrace)" -ForegroundColor Red
    exit 1
}
