# Accept all Android SDK licenses
$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
$latestPath = "$sdkPath\cmdline-tools\latest"
$sdkmanager = "$latestPath\bin\sdkmanager.bat"

Write-Host "Accepting all Android SDK licenses..." -ForegroundColor Yellow

# Accept all licenses automatically
$yes = "y`n" * 10
$yes | & $sdkmanager --sdk_root=$sdkPath --licenses

Write-Host "Licenses accepted!" -ForegroundColor Green

