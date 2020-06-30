package net.devetude.trace.common.extension

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.File
import java.util.Locale

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this /* context */)

val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val Context.locationManager: LocationManager
    get() = getSystemService(LOCATION_SERVICE) as LocationManager

private const val FILE_PROVIDER_AUTHORITY = "net.devetude.trace.file_provider"

fun Context.getUriOf(file: File): Uri =
    FileProvider.getUriForFile(this /* context */, FILE_PROVIDER_AUTHORITY, file)

fun Context?.hideSoftKeyboard(view: View) {
    this?.getSystemService<InputMethodManager>()
        ?.hideSoftInputFromWindow(view.windowToken, 0 /* flag */)
}

fun Context.showShortToast(@StringRes stringRes: Int) =
    makeText(this /* context */, stringRes, LENGTH_SHORT).show()

fun Context.getLocale(): Locale = if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
    resources.configuration.locales[0]
} else {
    resources.configuration.locale
}

fun Context.getBooleanSharedPreference(
    @StringRes keyStringRes: Int,
    defaultValue: Boolean = false
): Boolean = PreferenceManager.getDefaultSharedPreferences(this /* context */)
    .getBoolean(getString(keyStringRes), defaultValue)

private const val CLICK = "click"
private const val TARGET_NAME = "target_name"

@SuppressLint(value = ["MissingPermission"])
fun Context.logClickEvent(targetName: String) {
    val bundle = Bundle().apply { putString(TARGET_NAME, targetName) }
    FirebaseAnalytics.getInstance(this).logEvent(CLICK, bundle)
}
