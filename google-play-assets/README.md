# Google Play Store Assets

This folder contains all visual assets required for Google Play Store submission.

## Generated Assets

### ✅ App Icon
- **File:** `icon/app-icon-512x512.png`
- **Size:** 512x512 pixels
- **Format:** PNG with transparency
- **Status:** ✅ Generated
- **Usage:** Upload to Play Console > Store listing > App icon

### ✅ Feature Graphic
- **File:** `feature-graphic/feature-graphic-1024x500.png`
- **Size:** 1024x500 pixels
- **Format:** PNG
- **Status:** ✅ Generated
- **Usage:** Upload to Play Console > Store listing > Feature graphic

### ⚠️ Screenshots
- **Folder:** `screenshots/`
- **Status:** Needs manual creation
- **Requirements:**
  - Minimum: 2 screenshots
  - Recommended: 4-8 screenshots
  - Phone screenshots: 320-3840px wide
  - Tablet screenshots: Optional but recommended

## Screenshot Guidelines

### Required Screenshots (Minimum 2)

1. **Main Game Board**
   - Show the chess board with pieces
   - Display move history controls
   - Show captured pieces area

2. **Move History Interface**
   - Show the seekbar
   - Display move history text
   - Show navigation buttons

### Recommended Additional Screenshots

3. **Save/Load Dialog**
   - Show file selection dialog
   - Display available CHB files

4. **Fullscreen Mode**
   - Show chess board in fullscreen
   - Clean, focused view

5. **Promotion Dialog**
   - Show pawn promotion selection
   - Display piece options

6. **Castling Confirmation**
   - Show castling dialog
   - Display castling options

7. **Game in Progress**
   - Show active game state
   - Display captured pieces
   - Show move indicators

## How to Take Screenshots

### On Android Device:
1. Run the app on your device
2. Navigate to the screen you want to capture
3. Press Power + Volume Down buttons simultaneously
4. Screenshot saved to device gallery
5. Transfer to computer and place in `screenshots/` folder

### On Android Emulator:
1. Run the app in Android Studio emulator
2. Use the camera icon in the emulator toolbar
3. Or use: `adb shell screencap -p /sdcard/screenshot.png`
4. Pull screenshot: `adb pull /sdcard/screenshot.png screenshots/`

### Using ADB (if device connected):
```powershell
# Take screenshot
adb shell screencap -p /sdcard/screenshot.png

# Pull to computer
adb pull /sdcard/screenshot.png screenshots/screenshot-1.png
```

## File Naming Convention

Name screenshots descriptively:
- `screenshot-main-board.png`
- `screenshot-move-history.png`
- `screenshot-save-load.png`
- `screenshot-fullscreen.png`
- `screenshot-promotion.png`
- `screenshot-castling.png`

## Upload Order

When uploading to Play Console, order screenshots to tell a story:
1. Main game board (first impression)
2. Move history (key feature)
3. Save/Load (useful feature)
4. Fullscreen mode (visual appeal)
5. Additional features (promotion, castling, etc.)

## Asset Checklist

Before submitting to Play Console:

- [x] App icon (512x512) - Generated
- [x] Feature graphic (1024x500) - Generated
- [ ] Screenshot 1 - Main board
- [ ] Screenshot 2 - Move history
- [ ] Screenshot 3 - Save/Load (optional)
- [ ] Screenshot 4 - Fullscreen (optional)
- [ ] Screenshot 5 - Promotion (optional)
- [ ] Screenshot 6 - Castling (optional)

## Notes

- All assets are ready for upload to Google Play Console
- Screenshots should be taken from actual device/emulator for best quality
- Ensure screenshots show the app in a good light
- Remove any personal information from screenshots
- Use consistent device orientation (portrait recommended)
