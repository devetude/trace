package net.devetude.trace.db.module

import net.devetude.trace.db.TraceDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val traceDatabaseModule = module {
    single { TraceDatabase.create(androidApplication()) }
}
