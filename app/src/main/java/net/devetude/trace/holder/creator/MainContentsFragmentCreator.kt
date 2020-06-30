package net.devetude.trace.holder.creator

import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import net.devetude.trace.fragment.CarsFragment
import net.devetude.trace.fragment.HistoriesFragment
import net.devetude.trace.fragment.SettingsFragment

sealed class MainContentsFragmentCreator {
    @UiThread
    abstract fun create(): Fragment

    object CarsFragmentCreator : MainContentsFragmentCreator() {
        @UiThread
        override fun create(): Fragment = CarsFragment()
    }

    object HistoryFragmentCreator : MainContentsFragmentCreator() {
        @UiThread
        override fun create(): Fragment = HistoriesFragment()
    }

    object SettingsFragmentCreator : MainContentsFragmentCreator() {
        @UiThread
        override fun create(): Fragment = SettingsFragment()
    }
}
