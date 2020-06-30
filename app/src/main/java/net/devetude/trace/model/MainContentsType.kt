package net.devetude.trace.model

import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import net.devetude.trace.R
import net.devetude.trace.holder.creator.MainContentsFragmentCreator
import net.devetude.trace.holder.creator.MainContentsFragmentCreator.CarsFragmentCreator
import net.devetude.trace.holder.creator.MainContentsFragmentCreator.HistoryFragmentCreator
import net.devetude.trace.holder.creator.MainContentsFragmentCreator.SettingsFragmentCreator

enum class MainContentsType(
    @StringRes val titleStringRes: Int,
    @IdRes val bottomNavigationItemIdRes: Int,
    val fragmentCreator: MainContentsFragmentCreator
) {
    CARS(
        titleStringRes = R.string.cars,
        bottomNavigationItemIdRes = R.id.cars_item,
        fragmentCreator = CarsFragmentCreator
    ),
    HISTORY(
        titleStringRes = R.string.history,
        bottomNavigationItemIdRes = R.id.history_item,
        fragmentCreator = HistoryFragmentCreator
    ),
    SETTINGS(
        titleStringRes = R.string.settings,
        bottomNavigationItemIdRes = R.id.settings_item,
        fragmentCreator = SettingsFragmentCreator
    );

    companion object {
        private val ORDINAL_LOOKUP: Map<Int, MainContentsType> by lazy {
            values().associateBy { it.ordinal }
        }
        private val BOTTOM_NAVIGATION_ITEM_ID_RESOURCES: Map<Int, MainContentsType> by lazy {
            values().associateBy { it.bottomNavigationItemIdRes }
        }

        fun of(@IntRange(from = 0, to = 2) ordinal: Int): MainContentsType =
            ORDINAL_LOOKUP[ordinal] ?: error("Invalid ordinal=$ordinal")

        fun getIdBy(@IdRes bottomNavigationItemIdRes: Int): Int =
            BOTTOM_NAVIGATION_ITEM_ID_RESOURCES[bottomNavigationItemIdRes]?.ordinal
                ?: error("Invalid bottomNavigationItemIdRes=$bottomNavigationItemIdRes")
    }
}
