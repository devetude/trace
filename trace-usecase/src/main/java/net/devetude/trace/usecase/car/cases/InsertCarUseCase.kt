package net.devetude.trace.usecase.car.cases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.devetude.trace.entity.Car
import net.devetude.trace.repository.CarRepository

internal class InsertCarUseCase(private val carRepository: CarRepository) {
    suspend fun run(car: Car): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching { carRepository.insert(car) }
    }
}
