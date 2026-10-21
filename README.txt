AIRTIME LOADER - Android source (Kotlin)
Not compiled or run by me (no Android SDK available), so expect to fix a small error or two.

BUILD
1. Android Studio -> New Project -> "Empty Views Activity".
   Language Kotlin, package com.example.airtimeloader, Minimum SDK API 26.
2. In app/src/main/java/com/example/airtimeloader/ : replace MainActivity.kt with
   the one here, and add PinListenerService.kt and Store.kt.
3. Open app/src/main/AndroidManifest.xml and add the lines from manifest-snippet.xml.
4. Plug in your phone (USB debugging on) and press Run.

SET UP ON THE PHONE
- Open the app. Tap button 1 and switch on notification access for "Airtime Loader".
  Tap button 2 and allow the phone permission.
- If Android says the setting is "restricted": Settings > Apps > Airtime Loader >
  three-dot menu > Allow restricted settings, then try again.
- In WhatsApp, open the Safaricom Care channel and make sure its bell is on.
- Set the app's battery usage to Unrestricted so Android doesn't kill the listener.
- The app uses the phone's default SIM for USSD (dual-SIM: make the Safaricom SIM the default for calls).

TWEAKS (top of PinListenerService.kt)
- SOURCE_WORD: text that must appear in the notification ("" = any WhatsApp notification)
- MAX_FAILED_PINS: give up after this many rejected PINs
- STOP_AFTER_FIRST_SUCCESS: stop once one PIN loads
- REJECTED: words that mark Safaricom's reply as a failed PIN. The app shows a log of
  every reply, so adjust this list to the real wording.

PRIVACY
- No internet permission. PINs and the log stay on the phone.
- Only WhatsApp notifications that mention SOURCE_WORD and contain a 16-digit number are used.
