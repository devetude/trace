package net.devetude.trace.usecase.geocoding.cases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude
import net.devetude.trace.entity.ReverseGeocodingResponse
import net.devetude.trace.repository.GeocodingRepository

internal class RequestAddressUseCase(private val geocodingRepository: GeocodingRepository) {
    suspend fun run(
        @Latitude latitude: Double,
        @Longitude longitude: Double
    ): Result<ReverseGeocodingResponse> = withContext(Dispatchers.IO) {
        runCatching { geocodingRepository.requestAddress(coords = "$longitude,$latitude") }
    }
}
