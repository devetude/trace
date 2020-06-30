package net.devetude.trace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.viewmodel.action.SplashViewAction
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.StartMainActivity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.TurnOffTransitionAnimation
import net.devetude.trace.viewmodel.action.SplashViewAction.Io
import net.devetude.trace.viewmodel.action.SplashViewAction.Io.DoMandatoryAsyncWorks

class SplashViewModel : ViewModel() {
    private val _activityAction: MutableLiveData<Activity> = MutableLiveData()
    val activityAction: LiveData<Activity> = _activityAction

    private val _ioAction: MutableLiveData<Io> = MutableLiveData()
    val ioAction: LiveData<Io> = _ioAction

    fun onCreate() = emit(DoMandatoryAsyncWorks)

    fun onPause() = emit(TurnOffTransitionAnimation)

    fun onInitMandatoryModulesDone() = emit(StartMainActivity, FinishActivity)

    private fun emit(vararg actions: SplashViewAction) = actions.forEach {
        when (it) {
            is Activity -> _activityAction.value = it
            is Io -> _ioAction.value = it
        }.exhaustive()
    }
}
