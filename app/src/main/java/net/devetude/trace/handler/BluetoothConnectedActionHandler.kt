package net.devetude.trace.handler

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.devetude.trace.R
import net.devetude.trace.activity.MainActivity
import net.devetude.trace.common.extension.getBooleanSharedPreference
import net.devetude.trace.common.extension.loadCircleCroppedBitmap
import net.devetude.trace.entity.Car
import net.devetude.trace.notification.TraceNotificationCreator
import net.devetude.trace.notification.TraceNotificationType.BLUETOOTH_CONNECTION_STATE_CHANGE
import net.devetude.trace.usecase.car.CarUseCase
import net.devetude.trace.usecase.history.HistoryUseCase
import org.koin.core.inject

class BluetoothConnectedActionHandler : BluetoothActionHandler {
    private val carUseCase: CarUseCase by inject()
    private val historyUseCase: HistoryUseCase by inject()

    override fun handle(context: Context, bluetoothDevice: BluetoothDevice) {
        GlobalScope.launch {
            for(car in carUseCase.selectCarsBy(bluetoothDevice.address)) {
                if (!context.shouldChangeToDrivingState()) {
                    context.notifyNotification(
                        car,
                        R.string.bluetooth_connected_notification_text
                    )
                    continue
                }
                if (historyUseCase.insert(car.toDrivingHistory()).isSuccess) {
                    context.notifyNotification(
                        car,
                        R.string.changed_to_driving_state_notification_text
                    )
                }
            }
        }
    }

    private fun Context.shouldChangeToDrivingState(): Boolean =
        getBooleanSharedPreference(R.string.change_to_driving_state_key, defaultValue = true)

    private suspend fun Context.notifyNotification(car: Car, @StringRes contentStringRes: Int) =
        with(car) {
            val requestManager = Glide.with(this@notifyNotification /* context */)
            val largeIconBitmap = imagePath?.let { requestManager.loadCircleCroppedBitmap(it) }
            val intent = MainActivity.createIntent(context = this@notifyNotification)
                .addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            val notification = TraceNotificationCreator(context = this@notifyNotification)
                .create(
                    BLUETOOTH_CONNECTION_STATE_CHANGE,
                    getString(R.string.car_model_name_and_number, modelName, number),
                    getString(contentStringRes),
                    largeIconBitmap,
                    intent
                )
            NotificationManagerCompat
                .from(this@notifyNotification /* context */)
                .notify(car.number.hashCode(), notification)
        }
}
