# Generate Google Play Store Assets from App Icon
# This script creates the required assets for Google Play Store submission

Write-Host "Generating Google Play Store Assets..." -ForegroundColor Cyan

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
    Write-Host "Source icon not found: $sourceIcon" -ForegroundColor Red
    Write-Host "Trying alternative sources..." -ForegroundColor Yellow
    
    # Try other resolutions
    $alternatives = @(
        "app\src\main\res\mipmap-xxhdpi\ic_launcher.png",
        "app\src\main\res\mipmap-xhdpi\ic_launcher.png",
        "app\src\main\res\mipmap-hdpi\ic_launcher.png"
    )
    
    $found = $false
    foreach ($alt in $alternatives) {
        if (Test-Path $alt) {
            $sourceIcon = $alt
            $found = $true
            Write-Host "Using: $alt" -ForegroundColor Green
            break
        }
    }
    
    if (-not $found) {
        Write-Host "No icon found. Please ensure ic_launcher.png exists." -ForegroundColor Red
        exit 1
    }
}

Write-Host "Source icon: $sourceIcon" -ForegroundColor Green

# Check if ImageMagick is available
$magickAvailable = $false
try {
    $magickVersion = & magick -version 2>&1
    if ($LASTEXITCODE -eq 0) {
        $magickAvailable = $true
        Write-Host "ImageMagick found - will use for image processing" -ForegroundColor Green
    }
} catch {
    Write-Host "ImageMagick not found - will provide instructions for manual creation" -ForegroundColor Yellow
}

if ($magickAvailable) {
    # Generate 512x512 app icon
    Write-Host "`nGenerating 512x512 app icon..." -ForegroundColor Cyan
    $icon512 = "$iconDir\app-icon-512x512.png"
    & magick $sourceIcon -resize 512x512 -background transparent -gravity center -extent 512x512 $icon512
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Created: $icon512" -ForegroundColor Green
    }
    
    # Generate feature graphic (1024x500)
    Write-Host "`nGenerating feature graphic (1024x500)..." -ForegroundColor Cyan
    $featureGraphic = "$featureDir\feature-graphic-1024x500.png"
    
    # Create a feature graphic with the icon centered
    & magick -size 1024x500 xc:"#6B8492" ``
        `( $sourceIcon -resize 300x300 `) ``
        -gravity center -composite ``
        -font Arial -pointsize 48 -fill white -gravity south -annotate +0+80 "ChessBoard" ``
        -font Arial -pointsize 24 -fill white -gravity south -annotate +0+20 "Classic Chess, Modern Features" ``
        $featureGraphic
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Created: $featureGraphic" -ForegroundColor Green
    }
    
} else {
    Write-Host "`nImageMagick not available. Creating instructions file..." -ForegroundColor Yellow
    
    # Create instructions file
    $instructions = @"
# Google Play Store Assets Generation Instructions

## Required Assets

### 1. App Icon (512x512 PNG)
- Source: $sourceIcon
- Output: $iconDir\app-icon-512x512.png
- Size: 512x512 pixels
- Format: PNG with transparency

**How to create:**
1. Open $sourceIcon in an image editor (GIMP, Photoshop, Paint.NET, etc.)
2. Resize to 512x512 pixels
3. Ensure it's square and centered
4. Save as PNG with transparency
5. Place in: $iconDir\

### 2. Feature Graphic (1024x500 PNG/JPG)
- Output: $featureDir\feature-graphic-1024x500.png
- Size: 1024x500 pixels
- Format: PNG or JPG

**How to create:**
1. Create new image: 1024x500 pixels
2. Background: Use app color (#6B8492 or similar)
3. Center the app icon (resized to ~300x300)
4. Add text:
   - Title: "ChessBoard" (large, white)
   - Tagline: "Classic Chess, Modern Features" (smaller, white)
5. Save as PNG or JPG
6. Place in: $featureDir\

### 3. Screenshots
- Minimum: 2 screenshots
- Recommended: 4-8 screenshots
- Sizes: Phone (320-3840px wide), Tablet (optional)
- Format: PNG or JPG

**How to create:**
1. Run the app on a device or emulator
2. Take screenshots of:
   - Main game board
   - Move history interface
   - Save/Load dialog
   - Fullscreen mode
   - Promotion dialog
3. Save screenshots
4. Place in: $screenshotsDir\

## Online Tools (Alternative)

If you don't have image editing software:
- **Photopea:** https://www.photopea.com (Free, browser-based Photoshop)
- **Canva:** https://www.canva.com (Free templates for feature graphics)
- **Remove.bg:** https://www.remove.bg (For transparent backgrounds)

## ImageMagick Installation (Recommended)

To use the automated script:
1. Download ImageMagick: https://imagemagick.org/script/download.php
2. Install ImageMagick
3. Run this script again: .\generate-play-store-assets.ps1
"@
    
    $instructions | Out-File -FilePath "$assetsDir\ASSET_CREATION_INSTRUCTIONS.md" -Encoding UTF8
    Write-Host "Instructions saved to: $assetsDir\ASSET_CREATION_INSTRUCTIONS.md" -ForegroundColor Cyan
}

Write-Host "`n=== Asset Generation Complete ===" -ForegroundColor Green
Write-Host "`nAssets location: $assetsDir\" -ForegroundColor Cyan
Write-Host "  - Icon: $iconDir\" -ForegroundColor Yellow
Write-Host "  - Feature Graphic: $featureDir\" -ForegroundColor Yellow
Write-Host "  - Screenshots: $screenshotsDir\" -ForegroundColor Yellow

if (-not $magickAvailable) {
    Write-Host "`nNote: Install ImageMagick for automatic generation, or follow instructions in:" -ForegroundColor Yellow
    Write-Host "  $assetsDir\ASSET_CREATION_INSTRUCTIONS.md" -ForegroundColor Cyan
}
