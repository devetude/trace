package net.devetude.trace.handler

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.devetude.trace.R
import net.devetude.trace.activity.AddParkingHistoryActivity
import net.devetude.trace.common.extension.loadCircleCroppedBitmap
import net.devetude.trace.entity.Car
import net.devetude.trace.notification.TraceNotificationCreator
import net.devetude.trace.notification.TraceNotificationType.BLUETOOTH_CONNECTION_STATE_CHANGE
import net.devetude.trace.usecase.car.CarUseCase
import org.koin.core.inject

class BluetoothDisconnectedActionHandler : BluetoothActionHandler {
    private val carUseCase: CarUseCase by inject()

    override fun handle(context: Context, bluetoothDevice: BluetoothDevice) {
        GlobalScope.launch {
            carUseCase.selectCarsBy(bluetoothDevice.address).forEach {
                context.notifyNotification(it)
            }
        }
    }

    private suspend fun Context.notifyNotification(car: Car) = with(car) {
        val requestManager = Glide.with(this@notifyNotification /* context */)
        val largeIconBitmap = imagePath?.let { requestManager.loadCircleCroppedBitmap(it) }
        val intent = AddParkingHistoryActivity
            .createIntent(context = this@notifyNotification, carNumber = car.number)
            .addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        val notification = TraceNotificationCreator(context = this@notifyNotification)
            .create(
                BLUETOOTH_CONNECTION_STATE_CHANGE,
                getString(R.string.car_model_name_and_number, modelName, number),
                getString(R.string.bluetooth_disconnected_notification_text),
                largeIconBitmap,
                intent
            )
        NotificationManagerCompat
            .from(this@notifyNotification /* context */)
            .notify(car.number.hashCode(), notification)
    }
}
