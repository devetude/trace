package net.devetude.trace.usecase.car.cases

import net.devetude.trace.db.repository.CarRepository
import net.devetude.trace.entity.Car

internal class UpdateCar(private val carRepository: CarRepository) {
    suspend fun run(car: Car): Result<Unit> = runCatching { carRepository.update(car) }
}
