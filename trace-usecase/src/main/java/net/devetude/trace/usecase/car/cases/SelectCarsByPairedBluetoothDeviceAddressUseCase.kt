package net.devetude.trace.usecase.car.cases

import net.devetude.trace.entity.Car
import net.devetude.trace.repository.CarRepository

internal class SelectCarsByPairedBluetoothDeviceAddressUseCase(
    private val carRepository: CarRepository
) {
    suspend fun run(pairedBluetoothDeviceAddress: String): List<Car> =
        carRepository.selectCarsBy(pairedBluetoothDeviceAddress)
}
