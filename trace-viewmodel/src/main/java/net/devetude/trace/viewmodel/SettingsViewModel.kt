package net.devetude.trace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.viewmodel.action.SettingsViewAction
import net.devetude.trace.viewmodel.action.SettingsViewAction.Activity
import net.devetude.trace.viewmodel.action.SettingsViewAction.Activity.StartOssLicensesMenuActivity
import net.devetude.trace.viewmodel.action.SettingsViewAction.Activity.StartPrivacyPolicyActivity
import net.devetude.trace.viewmodel.action.SettingsViewAction.View
import net.devetude.trace.viewmodel.action.SettingsViewAction.View.InitViews

class SettingsViewModel : ViewModel() {
    private val _viewAction: MutableLiveData<View> = MutableLiveData()
    val viewAction: LiveData<View> = _viewAction

    private val _activityAction: MutableLiveData<Activity> = MutableLiveData()
    val activityAction: LiveData<Activity> = _activityAction

    fun onCreatePreferences() = emit(InitViews)

    fun onPrivacyPolicyPreferenceClicked(): Boolean {
        emit(StartPrivacyPolicyActivity)
        return true
    }

    fun onOpenSourcePreferenceClicked(): Boolean {
        emit(StartOssLicensesMenuActivity)
        return true
    }

    private fun emit(vararg actions: SettingsViewAction) = actions.forEach {
        when (it) {
            is View -> _viewAction.value = it
            is Activity -> _activityAction.value = it
        }.exhaustive()
    }
}
