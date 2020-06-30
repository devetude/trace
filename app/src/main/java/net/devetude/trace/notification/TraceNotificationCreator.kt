package net.devetude.trace.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat

class TraceNotificationCreator(private val context: Context) {
    fun create(
        type: TraceNotificationType,
        contentTitle: String = "",
        contentText: String = "",
        largeIconBitmap: Bitmap? = null,
        intent: Intent? = null
    ): Notification = NotificationCompat.Builder(context, type.channelType.id)
        .apply {
            setSmallIcon(type.smallIconDrawableRes)
            setAutoCancel(type.isAutoCancelable)
            priority = type.channelType.importance
            if (contentTitle.isNotBlank()) setContentTitle(contentTitle)
            if (contentText.isNotBlank()) setContentText(contentText)
            largeIconBitmap?.let(::setLargeIcon)
            intent.toPendingIntent()?.let(::setContentIntent)
        }
        .build()

    private fun Intent?.toPendingIntent(): PendingIntent? {
        this ?: return null
        return PendingIntent.getActivity(
            context,
            NOTIFICATION_REQ_CODE,
            this /* intent */,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val NOTIFICATION_REQ_CODE = 0
    }
}
