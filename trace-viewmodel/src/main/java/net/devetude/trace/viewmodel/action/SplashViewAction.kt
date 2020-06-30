package net.devetude.trace.viewmodel.action

sealed class SplashViewAction {
    sealed class Io : SplashViewAction() {
        object DoMandatoryAsyncWorks : Io()
    }

    sealed class Activity : SplashViewAction() {
        object StartMainActivity : Activity()

        object FinishActivity : Activity()

        object TurnOffTransitionAnimation : Activity()
    }
}
