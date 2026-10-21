package com.example.airtimeloader

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {

    private lateinit var logView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 3, pad, pad)
        }

        fun addButton(label: String, onClick: () -> Unit) {
            column.addView(Button(this).apply {
                text = label
                setOnClickListener { onClick() }
            })
        }

        column.addView(TextView(this).apply {
            text = "Redeems Safaricom Care giveaway PINs (*141*PIN#) when they arrive as WhatsApp notifications."
        })
        addButton("1. Allow notification access") {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
        addButton("2. Allow phone permission (USSD)") {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        }
        addButton("Reset tried-PIN list and log") {
            Store.clear(this)
            showLog()
        }

        logView = TextView(this).apply { setPadding(0, pad, 0, 0) }
        column.addView(logView)
        setContentView(ScrollView(this).apply { addView(column) })
    }

    override fun onResume() {
        super.onResume()
        showLog()
    }

    private fun showLog() {
        logView.text = Store.readLog(this).ifEmpty { "No activity yet." }
    }
}
