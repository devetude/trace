package net.devetude.trace.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.devetude.trace.model.BluetoothActionType

class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intentAction = intent.action ?: return
        val bluetoothDevice = intent.getBluetoothDevice() ?: return

        BluetoothActionType.of(intentAction).handler.handle(context, bluetoothDevice)
    }

    private fun Intent.getBluetoothDevice(): BluetoothDevice? =
        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
}
