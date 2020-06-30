package net.devetude.trace.db.module

import net.devetude.trace.db.repository.CarRepository
import net.devetude.trace.db.repository.HistoryRepository
import org.koin.dsl.module

val traceRepositoryModule = module {
    single { CarRepository(get()) }
    single { HistoryRepository(get()) }
}
