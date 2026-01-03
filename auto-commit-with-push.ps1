# Auto-commit and push script for ChessBoard project
# This version also pushes to remote automatically

Write-Host "Auto-committing and pushing changes..." -ForegroundColor Cyan

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
    
    # Still try to push in case there are unpushed commits
    $remote = git remote -v
    if ($remote) {
        Write-Host "`nPushing to remote..." -ForegroundColor Yellow
        git push origin main
    }
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
    
    # Push to remote if it exists
    $remote = git remote -v
    if ($remote) {
        Write-Host "`nPushing to remote..." -ForegroundColor Yellow
        git push origin main
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "`n=== Successfully pushed to remote! ===" -ForegroundColor Green
        } else {
            Write-Host "`nPush failed. Check your remote connection." -ForegroundColor Red
            Write-Host "You can push manually later with: git push origin main" -ForegroundColor Yellow
        }
    } else {
        Write-Host "`nNo remote repository configured." -ForegroundColor Yellow
        Write-Host "To add remote: git remote add origin <your-repo-url>" -ForegroundColor White
    }
} else {
    Write-Host "`nCommit failed. Please check git status." -ForegroundColor Red
    git status
}

