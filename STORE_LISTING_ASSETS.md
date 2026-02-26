# Google Play Store Listing Assets

## Required Assets Checklist

### ✅ App Icon
- **Status:** Available
- **Location:** `app/src/main/res/mipmap-*/ic_launcher.png`
- **Required Size:** 512x512 pixels (32-bit PNG with alpha)
- **Action:** Export highest resolution version (xxxhdpi) as 512x512 PNG

### ⚠️ Feature Graphic
- **Status:** Needs Creation
- **Required Size:** 1024x500 pixels
- **Format:** PNG or JPG
- **Content Suggestions:**
  - Chess board in the center
  - App name "ChessBoard" prominently displayed
  - Tagline: "Classic Chess, Modern Features"
  - Show key features: Save/Load, Move History, Castling
  - Use app color scheme (green, blue, purple buttons)

### ⚠️ Screenshots
- **Status:** Needs Creation
- **Minimum Required:** 2 screenshots
- **Recommended:** 4-8 screenshots
- **Required Sizes:**
  - Phone: At least 320px wide, max 3840px wide
  - Tablet: At least 320px wide, max 3840px wide
  - TV: 1920x1080 or 1280x720
- **Suggested Screenshots:**
  1. Main game board with pieces (showing white pieces with gray outlines)
  2. Move history interface with seekbar
  3. Save/Load dialog showing file selection
  4. Fullscreen mode
  5. Promotion dialog
  6. Castling confirmation dialog
  7. Game in progress with captured pieces visible

### ✅ Privacy Policy
- **Status:** Created
- **File:** `PRIVACY_POLICY.md`
- **Action:** Host online (GitHub Pages, Google Sites, or your website)
- **URL Format:** https://yourdomain.com/privacy-policy or https://username.github.io/ChessBoard/privacy-policy

## Store Listing Text

### App Name
```
ChessBoard
```

### Short Description (80 characters)
```
Classic chess game with move history, castling, and save/load features
```

### Full Description
See `GOOGLE_PLAY_STORE_LISTING.md` for complete description.

### Promotional Text (80 characters)
```
Play classic chess with move history, castling, and save/load features
```

### What's New (for initial release)
```
Initial Release
- Complete chess game with all standard rules
- Move history and navigation
- Save and load game functionality
- Castling support (kingside and queenside)
- Pawn promotion
- Fullscreen mode
- Beautiful, intuitive interface
```

## App Category
- **Primary Category:** Games > Board
- **Secondary Category:** (Optional) Games > Strategy

## Tags/Keywords
chess, board game, strategy, classic, moves, castling, promotion, save game, chess board, traditional game, offline game, single player

## Content Rating
- **Rating:** Everyone
- **Questionnaire:** See `CONTENT_RATING_QUESTIONNAIRE.md`

## Pricing
- **Price:** Free
- **In-App Purchases:** None
- **Ads:** None

## Contact Information
- **Developer Name:** [Your Name/Company]
- **Email:** [Your Email]
- **Website:** [Your Website or GitHub]
- **Support URL:** [Support URL or GitHub Issues]

## Additional Information

### App Version
- **Version Code:** 1
- **Version Name:** 1.0

### Target Audience
- **Age Group:** All ages
- **Primary Audience:** Chess enthusiasts, casual players, students learning chess

### Localization
- **Default Language:** English
- **Additional Languages:** (Optional - can add later)

## Submission Checklist

- [x] App signed with release keystore
- [x] Target SDK 34 (Android 14)
- [x] Min SDK 24 (Android 7.0)
- [x] Privacy Policy created
- [x] Store listing text prepared
- [x] Content rating questionnaire completed
- [ ] App icon exported (512x512)
- [ ] Feature graphic created (1024x500)
- [ ] Screenshots taken (minimum 2)
- [ ] Privacy Policy hosted online
- [ ] AAB file built and ready
- [ ] All assets prepared

## Next Steps

1. **Create Visual Assets:**
   - Export app icon as 512x512 PNG
   - Create feature graphic (1024x500)
   - Take screenshots of the app in action

2. **Host Privacy Policy:**
   - Upload `PRIVACY_POLICY.md` to a public URL
   - GitHub Pages is a free option
   - Or use Google Sites

3. **Build AAB:**
   - Run `build-release-aab.ps1` to create the Android App Bundle
   - Verify the AAB is signed correctly

4. **Submit to Google Play Console:**
   - Create developer account ($25 one-time fee)
   - Create new app
   - Fill in all store listing information
   - Upload AAB file
   - Complete content rating
   - Submit for review
