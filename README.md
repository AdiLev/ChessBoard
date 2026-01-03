# ChessBoard Android Application

A full-featured chess game for Android with move history, pawn promotion, and castling.

## Quick Start

### Building the APK
```powershell
.\build-apk.ps1
```

### Auto-Commit Your Changes
**IMPORTANT: To save your work automatically, run:**
```powershell
.\auto-commit.ps1
```

This will commit all your changes to git so you don't lose your work!

For automatic commit AND push to GitHub:
```powershell
.\auto-commit-with-push.ps1
```

## Features

- ✅ Full chess game with all standard rules
- ✅ Pawn promotion (with captured pieces option)
- ✅ Kingside and Queenside castling (O-O, O-O-O)
- ✅ Move history with seekbar navigation
- ✅ Custom ChessBoard.png icon
- ✅ Responsive UI with colored buttons
- ✅ Centered chess board

## Project Structure

```
app/
├── src/main/
│   ├── java/com/chessboard/app/
│   │   ├── ChessGame.kt          # Core game logic
│   │   ├── MainActivity.kt        # Main UI
│   │   ├── ChessBoardView.kt     # Board rendering
│   │   └── ...
│   └── res/
│       ├── layout/                # UI layouts
│       ├── mipmap-*/              # App icons
│       └── values/                # Strings, colors
├── build.gradle                   # App build config
build.gradle                       # Project build config
settings.gradle                    # Project settings
```

## Git Workflow

### Save Your Work
After making changes, always run:
```powershell
.\auto-commit.ps1
```

This ensures your work is saved in git history.

### Push to GitHub
To push your commits to GitHub:
```powershell
git push origin main
```

Or use the auto-push script:
```powershell
.\auto-commit-with-push.ps1
```

## Build Requirements

- Java 17 (OpenJDK or Microsoft JDK)
- Android SDK
- Gradle 8.0+

## APK Location

After building, find your APK at:
```
app\build\outputs\apk\debug\ChessBoard-debug.apk
```

## Chess Rules Implemented

- ✅ Standard piece movement (pawn, rook, knight, bishop, queen, king)
- ✅ Pawn capture (diagonal only)
- ✅ Pawn promotion (to captured pieces or standard pieces)
- ✅ Kingside castling (O-O)
- ✅ Queenside castling (O-O-O)
- ✅ Castling restrictions (king/rook moved, path clear, not in check)
- ✅ Knight can jump over pieces
- ✅ King movement disables castling
- ✅ Move validation and turn management

## License

This project is for personal/educational use.


