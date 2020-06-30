package net.devetude.trace.usecase.geocoding.cases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.devetude.trace.api.service.ReverseGeocodingService
import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude
import net.devetude.trace.entity.ReverseGeocodingResponse

internal class RequestAddressUseCase(
    private val reverseGeocodingService: ReverseGeocodingService
) {
    suspend fun run(
        @Latitude latitude: Double,
        @Longitude longitude: Double
    ): Result<ReverseGeocodingResponse> = withContext(Dispatchers.IO) {
        runCatching { reverseGeocodingService.requestAddress(coords = "$longitude,$latitude") }
    }
}
