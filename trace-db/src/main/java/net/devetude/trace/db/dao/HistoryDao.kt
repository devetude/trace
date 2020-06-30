package net.devetude.trace.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.History
import net.devetude.trace.entity.HistoryWithCar

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: History)

    @Query(
        value = "SELECT a.*, b.* " +
            "FROM ${History.TABLE_NAME} AS a " +
            "INNER JOIN ${Car.TABLE_NAME} AS b " +
            "ON a.${History.CAR_NUMBER_COLUMN_NAME} = b.${Car.NUMBER_COLUMN_NAME} " +
            "ORDER BY a.${History.CREATED_DATE_COLUMN_NAME} DESC"
    )
    fun selectAllHistoriesWithCar(): DataSource.Factory<Int, HistoryWithCar>
}
