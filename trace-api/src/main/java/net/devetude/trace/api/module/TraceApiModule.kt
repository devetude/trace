package net.devetude.trace.api.module

import net.devetude.trace.api.retrofit.ReverseGeocodingRetrofitCreator
import net.devetude.trace.api.service.ReverseGeocodingService
import org.koin.dsl.module

val traceApiModule = module {
    single {
        ReverseGeocodingRetrofitCreator()
            .create()
            .create(ReverseGeocodingService::class.java)
    }
}
