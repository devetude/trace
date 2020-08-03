package net.devetude.trace.usecase.car.cases

import net.devetude.trace.entity.Car
import net.devetude.trace.repository.CarRepository

internal class SelectCarByNumberUseCase(private val carRepository: CarRepository) {
    suspend fun run(number: String): Car? = carRepository.selectCarBy(number)
}
