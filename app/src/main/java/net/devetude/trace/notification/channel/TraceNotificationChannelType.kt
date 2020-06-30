package net.devetude.trace.notification.channel

import android.app.Notification.VISIBILITY_PUBLIC
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import net.devetude.trace.R
import net.devetude.trace.common.annotation.NotificationImportance
import net.devetude.trace.common.annotation.NotificationPriority

enum class TraceNotificationChannelType(
    val id: String,
    @StringRes val nameStringRes: Int,
    @NotificationImportance val importance: Int,
    @NotificationPriority val priority: Int,
    @StringRes val descriptionStringRes: Int,
    val isLightEnabled: Boolean,
    @ColorRes val lightColorRes: Int,
    val isVibrationEnabled: Boolean,
    val vibrationPattern: Array<Long>,
    val lockScreenVisibility: Int
) {
    BLUETOOTH_CONNECTION_STATE_CHANGE_CHANNEL(
        id = "BLUETOOTH_CONNECTION_STATE_CHANGE_CHANNEL_ID",
        nameStringRes = R.string.bluetooth_connection_state_change_channel_title,
        importance = IMPORTANCE_HIGH,
        priority = PRIORITY_HIGH,
        descriptionStringRes = R.string.bluetooth_connection_state_change_channel_description,
        isLightEnabled = true,
        lightColorRes = R.color.colorAccent,
        isVibrationEnabled = true,
        vibrationPattern = arrayOf(100L, 200L, 100L, 200L),
        lockScreenVisibility = VISIBILITY_PUBLIC
    );
}
