package net.devetude.trace.entity

import androidx.room.ColumnInfo
import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude

data class Location(
    @ColumnInfo(name = LATITUDE_COLUMN_NAME) @Latitude val latitude: Double? = null,
    @ColumnInfo(name = LONGITUDE_COLUMN_NAME) @Longitude val longitude: Double? = null,
    @ColumnInfo(name = ADDRESS_COLUMN_NAME) val address: String? = null,
    @ColumnInfo(name = FLOOR_COLUMN_NAME) val floor: Int = 0,
    @ColumnInfo(name = SPACE_COLUMN_NAME) val space: String? = null
) {
    companion object {
        private const val LATITUDE_COLUMN_NAME: String = "latitude"
        private const val LONGITUDE_COLUMN_NAME: String = "longitude"
        private const val ADDRESS_COLUMN_NAME: String = "address"
        private const val FLOOR_COLUMN_NAME: String = "floor"
        private const val SPACE_COLUMN_NAME: String = "space"
    }
}
