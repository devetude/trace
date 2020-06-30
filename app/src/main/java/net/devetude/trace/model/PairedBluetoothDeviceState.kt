package net.devetude.trace.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.devetude.trace.R

enum class PairedBluetoothDeviceState(
    @DrawableRes val drawableStartRes: Int,
    @StringRes val contentDescriptionStringRes: Int
) {
    PAIRED(
        drawableStartRes = R.drawable.ic_white_bluetooth,
        contentDescriptionStringRes = R.string.paired_car_bluetooth_device_button
    ),
    NO_PAIRED(
        drawableStartRes = R.drawable.ic_white_bluetooth_disabled,
        contentDescriptionStringRes = R.string.no_paired_car_bluetooth_device_button
    )
}
