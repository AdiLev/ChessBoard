# Create 1024x500 Distribution Icon (Feature Graphic)
# Uses .NET System.Drawing to create the image

Write-Host "Creating 1024x500 distribution icon..." -ForegroundColor Cyan

$assetsDir = "google-play-assets"
$featureDir = "$assetsDir\feature-graphic"
$outputFile = "$featureDir\feature-graphic-1024x500.png"

# Ensure directory exists
New-Item -ItemType Directory -Force -Path $featureDir | Out-Null

# Source icon
$sourceIcon = "app\src\main\res\mipmap-xxxhdpi\ic_launcher.png"
if (-not (Test-Path $sourceIcon)) {
    $sourceIcon = "google-play-assets\icon\app-icon-512x512.png"
    if (-not (Test-Path $sourceIcon)) {
        Write-Host "Source icon not found. Please ensure ic_launcher.png exists." -ForegroundColor Red
        exit 1
    }
}

try {
    # Load System.Drawing assembly
    Add-Type -AssemblyName System.Drawing
    
    # Create a 1024x500 bitmap
    $width = 1024
    $height = 500
    $bitmap = New-Object System.Drawing.Bitmap($width, $height)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    
    # Set high quality rendering
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    
    # Fill background with app color (#6B8492)
    $bgColor = [System.Drawing.Color]::FromArgb(107, 132, 146) # #6B8492
    $bgBrush = New-Object System.Drawing.SolidBrush($bgColor)
    $graphics.FillRectangle($bgBrush, 0, 0, $width, $height)
    
    # Load and resize the app icon
    if (Test-Path $sourceIcon) {
        $icon = [System.Drawing.Image]::FromFile((Resolve-Path $sourceIcon).Path)
        
        # Resize icon to 300x300 (maintaining aspect ratio)
        $iconSize = 300
        $iconX = ($width - $iconSize) / 2
        $iconY = ($height - $iconSize) / 2 - 40 # Slightly above center for text below
        
        # Draw icon with high quality
        $graphics.DrawImage($icon, $iconX, $iconY, $iconSize, $iconSize)
        $icon.Dispose()
    }
    
    # Add text
    $fontTitle = New-Object System.Drawing.Font("Arial", 48, [System.Drawing.FontStyle]::Bold)
    $fontTagline = New-Object System.Drawing.Font("Arial", 24, [System.Drawing.FontStyle]::Regular)
    $textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
    
    # Title: "ChessBoard"
    $titleText = "ChessBoard"
    $titleSize = $graphics.MeasureString($titleText, $fontTitle)
    $titleX = ($width - $titleSize.Width) / 2
    $titleY = $height - 100
    $graphics.DrawString($titleText, $fontTitle, $textBrush, $titleX, $titleY)
    
    # Tagline: "Classic Chess, Modern Features"
    $taglineText = "Classic Chess, Modern Features"
    $taglineSize = $graphics.MeasureString($taglineText, $fontTagline)
    $taglineX = ($width - $taglineSize.Width) / 2
    $taglineY = $height - 50
    $graphics.DrawString($taglineText, $fontTagline, $textBrush, $taglineX, $taglineY)
    
    # Save the image
    $bitmap.Save($outputFile, [System.Drawing.Imaging.ImageFormat]::Png)
    
    # Cleanup
    $graphics.Dispose()
    $bitmap.Dispose()
    $bgBrush.Dispose()
    $textBrush.Dispose()
    $fontTitle.Dispose()
    $fontTagline.Dispose()
    
    Write-Host "`nSuccessfully created distribution icon!" -ForegroundColor Green
    Write-Host "Location: $outputFile" -ForegroundColor Cyan
    
    # Show file info
    $fileInfo = Get-Item $outputFile
    Write-Host "`nFile Details:" -ForegroundColor Yellow
    Write-Host "  Size: $($fileInfo.Length / 1KB) KB" -ForegroundColor White
    Write-Host "  Dimensions: 1024x500 pixels" -ForegroundColor White
    Write-Host "  Format: PNG" -ForegroundColor White
    
} catch {
    Write-Host "`nError creating image: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "`nStack trace:" -ForegroundColor Yellow
    Write-Host $_.ScriptStackTrace -ForegroundColor Gray
    exit 1
}
