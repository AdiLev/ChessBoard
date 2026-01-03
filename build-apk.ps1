# Build APK Script for ChessBoard App
Write-Host "Building ChessBoard APK..." -ForegroundColor Cyan

$projectRoot = $PSScriptRoot
$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"

# Set Java to OpenJDK 17
$javaHome = Join-Path $projectRoot "openJdk-17"
if (Test-Path $javaHome) {
    $env:JAVA_HOME = $javaHome
    $env:PATH = "$javaHome\bin;$env:PATH"
    Write-Host "Using OpenJDK 17 from: $javaHome" -ForegroundColor Green
} else {
    Write-Host "OpenJDK 17 not found at: $javaHome" -ForegroundColor Yellow
    Write-Host "Looking for system Java..." -ForegroundColor Yellow
}

# Set Android SDK environment
$env:ANDROID_HOME = $sdkPath
$env:ANDROID_SDK_ROOT = $sdkPath

# Check for Android SDK
if (-not (Test-Path $sdkPath)) {
    Write-Host "Android SDK not found. Please install it first using install-android-sdk.ps1" -ForegroundColor Red
    exit 1
}

# Try to find Gradle
$gradleFound = $false

# Try using Android Studio's Gradle if available
$androidStudioPath = Get-ChildItem "$env:LOCALAPPDATA\Programs\Android\Android Studio" -ErrorAction SilentlyContinue | Select-Object -First 1
if ($androidStudioPath) {
    $gradlePath = Get-ChildItem -Path "$($androidStudioPath.FullName)\gradle" -Recurse -Filter "gradle.bat" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($gradlePath) {
        Write-Host "Found Gradle at: $($gradlePath.FullName)" -ForegroundColor Green
        Set-Location $projectRoot
        & $gradlePath.FullName assembleDebug
        $gradleFound = $true
    }
}

# If Gradle not found, check for existing APK
if (-not $gradleFound) {
    $apkPath = "$projectRoot\app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apkPath) {
        Write-Host "`nGradle not found, but existing APK is available:" -ForegroundColor Yellow
        Write-Host "  $apkPath" -ForegroundColor Cyan
        Write-Host "`nTo rebuild the APK, please use one of these methods:" -ForegroundColor Yellow
        Write-Host "  1. Open the project in Android Studio and build from there" -ForegroundColor White
        Write-Host "  2. Install Gradle and add it to your PATH" -ForegroundColor White
        Write-Host "  3. Use the Android SDK build tools if available" -ForegroundColor White
    } else {
        Write-Host "`nCould not build APK. Please:" -ForegroundColor Red
        Write-Host "  1. Install Android Studio" -ForegroundColor White
        Write-Host "  2. Open the project in Android Studio" -ForegroundColor White
        Write-Host "  3. Build > Build Bundle(s) / APK(s) > Build APK(s)" -ForegroundColor White
    }
}

if ($gradleFound) {
    $originalApk = "$projectRoot\app\build\outputs\apk\debug\app-debug.apk"
    $newApkName = "ChessBoard-debug.apk"
    $newApkPath = "$projectRoot\app\build\outputs\apk\debug\$newApkName"
    
    if (Test-Path $originalApk) {
        # Rename the APK to a more meaningful name
        Copy-Item $originalApk $newApkPath -Force
        Write-Host "`nBuild complete! APK location:" -ForegroundColor Green
        Write-Host "  $newApkPath" -ForegroundColor Cyan
        Write-Host "`nOriginal APK also available at:" -ForegroundColor Yellow
        Write-Host "  $originalApk" -ForegroundColor Gray
    } else {
        Write-Host "`nBuild complete! APK location:" -ForegroundColor Green
        Write-Host "  $originalApk" -ForegroundColor Cyan
    }
} else {
    # Even if Gradle wasn't found, rename existing APK if it exists
    $originalApk = "$projectRoot\app\build\outputs\apk\debug\app-debug.apk"
    $newApkName = "ChessBoard-debug.apk"
    $newApkPath = "$projectRoot\app\build\outputs\apk\debug\$newApkName"
    
    if (Test-Path $originalApk) {
        Copy-Item $originalApk $newApkPath -Force
        Write-Host "`nRenamed existing APK:" -ForegroundColor Green
        Write-Host "  $newApkPath" -ForegroundColor Cyan
    }
}

