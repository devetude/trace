package net.devetude.trace.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import net.devetude.trace.entity.Car.Companion.NUMBER_COLUMN_NAME
import net.devetude.trace.entity.History.Companion.CAR_NUMBER_COLUMN_NAME
import net.devetude.trace.entity.History.Companion.TABLE_NAME
import java.util.Date

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Car::class,
            parentColumns = [NUMBER_COLUMN_NAME],
            childColumns = [CAR_NUMBER_COLUMN_NAME],
            onDelete = CASCADE
        )
    ]
)
open class History(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_COLUMN_NAME) val id: Long,
    @ColumnInfo(name = CAR_NUMBER_COLUMN_NAME, index = true) open val carNumber: String,
    @ColumnInfo(name = IS_PARKING_STATE_COLUMN_NAME, index = true) val isParkingState: Boolean,
    @ColumnInfo(name = IMAGE_PATH_COLUMN_NAME) open val imagePath: String?,
    @Embedded(prefix = PARKING_LOCATION_PREFIX) open val parkingLocation: Location?,
    @ColumnInfo(name = CREATED_DATE_COLUMN_NAME) val createdDate: Date
) {
    data class ParkingHistory(
        override val carNumber: String,
        override val imagePath: String?,
        override val parkingLocation: Location?
    ) : History(
        id = 0,
        carNumber = carNumber,
        isParkingState = true,
        imagePath = imagePath,
        parkingLocation = parkingLocation,
        createdDate = Date()
    )

    data class DrivingHistory(
        override val carNumber: String
    ) : History(
        id = 0,
        carNumber = carNumber,
        isParkingState = false,
        imagePath = null,
        parkingLocation = null,
        createdDate = Date()
    )

    override fun hashCode(): Int = 31 * History::class.java.hashCode() + id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is History) return false
        return id == other.id &&
            carNumber == other.carNumber &&
            isParkingState == other.isParkingState &&
            imagePath == other.imagePath &&
            parkingLocation == other.parkingLocation &&
            createdDate == other.createdDate
    }

    companion object {
        const val TABLE_NAME: String = "history"

        private const val COLUMN_NAME_PREFIX = "h_"
        private const val PARKING_LOCATION_PREFIX = "${COLUMN_NAME_PREFIX}parking_location_"

        private const val ID_COLUMN_NAME = "${COLUMN_NAME_PREFIX}id"
        const val CAR_NUMBER_COLUMN_NAME = "${COLUMN_NAME_PREFIX}car_number"
        private const val IS_PARKING_STATE_COLUMN_NAME =
            "${COLUMN_NAME_PREFIX}is_parking_state"
        private const val IMAGE_PATH_COLUMN_NAME = "${COLUMN_NAME_PREFIX}image_path"
        const val CREATED_DATE_COLUMN_NAME = "${COLUMN_NAME_PREFIX}created_date"
    }
}
