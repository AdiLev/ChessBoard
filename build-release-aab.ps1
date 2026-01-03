# Build Release AAB for Google Play Store
# This script builds an Android App Bundle (AAB) for Google Play Store submission

Write-Host "Building Release AAB for Google Play Store..." -ForegroundColor Cyan

$java17 = "C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot"
$gradleWrapper = "C:\Users\USER\.gradle\wrapper\dists\gradle-8.0-bin\ca5e32bp14vu59qr306oxotwh\gradle-8.0\bin\gradle.bat"

# Set Java environment
$env:JAVA_HOME = $java17
$env:PATH = "$java17\bin;$env:PATH"

# Set Android SDK environment
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"

# Check if keystore.properties exists
if (-not (Test-Path "keystore.properties")) {
    Write-Host "`nWARNING: keystore.properties not found!" -ForegroundColor Yellow
    Write-Host "The AAB will be unsigned. For Google Play Store, you need to:" -ForegroundColor Yellow
    Write-Host "1. Create a keystore: keytool -genkey -v -keystore chessboard-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias chessboard-key" -ForegroundColor White
    Write-Host "2. Create keystore.properties file (see keystore.properties.example)" -ForegroundColor White
    Write-Host "`nContinuing with unsigned build..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
}

# Build AAB
Write-Host "`nBuilding AAB..." -ForegroundColor Cyan
& $gradleWrapper bundleRelease --no-daemon

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n=== Build Successful! ===" -ForegroundColor Green
    
    $aabPath = "app\build\outputs\bundle\release\app-release.aab"
    if (Test-Path $aabPath) {
        Copy-Item $aabPath "app\build\outputs\bundle\release\ChessBoard-release.aab" -Force
        Write-Host "`nAAB Location: app\build\outputs\bundle\release\ChessBoard-release.aab" -ForegroundColor Green
        Get-Item "app\build\outputs\bundle\release\ChessBoard-release.aab" | Select-Object Name, @{Name="Size (MB)";Expression={[math]::Round($_.Length/1MB, 2)}}, LastWriteTime
        Write-Host "`nThis AAB is ready for Google Play Store upload!" -ForegroundColor Green
    } else {
        Write-Host "AAB not found at expected location" -ForegroundColor Red
    }
} else {
    Write-Host "`nBuild failed!" -ForegroundColor Red
    exit 1
}


