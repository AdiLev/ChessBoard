# Build APK Script for ChessBoard App
Write-Host "Building ChessBoard APK..." -ForegroundColor Cyan

$projectRoot = $PSScriptRoot
$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"

# Set Java - Android requires Java 17, prioritize Java 17 installations
$javaHome = $null

# Try system Java 17 (Microsoft JDK) first - same as release build script
$systemJava17 = "C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot"
if (Test-Path $systemJava17) {
    $javaHome = $systemJava17
    Write-Host "Using Java 17 (Microsoft JDK) from: $javaHome" -ForegroundColor Green
}

# Try OpenJDK 17 in project directory
if (-not $javaHome) {
    $java17 = Join-Path $projectRoot "openJdk-17"
    if (Test-Path $java17) {
        $javaHome = $java17
        Write-Host "Using OpenJDK 17 from: $javaHome" -ForegroundColor Green
    }
}

# Try to find other Java 17 installations
if (-not $javaHome) {
    $possibleJava17Paths = @(
        "C:\Program Files\Java\jdk-17*",
        "C:\Program Files\Eclipse Adoptium\jdk-17*",
        "$env:ProgramFiles\Java\jdk-17*"
    )
    foreach ($pathPattern in $possibleJava17Paths) {
        $foundJava = Get-ChildItem -Path $pathPattern -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($foundJava -and (Test-Path (Join-Path $foundJava.FullName "bin\java.exe"))) {
            $javaHome = $foundJava.FullName
            Write-Host "Using Java 17 from: $javaHome" -ForegroundColor Green
            break
        }
    }
}

if ($javaHome) {
    $env:JAVA_HOME = $javaHome
    $env:PATH = "$javaHome\bin;$env:PATH"
    Write-Host "Java version:" -ForegroundColor Cyan
    & "$javaHome\bin\java.exe" -version 2>&1 | Select-Object -First 1
} else {
    Write-Host "WARNING: Java 17 not found! Android builds require Java 17." -ForegroundColor Red
    Write-Host "Please install Java 17 (JDK) and try again." -ForegroundColor Yellow
    Write-Host "Android Studio includes Java 17, or you can download from:" -ForegroundColor Yellow
    Write-Host "  https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Cyan
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
$gradlePath = $null

# Try using Gradle wrapper from user's .gradle directory (same as release script)
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

# Build if Gradle found
if ($gradlePath) {
    Set-Location $projectRoot
    Write-Host "Building APK..." -ForegroundColor Cyan
    & $gradlePath assembleDebug --no-daemon
    if ($LASTEXITCODE -eq 0) {
        $gradleFound = $true
    } else {
        Write-Host "Build failed with exit code: $LASTEXITCODE" -ForegroundColor Red
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

