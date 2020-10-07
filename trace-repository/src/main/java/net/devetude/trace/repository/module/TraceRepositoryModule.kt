package net.devetude.trace.repository.module

import net.devetude.trace.repository.CarRepository
import net.devetude.trace.repository.GeocodingRepository
import net.devetude.trace.repository.HistoryRepository
import org.koin.dsl.module

val traceRepositoryModule = module {
    single { CarRepository(get()) }
    single { HistoryRepository(get()) }
    single { GeocodingRepository(get()) }
}
