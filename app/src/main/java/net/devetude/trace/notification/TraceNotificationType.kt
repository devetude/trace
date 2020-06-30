package net.devetude.trace.notification

import androidx.annotation.DrawableRes
import net.devetude.trace.R
import net.devetude.trace.notification.channel.TraceNotificationChannelType
import net.devetude.trace.notification.channel.TraceNotificationChannelType.BLUETOOTH_CONNECTION_STATE_CHANGE_CHANNEL

enum class TraceNotificationType(
    val channelType: TraceNotificationChannelType,
    @DrawableRes val smallIconDrawableRes: Int,
    val isAutoCancelable: Boolean
) {
    BLUETOOTH_CONNECTION_STATE_CHANGE(
        channelType = BLUETOOTH_CONNECTION_STATE_CHANGE_CHANNEL,
        smallIconDrawableRes = R.drawable.ic_notification_app_logo,
        isAutoCancelable = true
    )
}
