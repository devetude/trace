package net.devetude.trace.db.repository

import androidx.lifecycle.LiveData
import net.devetude.trace.db.dao.CarDao
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.CarWithLastHistory

class CarRepository(private val carDao: CarDao) {
    val carsWithLastHistory: LiveData<List<CarWithLastHistory>> = carDao.selectAllWithLastHistory()
    val carCount: LiveData<Int> = carDao.countCars()

    suspend fun selectCarsBy(pairedBluetoothDeviceAddress: String): List<Car> =
        carDao.selectCarsBy(pairedBluetoothDeviceAddress)

    suspend fun selectCarBy(number: String): Car? = carDao.selectCarBy(number)

    suspend fun selectAllCars(): List<Car> = carDao.selectAllCars()

    suspend fun insert(car: Car) = carDao.insert(car)

    suspend fun delete(car: Car) = carDao.delete(car)

    suspend fun update(car: Car) = carDao.update(car)
}
