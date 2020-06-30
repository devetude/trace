package net.devetude.trace.usecase.module

import net.devetude.trace.usecase.car.CarUseCase
import net.devetude.trace.usecase.geocoding.GeocodingUseCase
import net.devetude.trace.usecase.history.HistoryUseCase
import org.koin.dsl.module

val traceUseCaseModule = module {
    single { CarUseCase(get()) }
    single { HistoryUseCase(get()) }
    single { GeocodingUseCase(get()) }
}
