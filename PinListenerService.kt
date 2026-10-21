package com.example.airtimeloader

import android.Manifest
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

/**
 * Reads WhatsApp notifications that mention SOURCE_WORD, picks out 16-digit airtime PINs
 * and redeems them with the Safaricom top-up code *141*PIN#.
 * The app has no INTERNET permission, so nothing leaves the phone.
 */
class PinListenerService : NotificationListenerService() {

    private companion object {
        // ---- tweak these ----
        const val SOURCE_WORD = "safaricom"         // must appear in the notification; "" = any WhatsApp notification
        const val MAX_FAILED_PINS = 3               // stop after this many rejected PINs in one run
        const val STOP_AFTER_FIRST_SUCCESS = true   // false = keep going and use every valid PIN

        // Words in Safaricom's reply that mean "PIN rejected". These are guesses:
        // check the in-app log and adjust them to the real reply texts.
        val REJECTED = Regex(
            "invalid|incorrect|not correct|wrong|already|been used|expired|not valid|exceeded|blocked|unable|fail|error|try again",
            RegexOption.IGNORE_CASE
        )

        val WHATSAPP = setOf("com.whatsapp", "com.whatsapp.w4b")
        val PIN = Regex("""(?<!\d)(\d{4})[ \u00A0-]?(\d{4})[ \u00A0-]?(\d{4})[ \u00A0-]?(\d{4})(?!\d)""")
    }

    private val main = Handler(Looper.getMainLooper())
    private val queue = ArrayDeque<String>()
    private var current: String? = null
    private var failures = 0

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in WHATSAPP) return
        val extras = sbn.notification.extras
        val text = listOfNotNull(
            extras.getCharSequence(Notification.EXTRA_TITLE),
            extras.getCharSequence(Notification.EXTRA_TEXT),
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT),
            extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString("\n")
        ).joinToString("\n")
        if (!text.contains(SOURCE_WORD, ignoreCase = true)) return

        val fresh = PIN.findAll(text)
            .map { m -> m.groupValues.drop(1).joinToString("") }
            .distinct()
            .filter { !Store.wasTried(this, it) && it != current && it !in queue }
            .toList()
        if (fresh.isEmpty()) return

        queue.addAll(fresh)
        if (current == null) {
            failures = 0
            redeemNext()
        }
    }

    private fun redeemNext() {
        val pin = queue.removeFirstOrNull()
        if (pin == null) { current = null; return }
        current = pin
        val tag = "…" + pin.takeLast(4)   // PINs are masked in the log

        val allowed = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
            PackageManager.PERMISSION_GRANTED
        if (!allowed) {
            Store.log(this, "$tag skipped: phone permission not granted")
            endRun(); return
        }
        val tm = getSystemService(TelephonyManager::class.java)
        if (tm == null) { endRun(); return }

        try {
            tm.sendUssdRequest("*141*$pin#", object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(t: TelephonyManager, request: String, response: CharSequence) {
                    Store.markTried(this@PinListenerService, pin)
                    Store.log(this@PinListenerService, "$tag: " + response.toString().replace('\n', ' ').take(110))
                    val rejected = REJECTED.containsMatchIn(response)
                    if (rejected) failures++
                    val done = (!rejected && STOP_AFTER_FIRST_SUCCESS) || failures >= MAX_FAILED_PINS
                    if (done) endRun() else redeemNext()
                }

                override fun onReceiveUssdResponseFailed(t: TelephonyManager, request: String, failureCode: Int) {
                    Store.log(this@PinListenerService, "$tag: USSD error $failureCode")
                    endRun()
                }
            }, main)
        } catch (e: Exception) {
            Store.log(this, "$tag: ${e.javaClass.simpleName}")
            endRun()
        }
    }

    private fun endRun() {
        queue.clear()
        current = null
    }
}
