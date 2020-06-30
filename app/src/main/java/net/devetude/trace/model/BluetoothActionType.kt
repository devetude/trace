package net.devetude.trace.model

import android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED
import android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED
import net.devetude.trace.handler.BluetoothActionHandler
import net.devetude.trace.handler.BluetoothConnectedActionHandler
import net.devetude.trace.handler.BluetoothDisconnectedActionHandler

enum class BluetoothActionType(
    val intentAction: String,
    val handler: BluetoothActionHandler
) {
    CONNECTED(ACTION_ACL_CONNECTED, BluetoothConnectedActionHandler()),
    DISCONNECTED(ACTION_ACL_DISCONNECTED, BluetoothDisconnectedActionHandler());

    companion object {
        private val INTENT_ACTION_LOOKUP: Map<String, BluetoothActionType> by lazy {
            values().associateBy { it.intentAction }
        }

        fun of(intentAction: String): BluetoothActionType = INTENT_ACTION_LOOKUP[intentAction]
            ?: error("Undefined intentAction=$intentAction")
    }
}
