package net.devetude.trace.usecase.car

import androidx.lifecycle.LiveData
import net.devetude.trace.db.repository.CarRepository
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.CarWithLastHistory
import net.devetude.trace.usecase.car.cases.CountCarsUseCase
import net.devetude.trace.usecase.car.cases.DeleteCar
import net.devetude.trace.usecase.car.cases.InsertCarUseCase
import net.devetude.trace.usecase.car.cases.SelectAllCarsUseCase
import net.devetude.trace.usecase.car.cases.SelectAllCarsWithLastHistoryUseCase
import net.devetude.trace.usecase.car.cases.SelectCarByNumberUseCase
import net.devetude.trace.usecase.car.cases.SelectCarsByPairedBluetoothDeviceAddressUseCase
import net.devetude.trace.usecase.car.cases.UpdateCar

class CarUseCase(private val carRepository: CarRepository) {
    fun selectAllCarsWithLastHistory(): LiveData<List<CarWithLastHistory>> =
        SelectAllCarsWithLastHistoryUseCase(carRepository).run()

    suspend fun selectCarsBy(pairedBluetoothDeviceAddress: String): List<Car> =
        SelectCarsByPairedBluetoothDeviceAddressUseCase(carRepository)
            .run(pairedBluetoothDeviceAddress)

    suspend fun selectCarBy(number: String): Car? =
        SelectCarByNumberUseCase(carRepository).run(number)

    suspend fun selectAllCars(): List<Car> = SelectAllCarsUseCase(carRepository).run()

    suspend fun insert(car: Car): Result<Unit> = InsertCarUseCase(carRepository).run(car)

    suspend fun delete(car: Car): Result<Unit> = DeleteCar(carRepository).run(car)

    suspend fun update(car: Car): Result<Unit> = UpdateCar(carRepository).run(car)

    fun countCars(): LiveData<Int> = CountCarsUseCase(carRepository).run()
}
