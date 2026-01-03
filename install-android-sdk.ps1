# Android SDK Installation Script
Write-Host "Installing Android SDK Command-Line Tools..." -ForegroundColor Cyan

$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
$toolsPath = "$sdkPath\cmdline-tools"
$latestPath = "$toolsPath\latest"

# Create SDK directory
Write-Host "Creating SDK directory: $sdkPath" -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path $sdkPath | Out-Null
New-Item -ItemType Directory -Force -Path $toolsPath | Out-Null

# Download Android SDK Command-Line Tools
Write-Host "Downloading Android SDK Command-Line Tools..." -ForegroundColor Yellow
$zipUrl = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
$zipFile = "$env:TEMP\android-cmdline-tools.zip"

try {
    Invoke-WebRequest -Uri $zipUrl -OutFile $zipFile -UseBasicParsing
    Write-Host "Download complete!" -ForegroundColor Green
} catch {
    Write-Host "Error downloading SDK tools: $_" -ForegroundColor Red
    exit 1
}

# Extract to temp location first
Write-Host "Extracting SDK tools..." -ForegroundColor Yellow
$extractPath = "$env:TEMP\android-cmdline-tools"
Expand-Archive -Path $zipFile -DestinationPath $extractPath -Force

# Move to correct location
$cmdlineToolsPath = Get-ChildItem -Path $extractPath -Directory | Select-Object -First 1
if ($cmdlineToolsPath) {
    Move-Item -Path $cmdlineToolsPath.FullName -Destination $latestPath -Force
    Write-Host "SDK tools extracted to: $latestPath" -ForegroundColor Green
}

# Clean up
Remove-Item -Path $zipFile -Force -ErrorAction SilentlyContinue
Remove-Item -Path $extractPath -Force -Recurse -ErrorAction SilentlyContinue

# Set environment variables
Write-Host "Setting up environment variables..." -ForegroundColor Yellow
$env:ANDROID_HOME = $sdkPath
$env:ANDROID_SDK_ROOT = $sdkPath
$env:PATH += ";$latestPath\bin;$sdkPath\platform-tools"

# Install required SDK components
Write-Host "Installing Android SDK components (this may take a few minutes)..." -ForegroundColor Yellow
Write-Host "Installing: platform-tools, platforms;android-34, build-tools;34.0.0" -ForegroundColor Cyan

$sdkmanager = "$latestPath\bin\sdkmanager.bat"

# Accept licenses first
Write-Host "Accepting Android SDK licenses..." -ForegroundColor Yellow
echo "y" | & $sdkmanager --sdk_root=$sdkPath --licenses | Out-Null

# Install required packages
Write-Host "Installing packages..." -ForegroundColor Yellow
& $sdkmanager --sdk_root=$sdkPath "platform-tools" "platforms;android-34" "build-tools;34.0.0"

Write-Host "`nAndroid SDK installation complete!" -ForegroundColor Green
Write-Host "SDK Location: $sdkPath" -ForegroundColor Cyan
Write-Host "`nPlease add these to your system PATH or restart your terminal:" -ForegroundColor Yellow
Write-Host "ANDROID_HOME=$sdkPath" -ForegroundColor White
Write-Host "ANDROID_SDK_ROOT=$sdkPath" -ForegroundColor White

