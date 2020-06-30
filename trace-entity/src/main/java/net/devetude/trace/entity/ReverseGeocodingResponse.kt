package net.devetude.trace.entity

import com.google.gson.annotations.SerializedName

data class ReverseGeocodingResponse(
    @SerializedName(value = "status") val status: Status,
    @SerializedName(value = "results") val results: List<Result>
) {
    data class Status(
        @SerializedName(value = "code") val code: Int,
        @SerializedName(value = "name") val name: String,
        @SerializedName(value = "message") val message: String
    )

    data class Result(
        @SerializedName(value = "name") val name: String,
        @SerializedName(value = "code") val code: Code,
        @SerializedName(value = "region") val region: Region
    ) {
        data class Code(
            @SerializedName(value = "id") val id: String,
            @SerializedName(value = "type") val type: String,
            @SerializedName(value = "mappingId") val mappingId: String
        )

        data class Region(
            @SerializedName(value = "area0") val area0: Area,
            @SerializedName(value = "area1") val area1: Area,
            @SerializedName(value = "area2") val area2: Area,
            @SerializedName(value = "area3") val area3: Area,
            @SerializedName(value = "area4") val area4: Area
        ) {
            data class Area(
                @SerializedName(value = "name") val name: String,
                @SerializedName(value = "coords") val coords: Coords
            ) {
                data class Coords(
                    @SerializedName(value = "center") val center: Center
                ) {
                    data class Center(
                        @SerializedName(value = "crs") val crs: String,
                        @SerializedName(value = "x") val longitude: Double,
                        @SerializedName(value = "y") val latitude: Double
                    )
                }
            }
        }
    }

    fun toAddress(): String = results.map { it.region }
        .firstOrNull()
        ?.let { "${it.area1.name} ${it.area2.name} ${it.area3.name} ${it.area4.name}".trim() }
        .orEmpty()
}
