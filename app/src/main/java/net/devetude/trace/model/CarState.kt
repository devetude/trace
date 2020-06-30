package net.devetude.trace.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.devetude.trace.R

enum class CarState(
    val isParkingState: Boolean?,
    @DrawableRes val drawableStartRes: Int,
    @StringRes val textStringRes: Int,
    @StringRes val contentDescriptionStringRes: Int
) {
    INITIALIZATION_REQUIRED(
        isParkingState = null,
        drawableStartRes = R.drawable.ic_white_priority_high,
        textStringRes = R.string.state_initialization_required,
        contentDescriptionStringRes = R.string.state_initialization_required_button
    ),
    PARKING(
        isParkingState = true,
        drawableStartRes = R.drawable.ic_white_parking,
        textStringRes = R.string.parking,
        contentDescriptionStringRes = R.string.parking_state_button
    ),
    DRIVING(
        isParkingState = false,
        drawableStartRes = R.drawable.ic_white_car,
        textStringRes = R.string.driving,
        contentDescriptionStringRes = R.string.driving_state_button
    );

    companion object {
        private val IS_PARKING_STATE_LOOKUP: Map<Boolean?, CarState> by lazy {
            values().associateBy { it.isParkingState }
        }

        fun of(isParkingState: Boolean?): CarState = IS_PARKING_STATE_LOOKUP[isParkingState]
            ?: error("Invalid isParkingState=$isParkingState")
    }
}
