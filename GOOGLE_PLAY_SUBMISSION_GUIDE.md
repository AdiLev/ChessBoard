# Complete Google Play Store Submission Guide - ChessBoard

This guide contains all the documents and information needed to submit ChessBoard to the Google Play Store.

## 📋 Quick Reference

### Essential Documents
1. **GOOGLE_PLAY_STORE_LISTING.md** - Complete store listing text (descriptions, tags, etc.)
2. **PRIVACY_POLICY.md** - Privacy policy document (needs to be hosted online)
3. **CONTENT_RATING_QUESTIONNAIRE.md** - Answers for content rating questionnaire
4. **STORE_LISTING_ASSETS.md** - Visual assets requirements and checklist
5. **SUBMISSION_CHECKLIST.md** - Step-by-step submission checklist

## 🚀 Quick Start

### Step 1: Prepare Visual Assets
- [ ] Export app icon as 512x512 PNG
- [ ] Create feature graphic (1024x500)
- [ ] Take screenshots (minimum 2, recommended 4-8)

### Step 2: Host Privacy Policy
- [ ] Upload `PRIVACY_POLICY.md` to a public URL
- [ ] Options: GitHub Pages, Google Sites, or your website
- [ ] Note the URL for Play Console

### Step 3: Build AAB
```powershell
.\build-release-aab.ps1
```
- AAB location: `app\build\outputs\bundle\release\app-release.aab`

### Step 4: Create Google Play Developer Account
- [ ] Go to https://play.google.com/console/signup
- [ ] Pay $25 one-time registration fee
- [ ] Complete developer profile

### Step 5: Submit to Play Console
- [ ] Create new app
- [ ] Fill in store listing (use `GOOGLE_PLAY_STORE_LISTING.md`)
- [ ] Upload visual assets
- [ ] Complete content rating (use `CONTENT_RATING_QUESTIONNAIRE.md`)
- [ ] Add privacy policy URL
- [ ] Upload AAB file
- [ ] Submit for review

## 📝 Store Listing Information

### App Name
```
ChessBoard
```

### Short Description (80 characters)
```
Classic chess game with move history, castling, and save/load features
```

### Full Description
See `GOOGLE_PLAY_STORE_LISTING.md` for the complete 4000-character description.

### Category
Games > Board

### Content Rating
Everyone (E)

### Pricing
Free (no in-app purchases, no ads)

## 🎨 Visual Assets Needed

### App Icon
- **Size:** 512x512 pixels
- **Format:** PNG (32-bit with alpha)
- **Status:** Export from existing icon in `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`

### Feature Graphic
- **Size:** 1024x500 pixels
- **Format:** PNG or JPG
- **Status:** Needs creation
- **Content:** Chess board, app name, key features

### Screenshots
- **Minimum:** 2 screenshots
- **Recommended:** 4-8 screenshots
- **Sizes:** Phone (320-3840px wide), Tablet (optional)
- **Status:** Needs creation

## 📄 Privacy Policy

The privacy policy is ready in `PRIVACY_POLICY.md`. You need to:
1. Host it online (GitHub Pages recommended)
2. Get the public URL
3. Enter the URL in Play Console

**Quick GitHub Pages Setup:**
1. Create a `docs` folder in your repository
2. Copy `PRIVACY_POLICY.md` to `docs/privacy-policy.md`
3. Enable GitHub Pages in repository settings
4. Use URL: `https://[username].github.io/ChessBoard/privacy-policy`

## ✅ Pre-Submission Checklist

### Technical
- [x] App signed with release keystore
- [x] Target SDK 34
- [x] Min SDK 24
- [x] App builds successfully
- [x] All features tested

### Content
- [x] Store listing text prepared
- [x] Privacy policy written
- [x] Content rating answers prepared
- [ ] Visual assets created
- [ ] Privacy policy hosted

### Build
- [ ] AAB file built
- [ ] AAB verified and signed
- [ ] Version code: 1
- [ ] Version name: 1.0

## 📱 App Information Summary

**App Name:** ChessBoard  
**Package Name:** com.chessboard.app  
**Version Code:** 1  
**Version Name:** 1.0  
**Category:** Games > Board  
**Content Rating:** Everyone  
**Price:** Free  
**In-App Purchases:** None  
**Ads:** None  
**Internet Required:** No  
**Target Audience:** All ages  

## 🔗 Important Links

- **Google Play Console:** https://play.google.com/console
- **Developer Signup:** https://play.google.com/console/signup
- **Play Console Help:** https://support.google.com/googleplay/android-developer
- **Content Rating Guide:** https://support.google.com/googleplay/android-developer/answer/9888179

## 📚 Document Reference

All submission documents are in the project root:
- `GOOGLE_PLAY_STORE_LISTING.md` - Store listing text
- `PRIVACY_POLICY.md` - Privacy policy (host online)
- `CONTENT_RATING_QUESTIONNAIRE.md` - Rating answers
- `STORE_LISTING_ASSETS.md` - Visual assets guide
- `SUBMISSION_CHECKLIST.md` - Detailed checklist
- `GOOGLE_PLAY_GUIDE.md` - Original guide (technical setup)

## 🎯 Next Steps

1. **Create visual assets** (icon, feature graphic, screenshots)
2. **Host privacy policy** online
3. **Build AAB** file
4. **Create Play Console account** (if not done)
5. **Submit app** using the checklist

## 💡 Tips

- Start with a **gradual rollout** (20%) to catch any issues
- Monitor **user reviews** and **crash reports** after launch
- Keep your **keystore file safe** - you'll need it for updates
- **Test thoroughly** before submitting
- **Review time** is typically 1-3 days

---

**Good luck with your submission!** 🎉
