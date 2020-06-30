package net.devetude.trace.db.module

import net.devetude.trace.db.TraceDatabase
import net.devetude.trace.db.dao.CarDao
import net.devetude.trace.db.dao.CarDao_Impl
import net.devetude.trace.db.dao.HistoryDao
import net.devetude.trace.db.dao.HistoryDao_Impl
import org.koin.dsl.module

val traceDaoModule = module {
    single { CarDao_Impl(get<TraceDatabase>()) as CarDao }
    single { HistoryDao_Impl(get<TraceDatabase>()) as HistoryDao }
}
