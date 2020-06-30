package net.devetude.trace.notification.channel

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import net.devetude.trace.common.extension.notificationManager

class TraceNotificationChannelCreator(private val context: Context) {
    fun maybeCreateChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channels = TraceNotificationChannelType.values().map { it.toNotificationChannel() }
        context.notificationManager.createNotificationChannels(channels)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun TraceNotificationChannelType.toNotificationChannel(): NotificationChannel =
        NotificationChannel(id, context.getString(nameStringRes), importance).apply {
            description = context.getString(descriptionStringRes)
            enableLights(isLightEnabled)
            lightColor = context.getColor(lightColorRes)
            enableVibration(isVibrationEnabled)
            vibrationPattern = this@toNotificationChannel.vibrationPattern.toLongArray()
            lockscreenVisibility = lockScreenVisibility
        }
}
