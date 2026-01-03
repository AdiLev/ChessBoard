# Auto-commit script for ChessBoard project
# Run this script to automatically commit all changes to git

Write-Host "Auto-committing changes to git..." -ForegroundColor Cyan

# Check if git is initialized
if (-not (Test-Path ".git")) {
    Write-Host "Git not initialized. Initializing..." -ForegroundColor Yellow
    git init
    git branch -M main
}

# Add all changes
Write-Host "`nStaging all changes..." -ForegroundColor Yellow
git add .

# Check if there are changes to commit
$status = git status --porcelain
if ([string]::IsNullOrWhiteSpace($status)) {
    Write-Host "No changes to commit." -ForegroundColor Green
    exit 0
}

# Create commit message with timestamp
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$commitMessage = "Auto-commit: Changes at $timestamp"

Write-Host "`nCommitting changes..." -ForegroundColor Yellow
git commit -m $commitMessage

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n=== Successfully committed! ===" -ForegroundColor Green
    Write-Host "Commit message: $commitMessage" -ForegroundColor Cyan
    
    # Show recent commits
    Write-Host "`nRecent commits:" -ForegroundColor Yellow
    git log --oneline -5
    
    # Check if remote exists
    $remote = git remote -v
    if ($remote) {
        Write-Host "`nNote: Changes are committed locally." -ForegroundColor Yellow
        Write-Host "To push to remote, run: git push" -ForegroundColor White
    }
} else {
    Write-Host "`nCommit failed. Please check git status." -ForegroundColor Red
    git status
}


