package net.devetude.trace.usecase.geocoding

import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude
import net.devetude.trace.entity.ReverseGeocodingResponse
import net.devetude.trace.repository.GeocodingRepository
import net.devetude.trace.usecase.geocoding.cases.RequestAddressUseCase

class GeocodingUseCase(private val geocodingRepository: GeocodingRepository) {
    suspend fun requestAddress(
        @Latitude latitude: Double,
        @Longitude longitude: Double
    ): Result<ReverseGeocodingResponse> = RequestAddressUseCase(geocodingRepository)
        .run(latitude, longitude)
}
