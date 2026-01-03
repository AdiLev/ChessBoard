# Build Release APK for ChessBoard App
# This script builds a signed release APK

Write-Host "Building Release APK for ChessBoard..." -ForegroundColor Cyan

$projectRoot = $PSScriptRoot
$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"

# Set Java 17 - Android requires Java 17
$javaHome = $null
$systemJava17 = "C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot"
if (Test-Path $systemJava17) {
    $javaHome = $systemJava17
    Write-Host "Using Java 17 (Microsoft JDK) from: $javaHome" -ForegroundColor Green
} else {
    $java17 = Join-Path $projectRoot "openJdk-17"
    if (Test-Path $java17) {
        $javaHome = $java17
        Write-Host "Using OpenJDK 17 from: $javaHome" -ForegroundColor Green
    }
}

if ($javaHome) {
    $env:JAVA_HOME = $javaHome
    $env:PATH = "$javaHome\bin;$env:PATH"
    Write-Host "Java version:" -ForegroundColor Cyan
    & "$javaHome\bin\java.exe" -version 2>&1 | Out-Null
} else {
    Write-Host "WARNING: Java 17 not found! Android builds require Java 17." -ForegroundColor Red
    exit 1
}

# Set Android SDK environment
$env:ANDROID_HOME = $sdkPath
$env:ANDROID_SDK_ROOT = $sdkPath

# Check for Android SDK
if (-not (Test-Path $sdkPath)) {
    Write-Host "Android SDK not found. Please install it first using install-android-sdk.ps1" -ForegroundColor Red
    exit 1
}

# Check if keystore.properties exists
if (-not (Test-Path "keystore.properties")) {
    Write-Host "`nWARNING: keystore.properties not found!" -ForegroundColor Yellow
    Write-Host "The APK will be unsigned. For a signed release APK, you need to:" -ForegroundColor Yellow
    Write-Host "1. Create a keystore: keytool -genkey -v -keystore chessboard-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias chessboard-key" -ForegroundColor White
    Write-Host "2. Create keystore.properties file (see keystore.properties.example)" -ForegroundColor White
    Write-Host "`nContinuing with unsigned build..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
} else {
    Write-Host "Keystore properties found - APK will be signed" -ForegroundColor Green
}

# Find Gradle wrapper
$gradlePath = $null
$gradleWrapperPath = "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.0-bin\ca5e32bp14vu59qr306oxotwh\gradle-8.0\bin\gradle.bat"
if (Test-Path $gradleWrapperPath) {
    $gradlePath = $gradleWrapperPath
    Write-Host "Found Gradle wrapper at: $gradlePath" -ForegroundColor Green
}

# Try using Android Studio's Gradle if wrapper not found
if (-not $gradlePath) {
    $androidStudioPath = Get-ChildItem "$env:LOCALAPPDATA\Programs\Android\Android Studio" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($androidStudioPath) {
        $gradlePath = Get-ChildItem -Path "$($androidStudioPath.FullName)\gradle" -Recurse -Filter "gradle.bat" -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($gradlePath) {
            $gradlePath = $gradlePath.FullName
            Write-Host "Found Gradle at: $gradlePath" -ForegroundColor Green
        }
    }
}

if (-not $gradlePath) {
    Write-Host "Gradle not found. Cannot build APK." -ForegroundColor Red
    exit 1
}

# Build Release APK
Set-Location $projectRoot
Write-Host "`nBuilding Release APK..." -ForegroundColor Cyan
& $gradlePath assembleRelease --no-daemon

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n=== Build Successful! ===" -ForegroundColor Green
    
    $originalApk = "$projectRoot\app\build\outputs\apk\release\app-release.apk"
    $newApkName = "ChessBoard.apk"
    $newApkPath = "$projectRoot\app\build\outputs\apk\release\$newApkName"
    
    if (Test-Path $originalApk) {
        Copy-Item $originalApk $newApkPath -Force
        Write-Host "`nRelease APK Location: $newApkPath" -ForegroundColor Green
        Get-Item $newApkPath | Select-Object Name, @{Name="Size (MB)";Expression={[math]::Round($_.Length/1MB, 2)}}, LastWriteTime
        Write-Host "`nOriginal APK also available at:" -ForegroundColor Yellow
        Write-Host "  $originalApk" -ForegroundColor Gray
        
        if (Test-Path "keystore.properties") {
            Write-Host "`nThis APK is signed and ready for distribution!" -ForegroundColor Green
        } else {
            Write-Host "`nWARNING: This APK is unsigned!" -ForegroundColor Yellow
        }
    } else {
        Write-Host "APK not found at expected location: $originalApk" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "`nBuild failed!" -ForegroundColor Red
    exit 1
}

