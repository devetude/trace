package net.devetude.trace.entity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.room.ColumnInfo

data class PairedBluetoothDevice(
    @ColumnInfo(name = ADDRESS_COLUMN_NAME, index = true) val address: String,
    @ColumnInfo(name = NAME_COLUMN_NAME, index = true) val name: String
) {
    companion object {
        const val ADDRESS_COLUMN_NAME: String = "paired_bluetooth_device_address"
        private const val NAME_COLUMN_NAME: String = "paired_bluetooth_device_name"

        @SuppressLint(value = ["MissingPermission"])
        fun of(bluetoothDevice: BluetoothDevice): PairedBluetoothDevice = with(bluetoothDevice) {
            PairedBluetoothDevice(address, name)
        }
    }
}
