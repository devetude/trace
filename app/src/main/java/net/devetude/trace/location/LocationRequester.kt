package net.devetude.trace.location

import android.annotation.SuppressLint
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.location.LocationManager.PASSIVE_PROVIDER
import net.devetude.trace.BuildConfig
import net.devetude.trace.model.LocationRequestConfiguration

class LocationRequester(
    private val locationManager: LocationManager,
    private val locationChangeListener: LocationChangeListener,
    private val configuration: LocationRequestConfiguration =
        LocationRequestConfiguration.of(BuildConfig.DEBUG)
) {
    @SuppressLint(value = ["MissingPermission"])
    fun request() = ALL_PROVIDERS.forEach {
        locationManager.requestLocationUpdates(
            it,
            configuration.intervalMs,
            configuration.minDistanceM,
            locationChangeListener
        )
    }

    companion object {
        private val ALL_PROVIDERS: Array<String> =
            arrayOf(GPS_PROVIDER, NETWORK_PROVIDER, PASSIVE_PROVIDER)
    }
}
