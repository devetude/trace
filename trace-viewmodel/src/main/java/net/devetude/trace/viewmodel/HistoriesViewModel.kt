package net.devetude.trace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import kotlinx.coroutines.launch
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.HistoryWithCar
import net.devetude.trace.usecase.car.CarUseCase
import net.devetude.trace.usecase.history.HistoryUseCase
import net.devetude.trace.viewmodel.action.HistoriesViewAction
import net.devetude.trace.viewmodel.action.HistoriesViewAction.Activity
import net.devetude.trace.viewmodel.action.HistoriesViewAction.Activity.StartAddParkingHistoryActivity
import net.devetude.trace.viewmodel.action.HistoriesViewAction.State
import net.devetude.trace.viewmodel.action.HistoriesViewAction.State.RegisterTimeTickReceiver
import net.devetude.trace.viewmodel.action.HistoriesViewAction.State.UnregisterTimeTickReceiver
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.CollapseAddHistoryFloatingActionsMenu
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.DismissProgressDialog
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.InitViews
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.ShowFailToAddHistoryToast
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.ShowProgressDialog
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.ShowSelectableCarsDialog
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.UpdateVisibleHistoryItemViews

class HistoriesViewModel(
    private val historyUseCase: HistoryUseCase,
    private val carUseCase: CarUseCase
) : ViewModel() {
    private val _viewAction: MutableLiveData<View> = MutableLiveData()
    val viewAction: LiveData<View> = _viewAction

    private val _stateAction: MutableLiveData<State> = MutableLiveData()
    val stateAction: LiveData<State> = _stateAction

    private val _activityAction: MutableLiveData<Activity> = MutableLiveData()
    val activityAction: LiveData<Activity> = _activityAction

    val pagedHistoriesWithCar: LiveData<PagedList<HistoryWithCar>> =
        historyUseCase.selectPagedHistoriesWithCar()

    val areNotCarsExist: LiveData<Boolean> = Transformations.map(carUseCase.countCars()) { 0 < it }

    fun onCreate() = emit(InitViews, RegisterTimeTickReceiver)

    fun onPause() = emit(UnregisterTimeTickReceiver)

    fun onTimeTickReceived() = emit(UpdateVisibleHistoryItemViews)

    fun onAddDrivingHistoryFloatingActionButtonClicked() {
        emit(CollapseAddHistoryFloatingActionsMenu, ShowProgressDialog)
        viewModelScope.launch {
            val selectableCars = carUseCase.selectAllCars()
            emit(ShowSelectableCarsDialog(selectableCars), DismissProgressDialog)
        }
    }

    fun onAddParkingHistoryFloatingActionButtonClicked() =
        emit(CollapseAddHistoryFloatingActionsMenu, StartAddParkingHistoryActivity)

    fun onCarSelected(car: Car) {
        emit(ShowProgressDialog)
        viewModelScope.launch {
            historyUseCase.insert(car.toDrivingHistory())
                .onFailure { emit(DismissProgressDialog, ShowFailToAddHistoryToast) }
                .onSuccess { emit(DismissProgressDialog) }
        }
    }

    private fun emit(vararg actions: HistoriesViewAction) = actions.forEach {
        when (it) {
            is View -> _viewAction.value = it
            is State -> _stateAction.value = it
            is Activity -> _activityAction.value = it
        }.exhaustive()
    }
}
