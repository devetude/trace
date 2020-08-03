package net.devetude.trace.repository

import net.devetude.trace.api.service.ReverseGeocodingService
import net.devetude.trace.entity.ReverseGeocodingResponse

class GeocodingRepository(private val reverseGeocodingService: ReverseGeocodingService) {
    suspend fun requestAddress(coords: String): ReverseGeocodingResponse =
        reverseGeocodingService.requestAddress(coords)
}
