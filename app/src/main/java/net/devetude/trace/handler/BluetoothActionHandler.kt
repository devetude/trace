package net.devetude.trace.handler

import android.bluetooth.BluetoothDevice
import android.content.Context
import org.koin.core.KoinComponent

interface BluetoothActionHandler : KoinComponent {
    fun handle(context: Context, bluetoothDevice: BluetoothDevice)
}
