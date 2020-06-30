package net.devetude.trace.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_DENIED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import net.devetude.trace.common.extension.locationManager
import net.devetude.trace.model.LocationPermissionsCheckResult
import net.devetude.trace.model.LocationPermissionsCheckResult.DENIED_BEFORE
import net.devetude.trace.model.LocationPermissionsCheckResult.LOCATION_DISABLED
import net.devetude.trace.model.LocationPermissionsCheckResult.PERMITTED
import net.devetude.trace.model.LocationPermissionsCheckResult.REQUEST_REQUIRED

class LocationPermissionChecker(private val activity: Activity) {
    fun check(): LocationPermissionsCheckResult = if (hasAllPermissions()) {
        if (isLocationEnabled()) PERMITTED else LOCATION_DISABLED
    } else {
        if (wasDeniedBefore()) DENIED_BEFORE else REQUEST_REQUIRED
    }

    private fun hasAllPermissions(): Boolean = LOCATION_PERMISSIONS.none(::hasNot)

    private fun hasNot(permission: String): Boolean =
        ContextCompat.checkSelfPermission(activity, permission) == PERMISSION_DENIED

    private fun isLocationEnabled(): Boolean =
        LocationManagerCompat.isLocationEnabled(activity.locationManager)

    private fun wasDeniedBefore(): Boolean = LOCATION_PERMISSIONS.any {
        ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
    }

    companion object {
        val LOCATION_PERMISSIONS: Array<String> =
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    }
}
