package net.devetude.trace.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.devetude.trace.entity.Car.Companion.TABLE_NAME
import net.devetude.trace.entity.History.DrivingHistory

@Entity(tableName = TABLE_NAME)
data class Car(
    @PrimaryKey @ColumnInfo(name = NUMBER_COLUMN_NAME) val number: String,
    @ColumnInfo(name = MODEL_COLUMN_NAME, index = true) val modelName: String,
    @ColumnInfo(name = IMAGE_PATH_COLUMN_NAME) val imagePath: String?,
    @Embedded(prefix = COLUMN_NAME_PREFIX) val pairedBluetoothDevice: PairedBluetoothDevice?
) {
    fun toDrivingHistory(): DrivingHistory = DrivingHistory(number)

    companion object {
        const val TABLE_NAME = "car"

        const val COLUMN_NAME_PREFIX = "c_"

        const val NUMBER_COLUMN_NAME = "${COLUMN_NAME_PREFIX}number"
        private const val MODEL_COLUMN_NAME = "${COLUMN_NAME_PREFIX}model_name"
        private const val IMAGE_PATH_COLUMN_NAME = "${COLUMN_NAME_PREFIX}image_path"
    }
}
