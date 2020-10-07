package net.devetude.trace.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import net.devetude.trace.R
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.viewmodel.SettingsViewModel
import net.devetude.trace.viewmodel.action.SettingsViewAction.Activity.StartOssLicensesMenuActivity
import net.devetude.trace.viewmodel.action.SettingsViewAction.Activity.StartPrivacyPolicyActivity
import net.devetude.trace.viewmodel.action.SettingsViewAction.View.InitViews
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext())
            .setCurrentScreen(requireActivity(), SCREEN_NAME, null)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        observeActions()
        viewModel.onCreatePreferences()
    }

    private fun observeActions() = with(viewModel) {
        viewAction.observe(this@SettingsFragment /* owner */) {
            when (it) {
                InitViews -> initViews()
            }.exhaustive()
        }
        activityAction.observe(this@SettingsFragment /* owner */) {
            when (it) {
                StartPrivacyPolicyActivity -> startPrivacyPolicyActivity()
                StartOssLicensesMenuActivity -> startOssLicensesMenuActivity()
            }.exhaustive()
        }
    }

    private fun initViews() {
        findPreference<Preference>(getString(R.string.privacy_policy_key))
            ?.setOnPreferenceClickListener {
                viewModel.onPrivacyPolicyPreferenceClicked()
            }
        findPreference<Preference>(getString(R.string.open_source_key))
            ?.setOnPreferenceClickListener {
                viewModel.onOpenSourcePreferenceClicked()
            }
    }

    private fun startPrivacyPolicyActivity() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URI))
        startActivity(intent)
    }

    private fun startOssLicensesMenuActivity() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source))
        val intent = Intent(context, OssLicensesMenuActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val SCREEN_NAME = "SettingsFragment"
        private const val PRIVACY_POLICY_URI =
            "https://github.com/devetude/trace-privacy-policy/blob/master/README.md"
    }
}
