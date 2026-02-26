# Screenshot Helper Script for Google Play Store
# This script helps organize screenshots for Play Store submission

Write-Host "ChessBoard - Screenshot Helper" -ForegroundColor Cyan
Write-Host "==============================`n" -ForegroundColor Cyan

$screenshotsDir = "screenshots"

Write-Host "Screenshots will be saved to: $screenshotsDir\" -ForegroundColor Yellow
Write-Host ""

# Check if device is connected
$deviceConnected = $false
try {
    $adbDevices = adb devices 2>&1
    if ($adbDevices -match "device$") {
        $deviceConnected = $true
        Write-Host "✓ Android device detected" -ForegroundColor Green
    } else {
        Write-Host "⚠ No Android device detected via ADB" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠ ADB not found in PATH" -ForegroundColor Yellow
}

Write-Host "`nScreenshot Options:" -ForegroundColor Cyan
Write-Host "1. Take screenshot via ADB (if device connected)" -ForegroundColor White
Write-Host "2. Manual instructions" -ForegroundColor White
Write-Host ""

if ($deviceConnected) {
    $choice = Read-Host "Enter choice (1 or 2)"
    
    if ($choice -eq "1") {
        Write-Host "`nTaking screenshot via ADB..." -ForegroundColor Cyan
        $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
        $screenshotName = "screenshot-$timestamp.png"
        
        adb shell screencap -p /sdcard/screenshot.png
        adb pull /sdcard/screenshot.png "$screenshotsDir\$screenshotName"
        adb shell rm /sdcard/screenshot.png
        
        if (Test-Path "$screenshotsDir\$screenshotName") {
            Write-Host "✓ Screenshot saved: $screenshotsDir\$screenshotName" -ForegroundColor Green
        } else {
            Write-Host "✗ Failed to take screenshot" -ForegroundColor Red
        }
    }
}

Write-Host "`nManual Screenshot Instructions:" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "On Android Device:" -ForegroundColor Yellow
Write-Host "  1. Run ChessBoard app on your device"
Write-Host "  2. Navigate to the screen you want to capture"
Write-Host "  3. Press Power + Volume Down buttons simultaneously"
Write-Host "  4. Screenshot saved to device gallery"
Write-Host "  5. Transfer to: $screenshotsDir\" -ForegroundColor White
Write-Host ""
Write-Host "On Android Emulator:" -ForegroundColor Yellow
Write-Host "  1. Run app in Android Studio emulator"
Write-Host "  2. Click camera icon in emulator toolbar"
Write-Host "  3. Save screenshot to: $screenshotsDir\" -ForegroundColor White
Write-Host ""
Write-Host "Recommended Screenshots:" -ForegroundColor Cyan
Write-Host "  1. Main game board with pieces"
Write-Host "  2. Move history interface with seekbar"
Write-Host "  3. Save/Load file selection dialog"
Write-Host "  4. Fullscreen mode"
Write-Host "  5. Promotion dialog"
Write-Host "  6. Castling confirmation dialog"
Write-Host ""
