# Airtime Loader (Android Studio project)

A small Kotlin Android app that detects possible 16-digit voucher codes in WhatsApp notifications and lets you choose whether to send each one to Safaricom's `*141*PIN#` USSD service.

## Important behaviour
- **No automatic redemption.** A confirmation dialog is shown for each code.
- Codes are only extracted from WhatsApp / WhatsApp Business notifications that mention “Safaricom” or “airtime”. Android notification previews must include the message text for detection to work.
- Codes and activity log stay in local app storage. The app does not request internet access.
- A USSD response is not a guarantee that airtime was credited. Verify the message and airtime balance with Safaricom.
- Use only genuine giveaway vouchers intended for public redemption. Repeated attempts can fail, and Android/carrier support for USSD APIs varies by device.

## Build the APK
1. Install Android Studio on a computer.
2. Extract this ZIP.
3. In Android Studio, choose **Open** and select the `AirtimeLoader` folder (the one containing `settings.gradle.kts`).
4. Allow Gradle sync to finish. Internet access may be needed the first time to download Android Gradle Plugin dependencies.
5. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Android Studio shows a notification with **Locate**. The debug APK is normally at `app/build/outputs/apk/debug/app-debug.apk`.

## Set up on your phone
1. Install the APK. If installing outside Play Store, Android may ask you to allow installs from that source.
2. Open Airtime Loader and tap **Enable notification access**. Enable access for Airtime Loader in Android Settings.
3. Tap **Allow phone permission** and grant the permission when prompted.
4. Ensure WhatsApp channel notifications display message previews; Android may not expose the full text of channel posts if previews are hidden.
5. On dual-SIM phones, select the Safaricom SIM as the default SIM for calls/USSD where your device provides that setting.
6. When codes appear, open the app, tap **Refresh detected codes**, then review and confirm only the code you want to try.

## Troubleshooting
- If no codes appear, ensure WhatsApp notifications are enabled and message previews are visible. Some WhatsApp channel notifications do not include full message text, so detection may not work for every post.
- USSD API support varies by Android version, carrier and handset. If the request fails, use the phone dialer manually instead.
- This source project has not been compiled or tested on a physical phone in this environment because Android SDK/Gradle are unavailable here.


## Build notes

This project requires Android Studio, a compatible JDK (JDK 17 is recommended for Android Gradle Plugin 8.7.x), Android SDK Platform 35, and internet access for the first Gradle dependency download. Open this folder in Android Studio and let Gradle sync before selecting **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

The source archive supplied here has not been compiled in this environment: Android SDK/Gradle are not installed here and outbound network access is unavailable. Do not treat this ZIP as an APK.
