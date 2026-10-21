package com.example.airtimeloader

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Remembers which PINs were already tried and keeps a short on-screen log. */
object Store {
    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("airtime_loader", Context.MODE_PRIVATE)

    fun wasTried(ctx: Context, pin: String): Boolean =
        prefs(ctx).getStringSet("tried", emptySet())!!.contains(pin)

    fun markTried(ctx: Context, pin: String) {
        val tried = prefs(ctx).getStringSet("tried", emptySet())!!.toMutableSet()
        tried.add(pin)
        prefs(ctx).edit().putStringSet("tried", tried).apply()
    }

    fun log(ctx: Context, line: String) {
        val stamp = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val old = prefs(ctx).getString("log", "").orEmpty().lines()
        val lines = (listOf("$stamp  $line") + old).filter { it.isNotBlank() }.take(30)
        prefs(ctx).edit().putString("log", lines.joinToString("\n")).apply()
    }

    fun readLog(ctx: Context): String = prefs(ctx).getString("log", "").orEmpty()

    fun clear(ctx: Context) {
        prefs(ctx).edit().clear().apply()
    }
}
