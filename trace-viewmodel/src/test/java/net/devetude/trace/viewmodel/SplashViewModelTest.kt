package net.devetude.trace.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifyOrder
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.StartMainActivity
import net.devetude.trace.viewmodel.action.SplashViewAction.Activity.TurnOffTransitionAnimation
import net.devetude.trace.viewmodel.action.SplashViewAction.Io
import net.devetude.trace.viewmodel.action.SplashViewAction.Io.DoMandatoryAsyncWorks
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {
    @MockK
    private lateinit var activityActionObserver: Observer<Activity>

    @MockK
    private lateinit var ioActionObserver: Observer<Io>

    @InjectMockKs
    private lateinit var splashViewModel: SplashViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        splashViewModel.activityAction.observeForever(activityActionObserver)
        every { activityActionObserver.onChanged(any()) } returns Unit

        splashViewModel.ioAction.observeForever(ioActionObserver)
        every { ioActionObserver.onChanged(any()) } returns Unit
    }

    @Test
    fun `Test onCreate`() {
        splashViewModel.onCreate()

        verify(exactly = 1) { ioActionObserver.onChanged(eq(DoMandatoryAsyncWorks)) }
        verify { activityActionObserver wasNot Called }
        confirmVerified(ioActionObserver, activityActionObserver)
    }

    @Test
    fun `Test onPause`() {
        splashViewModel.onPause()

        verify(exactly = 1) { activityActionObserver.onChanged(eq(TurnOffTransitionAnimation)) }
        verify { ioActionObserver wasNot Called }
        confirmVerified(activityActionObserver, ioActionObserver)
    }

    @Test
    fun `Test onInitMandatoryModulesDone`() {
        splashViewModel.onInitMandatoryModulesDone()

        verifyOrder {
            activityActionObserver.onChanged(eq(StartMainActivity))
            activityActionObserver.onChanged(eq(FinishActivity))
        }
        verify { ioActionObserver wasNot Called }
        confirmVerified(activityActionObserver, ioActionObserver)
    }
}
