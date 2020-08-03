package net.devetude.trace.application

import android.app.Application
import net.devetude.trace.api.module.traceApiModule
import net.devetude.trace.db.module.traceDaoModule
import net.devetude.trace.db.module.traceDatabaseModule
import net.devetude.trace.repository.module.traceRepositoryModule
import net.devetude.trace.usecase.module.traceUseCaseModule
import net.devetude.trace.viewmodel.module.traceViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class TraceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(androidContext = this@TraceApplication)
            modules(MODULES)
        }
    }

    companion object {
        private val MODULES: List<Module> = listOf(
            traceRepositoryModule,
            traceDatabaseModule,
            traceDaoModule,
            traceRepositoryModule,
            traceUseCaseModule,
            traceViewModelModule,
            traceApiModule
        )
    }
}
