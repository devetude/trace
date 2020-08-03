package net.devetude.trace.usecase.car.cases

import androidx.lifecycle.LiveData
import net.devetude.trace.repository.CarRepository

internal class CountCarsUseCase(private val carRepository: CarRepository) {
    fun run(): LiveData<Int> = carRepository.carCount
}
