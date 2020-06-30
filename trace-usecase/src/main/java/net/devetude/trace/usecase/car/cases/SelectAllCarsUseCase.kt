package net.devetude.trace.usecase.car.cases

import net.devetude.trace.db.repository.CarRepository
import net.devetude.trace.entity.Car

internal class SelectAllCarsUseCase(private val carRepository: CarRepository) {
    suspend fun run(): List<Car> = carRepository.selectAllCars()
}
