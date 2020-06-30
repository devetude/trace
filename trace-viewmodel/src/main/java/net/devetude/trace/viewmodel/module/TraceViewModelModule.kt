package net.devetude.trace.viewmodel.module

import net.devetude.trace.viewmodel.AddCarViewModel
import net.devetude.trace.viewmodel.AddParkingHistoryViewModel
import net.devetude.trace.viewmodel.CarsViewModel
import net.devetude.trace.viewmodel.HistoriesViewModel
import net.devetude.trace.viewmodel.MainViewModel
import net.devetude.trace.viewmodel.SettingsViewModel
import net.devetude.trace.viewmodel.SplashViewModel
import net.devetude.trace.viewmodel.UpdateCarViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val traceViewModelModule = module {
    viewModel { SplashViewModel() }
    viewModel { MainViewModel() }
    viewModel { AddCarViewModel(get()) }
    viewModel { CarsViewModel(get(), get()) }
    viewModel { HistoriesViewModel(get(), get()) }
    viewModel { AddParkingHistoryViewModel(get(), get(), get()) }
    viewModel { UpdateCarViewModel(get()) }
    viewModel { SettingsViewModel() }
}
