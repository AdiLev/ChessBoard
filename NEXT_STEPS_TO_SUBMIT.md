# Next Steps to Submit ChessBoard to Google Play Store

## ✅ What You Already Have Ready

- ✅ **App Icon** (512x512) - `google-play-assets/icon/app-icon-512x512.png`
- ✅ **Feature Graphic** (1024x1024) - `google-play-assets/feature-graphic/feature-graphic-1024x1024.png`
- ✅ **Screenshots** (2 files) - `google-play-assets/screenshots/`
- ✅ **Store Listing Text** - `GOOGLE_PLAY_STORE_LISTING.md` and `PLAY_CONSOLE_COPY_PASTE.md`
- ✅ **Privacy Policy** - `PRIVACY_POLICY.md`
- ✅ **Content Rating Answers** - `CONTENT_RATING_QUESTIONNAIRE.md`
- ✅ **Signed Release Build Script** - `build-release-aab.ps1`

## 🚀 Step-by-Step Action Plan

### Step 1: Host Your Privacy Policy Online (REQUIRED)

**Why:** Google Play requires a publicly accessible URL for your privacy policy.

**Options:**

**Option A: GitHub Pages (Recommended - Free & Easy)**
1. Create a new file: `docs/privacy-policy.md` in your repository
2. Copy content from `PRIVACY_POLICY.md` to `docs/privacy-policy.md`
3. Go to your GitHub repo → Settings → Pages
4. Enable GitHub Pages, select "main" branch and "/docs" folder
5. Your URL will be: `https://[your-username].github.io/ChessBoard/privacy-policy`
6. Wait 5-10 minutes for it to be live

**Option B: Google Sites (Free)**
1. Go to https://sites.google.com
2. Create a new site
3. Copy/paste your privacy policy content
4. Publish and get the URL

**Option C: Your Own Website**
- Upload `PRIVACY_POLICY.md` to your website
- Make sure it's publicly accessible

**Save this URL** - you'll need it in Step 4!

---

### Step 2: Build Your Release AAB File

**Run this command:**
```powershell
.\build-release-aab.ps1
```

**Verify the build:**
- Check that `app\build\outputs\bundle\release\app-release.aab` exists
- File should be ~500KB - 2MB in size
- Make sure it's signed (the script handles this)

**Note:** Keep your keystore file (`chessboard-release-key.jks`) safe - you'll need it for all future updates!

---

### Step 3: Create Google Play Developer Account

**If you don't have one yet:**

1. Go to: https://play.google.com/console/signup
2. Sign in with your Google account
3. Pay the **$25 one-time registration fee** (credit card required)
4. Complete your developer profile:
   - Developer name
   - Email address
   - Phone number
   - Address

**This is a one-time fee** - you can publish unlimited apps after this.

---

### Step 4: Create Your App in Play Console

**⚠️ IMPORTANT: If you want a PAID app, see `PAID_APP_SETUP_GUIDE.md` first!**

1. **Go to:** https://play.google.com/console
2. **Click:** "Create app" button
3. **Fill in:**
   - **App name:** `ChessBoard`
   - **Default language:** English (United States)
   - **App or game:** Game
   - **Free or paid:** 
     - **Free** (default) - No payment setup needed
     - **Paid** - Requires merchant account setup (see `PAID_APP_SETUP_GUIDE.md`)
   - **Declare:** Check "No" for ads and in-app purchases
4. **Click:** "Create app"

---

### Step 5: Complete Store Listing

**Go to:** Play Console → Your App → Store presence → Main store listing

**Copy text from:** `PLAY_CONSOLE_COPY_PASTE.md` (it's all ready!)

**Fill in:**

1. **App name:** `ChessBoard`

2. **Short description (80 chars):**
   ```
   Classic chess game with move history, castling, and save/load features
   ```

3. **Full description:** 
   - Open `PLAY_CONSOLE_COPY_PASTE.md`
   - Copy the entire "Full Description" section
   - Paste into Play Console

4. **App icon:**
   - Upload: `google-play-assets/icon/app-icon-512x512.png`

5. **Feature graphic:**
   - Upload: `google-play-assets/feature-graphic/feature-graphic-1024x1024.png`
   - Note: Play Store prefers 1024x500, but 1024x1024 should work

6. **Screenshots:**
   - Upload all files from `google-play-assets/screenshots/`
   - Minimum 2 required (you have 2)
   - Add more if you want (recommended: 4-8)

7. **Promotional text (optional):**
   ```
   Play classic chess with move history, castling, and save/load features
   ```

8. **Category:**
   - Select: **Games** → **Board**

9. **Tags:**
   ```
   chess, board game, strategy, classic, moves, castling, promotion, save game
   ```

10. **Contact details:**
    - Fill in your email, website (if any), support URL

11. **Privacy Policy:**
    - Enter the URL from Step 1

**Click:** "Save draft" when done

---

### Step 6: Complete Content Rating

**Go to:** Play Console → Your App → Policy → App content → Content rating

**Answer the questionnaire:**
- Open `CONTENT_RATING_QUESTIONNAIRE.md` or `PLAY_CONSOLE_COPY_PASTE.md`
- Answer all questions (all should be "No" for your app)
- Expected rating: **Everyone (E)**

**This is required** - you can't submit without it!

---

### Step 7: Complete Data Safety Section

**Go to:** Play Console → Your App → Policy → App content → Data safety

**Fill in:**

1. **Does your app collect or share user data?**
   - Select: **No**

2. **Data types:**
   - Select: **None** (no data collected)

3. **Data security:**
   - All data stored locally on device
   - No data transmission

4. **Data deletion:**
   - Users can delete by uninstalling app
   - No account required

**Save** when done.

---

### Step 8: Upload Your AAB File

**Go to:** Play Console → Your App → Release → Production (or Testing)

**For first release, use Production:**

1. **Click:** "Create new release"
2. **Upload:** `app\build\outputs\bundle\release\app-release.aab`
3. **Release name:** `1.0`
4. **Release notes:**
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
5. **Review release** - make sure everything looks correct
6. **Save** the release

---

### Step 9: Set Pricing & Distribution

**Go to:** Play Console → Your App → Pricing & distribution

**Fill in:**

1. **Countries/regions:**
   - Select: **All countries** (or specific ones if preferred)

2. **Price:**
   - **Free:** Already set (from Step 4)
   - **Paid:** Set price tier ($0.99, $1.99, etc.) - Requires merchant account (see `PAID_APP_SETUP_GUIDE.md`)

3. **Device categories:**
   - Phone
   - Tablet (optional)

4. **Content guidelines:**
   - Check all boxes confirming compliance

5. **US export laws:**
   - Check compliance box

**Save** when done.

---

### Step 10: Review & Submit

**Go to:** Play Console → Your App → Release → Production

**Before submitting:**

✅ **Checklist:**
- [ ] Store listing complete
- [ ] App icon uploaded
- [ ] Feature graphic uploaded
- [ ] Screenshots uploaded (minimum 2)
- [ ] Privacy policy URL added
- [ ] Content rating completed
- [ ] Data safety completed
- [ ] AAB file uploaded
- [ ] Release notes added
- [ ] Pricing & distribution set

**Submit:**

1. **Click:** "Review release" or "Start rollout to Production"
2. **Select rollout percentage:** Start with **20%** (recommended)
3. **Click:** "Start rollout to Production"
4. **Confirm** submission

**You're done!** 🎉

---

### Step 11: Wait for Review

**What happens next:**

- **Review time:** Typically 1-3 days
- **Status:** Check Play Console → Release → Production
- **You'll get an email** when review is complete

**Possible outcomes:**

✅ **Approved:** Your app goes live!
❌ **Rejected:** Google will tell you what to fix
⚠️ **More info needed:** Respond to their questions

---

### Step 12: After Approval

**Once approved:**

1. **Monitor:** Check Play Console for any issues
2. **Gradual rollout:** If you started at 20%, increase to 100% after a few days
3. **Monitor reviews:** Check user feedback
4. **Check crash reports:** Fix any issues

**To increase rollout:**

- Go to Play Console → Release → Production
- Click on your release
- Increase rollout percentage
- Save

---

## 💰 Want to Make It a Paid App?

**See `PAID_APP_SETUP_GUIDE.md` for complete instructions on:**
- Setting up Google Play Merchant Account
- Payment processing setup
- Tax information requirements
- Pricing strategy
- Revenue expectations
- Alternative freemium models

**Key Requirements for Paid Apps:**
- Merchant account setup (1-3 days verification)
- Tax information (TIN/EIN/VAT)
- Bank account for payouts
- Price selection ($0.99 minimum)

---

## 📋 Quick Reference

### File Locations

- **AAB file:** `app\build\outputs\bundle\release\app-release.aab`
- **App icon:** `google-play-assets/icon/app-icon-512x512.png`
- **Feature graphic:** `google-play-assets/feature-graphic/feature-graphic-1024x1024.png`
- **Screenshots:** `google-play-assets/screenshots/`
- **Store text:** `PLAY_CONSOLE_COPY_PASTE.md`
- **Privacy policy:** `PRIVACY_POLICY.md`
- **Paid app guide:** `PAID_APP_SETUP_GUIDE.md`

### Important Links

- **Play Console:** https://play.google.com/console
- **Developer Signup:** https://play.google.com/console/signup
- **Help Center:** https://support.google.com/googleplay/android-developer

### Build Commands

```powershell
# Build release AAB
.\build-release-aab.ps1

# Build release APK (if needed)
.\build-release-apk.ps1
```

---

## ⚠️ Important Notes

1. **Keystore Security:** Keep `chessboard-release-key.jks` safe - you'll need it for ALL future updates!

2. **Version Management:** 
   - Current: Version Code 1, Version Name 1.0
   - For updates: Increment version code (2, 3, 4...) and update version name

3. **Privacy Policy:** Must be publicly accessible - GitHub Pages is easiest

4. **Review Time:** Usually 1-3 days, but can take up to 7 days

5. **Gradual Rollout:** Start with 20% to catch issues before full release

6. **Testing:** Consider using "Internal testing" track first to test the upload process

---

## 🆘 Need Help?

- **Play Console Help:** https://support.google.com/googleplay/android-developer
- **Check your documents:** All answers are in `PLAY_CONSOLE_COPY_PASTE.md`
- **Common issues:** See `GOOGLE_PLAY_SUBMISSION_GUIDE.md`

---

## ✅ Final Checklist Before Submitting

- [ ] Privacy policy hosted online (have URL ready)
- [ ] AAB file built and verified
- [ ] Google Play Developer account created ($25 paid)
- [ ] App created in Play Console
- [ ] Store listing complete (all text filled in)
- [ ] Visual assets uploaded (icon, feature graphic, screenshots)
- [ ] Content rating completed
- [ ] Data safety section completed
- [ ] AAB file uploaded
- [ ] Release notes added
- [ ] Pricing & distribution set
- [ ] Ready to submit!

**Good luck with your submission!** 🎉
