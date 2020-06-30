package net.devetude.trace.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_TIME_TICK

class TimeTickReceiver(private inline val onTimeTickReceived: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TIME_TICK) return
        onTimeTickReceived()
    }
}
