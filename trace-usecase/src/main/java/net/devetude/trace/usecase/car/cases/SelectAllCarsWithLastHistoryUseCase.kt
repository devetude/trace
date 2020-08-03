package net.devetude.trace.usecase.car.cases

import androidx.lifecycle.LiveData
import net.devetude.trace.entity.CarWithLastHistory
import net.devetude.trace.repository.CarRepository

internal class SelectAllCarsWithLastHistoryUseCase(private val carRepository: CarRepository) {
    fun run(): LiveData<List<CarWithLastHistory>> = carRepository.carsWithLastHistory
}
