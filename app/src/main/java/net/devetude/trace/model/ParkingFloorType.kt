package net.devetude.trace.model

import androidx.annotation.StringRes
import net.devetude.trace.R
import net.devetude.trace.common.annotation.ParkingFloor

enum class ParkingFloorType(
    @ParkingFloor val floor: Int,
    @StringRes val stringRes: Int
) {
    F9(floor = 9, stringRes = R.string.f9),
    F8(floor = 8, stringRes = R.string.f8),
    F7(floor = 7, stringRes = R.string.f7),
    F6(floor = 6, stringRes = R.string.f6),
    F5(floor = 5, stringRes = R.string.f5),
    F4(floor = 4, stringRes = R.string.f4),
    F3(floor = 3, stringRes = R.string.f3),
    F2(floor = 2, stringRes = R.string.f2),
    F1(floor = 1, stringRes = R.string.f1),
    NOT_SELECTED(floor = 0, stringRes = R.string.not_selected),
    B1(floor = -1, stringRes = R.string.b1),
    B2(floor = -2, stringRes = R.string.b2),
    B3(floor = -3, stringRes = R.string.b3),
    B4(floor = -4, stringRes = R.string.b4),
    B5(floor = -5, stringRes = R.string.b5),
    B6(floor = -6, stringRes = R.string.b6),
    B7(floor = -7, stringRes = R.string.b7),
    B8(floor = -8, stringRes = R.string.b8),
    B9(floor = -9, stringRes = R.string.b9);

    companion object {
        private val FLOOR_LOOKUP: Map<Int, ParkingFloorType> by lazy {
            values().associateBy { it.floor }
        }

        fun of(@ParkingFloor floor: Int): ParkingFloorType =
            FLOOR_LOOKUP[floor] ?: error("Undefined floor=$floor")
    }
}
