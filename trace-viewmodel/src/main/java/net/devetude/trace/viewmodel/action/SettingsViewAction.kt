package net.devetude.trace.viewmodel.action

sealed class SettingsViewAction {
    sealed class View : SettingsViewAction() {
        object InitViews : View()
    }

    sealed class Activity : SettingsViewAction() {
        object StartPrivacyPolicyActivity : Activity()

        object StartOssLicensesMenuActivity : Activity()
    }
}
