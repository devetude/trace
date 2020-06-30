package net.devetude.trace.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.facebook.stetho.Stetho
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.devetude.trace.R
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.db.TraceDatabase
import net.devetude.trace.notification.channel.TraceNotificationChannelCreator
import net.devetude.trace.viewmodel.SplashViewModel
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.StartMainActivity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.TurnOffTransitionAnimation
import net.devetude.trace.viewmodel.action.SplashViewAction.Io.DoMandatoryAsyncWorks
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModel()
    private val traceDatabase: TraceDatabase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        observeActions()
        viewModel.onCreate()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun observeActions() = with(viewModel) {
        ioAction.observe(
            this@SplashActivity /* owner */,
            Observer {
                when (it) {
                    DoMandatoryAsyncWorks -> {
                        doMandatoryAsyncWorks()
                    }
                }.exhaustive()
            }
        )
        activityAction.observe(
            this@SplashActivity /* owner */,
            Observer {
                when (it) {
                    StartMainActivity -> {
                        startMainActivity()
                    }
                    TurnOffTransitionAnimation -> {
                        turnOffTransitionAnimation()
                    }
                    FinishActivity -> {
                        finish()
                    }
                }.exhaustive()
            }
        )
    }

    private fun doMandatoryAsyncWorks() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                TraceNotificationChannelCreator(
                    context = this@SplashActivity
                ).maybeCreateChannels()
                traceDatabase.openHelper.writableDatabase
                Stetho.initializeWithDefaults(this@SplashActivity /* context */)
            }
            viewModel.onInitMandatoryModulesDone()
        }
    }

    private fun turnOffTransitionAnimation() =
        overridePendingTransition(0 /* enterAnim */, 0 /* exitAnim */)

    private fun startMainActivity() = startActivity(MainActivity.createIntent(context = this))
}
