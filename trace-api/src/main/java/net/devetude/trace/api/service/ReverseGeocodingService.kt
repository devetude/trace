package net.devetude.trace.api.service

import net.devetude.trace.entity.ReverseGeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingService {
    @GET(value = "gc")
    suspend fun requestAddress(
        @Query(value = "coords") coords: String,
        @Query(value = "output") output: String = "json",
        @Query(value = "orders") orders: String = "roadaddr,addr,legalcode"
    ): ReverseGeocodingResponse
}
