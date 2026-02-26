# Google Play Store Assets Generation Instructions

## Required Assets

### 1. App Icon (512x512 PNG)
- Source: app\src\main\res\mipmap-xxxhdpi\ic_launcher.png
- Output: google-play-assets\icon\app-icon-512x512.png
- Size: 512x512 pixels
- Format: PNG with transparency

**How to create:**
1. Open app\src\main\res\mipmap-xxxhdpi\ic_launcher.png in an image editor (GIMP, Photoshop, Paint.NET, etc.)
2. Resize to 512x512 pixels
3. Ensure it's square and centered
4. Save as PNG with transparency
5. Place in: google-play-assets\icon\

### 2. Feature Graphic (1024x500 PNG/JPG)
- Output: google-play-assets\feature-graphic\feature-graphic-1024x500.png
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
6. Place in: google-play-assets\feature-graphic\

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
4. Place in: google-play-assets\screenshots\

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
