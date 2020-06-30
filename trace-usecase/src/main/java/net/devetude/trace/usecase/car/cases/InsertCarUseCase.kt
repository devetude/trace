package net.devetude.trace.usecase.car.cases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.devetude.trace.db.repository.CarRepository
import net.devetude.trace.entity.Car

internal class InsertCarUseCase(private val carRepository: CarRepository) {
    suspend fun run(car: Car): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching { carRepository.insert(car) }
    }
}
