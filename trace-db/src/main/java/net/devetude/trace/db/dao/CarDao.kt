package net.devetude.trace.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.CarWithLastHistory
import net.devetude.trace.entity.History
import net.devetude.trace.entity.PairedBluetoothDevice

@Dao
interface CarDao {
    @Query(
        value = "SELECT a.*, b.* " +
            "FROM ${Car.TABLE_NAME} AS a " +
            "LEFT JOIN ${History.TABLE_NAME} AS b " +
            "ON a.${Car.NUMBER_COLUMN_NAME} = b.${History.CAR_NUMBER_COLUMN_NAME} " +
            "AND b.${History.CREATED_DATE_COLUMN_NAME} = (" +
            "SELECT max(${History.CREATED_DATE_COLUMN_NAME}) " +
            "FROM ${History.TABLE_NAME} " +
            "WHERE ${History.CAR_NUMBER_COLUMN_NAME} = a.${Car.NUMBER_COLUMN_NAME})"
    )
    fun selectAllWithLastHistory(): LiveData<List<CarWithLastHistory>>

    @Query(
        value = "SELECT * " +
            "FROM ${Car.TABLE_NAME} " +
            "WHERE ${Car.COLUMN_NAME_PREFIX}${PairedBluetoothDevice.ADDRESS_COLUMN_NAME} " +
            "= :pairedBluetoothDeviceAddress"
    )
    suspend fun selectCarsBy(pairedBluetoothDeviceAddress: String): List<Car>

    @Query(value = "SELECT * FROM ${Car.TABLE_NAME} WHERE ${Car.NUMBER_COLUMN_NAME} = :number")
    suspend fun selectCarBy(number: String): Car?

    @Query(value = "SELECT * FROM ${Car.TABLE_NAME}")
    suspend fun selectAllCars(): List<Car>

    @Insert
    suspend fun insert(car: Car)

    @Delete
    suspend fun delete(car: Car)

    @Update
    suspend fun update(car: Car)

    @Query(value = "SELECT COUNT(*) FROM ${Car.TABLE_NAME}")
    fun countCars(): LiveData<Int>
}
