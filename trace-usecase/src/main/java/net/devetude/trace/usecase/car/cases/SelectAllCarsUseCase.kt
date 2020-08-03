package net.devetude.trace.usecase.car.cases

import net.devetude.trace.entity.Car
import net.devetude.trace.repository.CarRepository

internal class SelectAllCarsUseCase(private val carRepository: CarRepository) {
    suspend fun run(): List<Car> = carRepository.selectAllCars()
}
