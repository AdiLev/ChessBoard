# Google Play Store Publishing Guide for ChessBoard

## Step 1: Create a Signing Keystore

You need to create a keystore file to sign your app. **Keep this file safe - you'll need it for all future updates!**

### Create Keystore (run this command):

```powershell
keytool -genkey -v -keystore chessboard-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias chessboard-key
```

**Important Information to Provide:**
- Password: Choose a strong password (save it securely!)
- Name, Organization, etc.: Your app/company details
- **Save the keystore file and password in a secure location**

## Step 2: Configure Signing in build.gradle

The signing configuration has been added to `app/build.gradle`. You need to:

1. Create a `keystore.properties` file in the project root with:
```
storePassword=YOUR_KEYSTORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=chessboard-key
storeFile=chessboard-release-key.jks
```

2. Replace `YOUR_KEYSTORE_PASSWORD` and `YOUR_KEY_PASSWORD` with your actual passwords

## Step 3: Build Android App Bundle (AAB)

Google Play Store prefers AAB format over APK. Build it with:

```powershell
gradlew bundleRelease
```

The AAB will be at: `app\build\outputs\bundle\release\app-release.aab`

## Step 4: Google Play Console Setup

### Prerequisites:
1. **Google Play Developer Account** ($25 one-time fee)
   - Sign up at: https://play.google.com/console/signup
   - Pay the registration fee

2. **App Information Needed:**
   - App name: "ChessBoard"
   - Short description (80 characters)
   - Full description (4000 characters)
   - App icon (512x512 PNG)
   - Feature graphic (1024x500 PNG)
   - Screenshots (at least 2, up to 8)
   - Privacy Policy URL (required for most apps)

3. **Content Rating:**
   - Complete content rating questionnaire
   - Chess games are typically rated "Everyone"

4. **Target Audience:**
   - Age group
   - Content guidelines compliance

## Step 5: Upload to Google Play Console

1. Go to Google Play Console: https://play.google.com/console
2. Create a new app
3. Fill in all required information
4. Upload the AAB file from Step 3
5. Complete store listing
6. Set pricing (Free or Paid)
7. Submit for review

## Step 6: App Requirements Checklist

- [x] App is signed with release keystore
- [x] Target SDK 34 (Android 14)
- [x] Min SDK 24 (Android 7.0)
- [ ] Privacy Policy URL (if app collects data)
- [ ] App icon (512x512)
- [ ] Screenshots (at least 2)
- [ ] Feature graphic (1024x500)
- [ ] App description
- [ ] Content rating completed

## Step 7: Testing

Before publishing:
- Test on multiple devices
- Test all features (moves, promotion, castling, etc.)
- Test fullscreen mode
- Test on different screen sizes

## Important Notes:

1. **Keystore Security:**
   - Never commit keystore files to git
   - Keep backups in multiple secure locations
   - If you lose the keystore, you cannot update your app!

2. **Version Management:**
   - Each update needs a new `versionCode` (increment by 1)
   - Update `versionName` for user-facing version (e.g., "1.0", "1.1")

3. **Privacy Policy:**
   - Required if app collects any user data
   - Even if not collecting data, it's recommended to have one
   - Can host on GitHub Pages, Google Sites, etc.

4. **App Size:**
   - Current release APK: ~12.4 MB
   - AAB will be smaller (Google Play optimizes per device)

## Quick Commands:

```powershell
# Build release AAB (for Google Play)
gradlew bundleRelease

# Build release APK (for direct distribution)
gradlew assembleRelease

# Sign APK manually (if needed)
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore chessboard-release-key.jks app-release-unsigned.apk chessboard-key
```

