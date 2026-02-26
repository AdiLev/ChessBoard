# Google Play Store Submission Checklist

## Pre-Submission Requirements

### ✅ Technical Requirements
- [x] App signed with release keystore
- [x] Target SDK 34 (Android 14)
- [x] Min SDK 24 (Android 7.0)
- [x] App builds successfully
- [x] No critical bugs
- [x] App tested on multiple devices/screen sizes

### ✅ Code Quality
- [x] No hardcoded sensitive information
- [x] Proper error handling
- [x] App doesn't crash on startup
- [x] All features tested and working

## Store Listing Requirements

### App Information
- [x] App name: "ChessBoard"
- [x] Short description (80 chars): Prepared
- [x] Full description (4000 chars): Prepared
- [x] App category: Games > Board
- [x] Content rating: Everyone

### Visual Assets
- [ ] App icon (512x512 PNG) - Export from existing icon
- [ ] Feature graphic (1024x500 PNG/JPG) - Needs creation
- [ ] Screenshots (min 2, recommended 4-8) - Needs creation
  - [ ] Phone screenshots
  - [ ] Tablet screenshots (optional but recommended)

### Legal & Policy
- [x] Privacy Policy document created
- [ ] Privacy Policy hosted online (need URL)
- [x] Content rating questionnaire completed

### App Details
- [x] Version code: 1
- [x] Version name: 1.0
- [x] Pricing: Free
- [x] In-app purchases: None
- [x] Ads: None

## Google Play Console Setup

### Account Setup
- [ ] Google Play Developer account created
- [ ] $25 registration fee paid
- [ ] Developer profile completed

### App Creation
- [ ] New app created in Play Console
- [ ] App name reserved
- [ ] Default language set (English)

### Store Listing
- [ ] App name entered
- [ ] Short description entered
- [ ] Full description entered
- [ ] App icon uploaded
- [ ] Feature graphic uploaded
- [ ] Screenshots uploaded
- [ ] Promotional text entered
- [ ] Category selected
- [ ] Tags/keywords added

### Content Rating
- [ ] Content rating questionnaire completed
- [ ] Rating received: Everyone

### Privacy & Security
- [ ] Privacy Policy URL entered
- [ ] Data safety section completed
- [ ] Permissions declared

### Pricing & Distribution
- [ ] App set to Free
- [ ] Countries selected for distribution
- [ ] Age restrictions set (if any)

### App Content
- [ ] Content rating completed
- [ ] Target audience defined
- [ ] News app status (if applicable)

## Build & Release

### Build Preparation
- [ ] Release AAB built (`build-release-aab.ps1`)
- [ ] AAB file verified and signed
- [ ] Version code incremented (if updating)
- [ ] Release notes prepared

### Testing
- [ ] Internal testing track (optional)
- [ ] Closed testing track (optional)
- [ ] Open testing track (optional)
- [ ] Production release ready

### Release
- [ ] AAB uploaded to Play Console
- [ ] Release notes added
- [ ] Rollout percentage set (start with 20% recommended)
- [ ] App submitted for review

## Post-Submission

### Review Process
- [ ] Review status monitored
- [ ] Any reviewer feedback addressed
- [ ] App approved for release

### Launch
- [ ] App published
- [ ] Store listing verified
- [ ] App accessible in Play Store
- [ ] Download and install tested

## Maintenance

### Updates
- [ ] Version code incremented for updates
- [ ] Version name updated
- [ ] Release notes prepared
- [ ] Changes tested before release

### Monitoring
- [ ] User reviews monitored
- [ ] Crash reports reviewed
- [ ] Performance metrics checked
- [ ] User feedback addressed

## Important Notes

1. **Keystore Security:** Keep your keystore file safe - you'll need it for all future updates
2. **Version Management:** Always increment versionCode for each release
3. **Testing:** Test thoroughly before submitting
4. **Privacy Policy:** Must be accessible via public URL
5. **Review Time:** Google Play review typically takes 1-3 days
6. **Rollout:** Start with gradual rollout (20%) to catch any issues

## Quick Reference

### Build AAB
```powershell
.\build-release-aab.ps1
```

### AAB Location
```
app\build\outputs\bundle\release\app-release.aab
```

### Privacy Policy Hosting Options
- GitHub Pages (free)
- Google Sites (free)
- Your own website
- Firebase Hosting (free tier)

### Support Resources
- Google Play Console Help: https://support.google.com/googleplay/android-developer
- Play Console: https://play.google.com/console
