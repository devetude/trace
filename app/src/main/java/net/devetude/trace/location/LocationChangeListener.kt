package net.devetude.trace.location

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude

interface LocationChangeListener : LocationListener {
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

    override fun onProviderEnabled(provider: String?) = Unit

    override fun onProviderDisabled(provider: String?) = Unit

    override fun onLocationChanged(location: Location?) =
        onLocationCoordinateChanged(location?.latitude, location?.longitude)

    fun onLocationCoordinateChanged(@Latitude latitude: Double?, @Longitude longitude: Double?)
}
