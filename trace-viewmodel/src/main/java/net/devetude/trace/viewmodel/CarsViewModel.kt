package net.devetude.trace.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.CarWithLastHistory
import net.devetude.trace.usecase.car.CarUseCase
import net.devetude.trace.usecase.history.HistoryUseCase
import net.devetude.trace.viewmodel.action.CarsViewAction
import net.devetude.trace.viewmodel.action.CarsViewAction.Activity
import net.devetude.trace.viewmodel.action.CarsViewAction.Activity.StartAddCarActivity
import net.devetude.trace.viewmodel.action.CarsViewAction.Activity.StartAddParkingHistoryActivity
import net.devetude.trace.viewmodel.action.CarsViewAction.Activity.StartUpdateCarActivity
import net.devetude.trace.viewmodel.action.CarsViewAction.View
import net.devetude.trace.viewmodel.action.CarsViewAction.View.DismissChangeToDrivingStateConfirmDialog
import net.devetude.trace.viewmodel.action.CarsViewAction.View.DismissDeleteCarConfirmDialog
import net.devetude.trace.viewmodel.action.CarsViewAction.View.DismissProgressDialog
import net.devetude.trace.viewmodel.action.CarsViewAction.View.InitViews
import net.devetude.trace.viewmodel.action.CarsViewAction.View.ShowChangeToDrivingStateConfirmDialog
import net.devetude.trace.viewmodel.action.CarsViewAction.View.ShowDeleteCarConfirmDialog
import net.devetude.trace.viewmodel.action.CarsViewAction.View.ShowFailToDeleteCarToast
import net.devetude.trace.viewmodel.action.CarsViewAction.View.ShowProgressDialog
import net.devetude.trace.viewmodel.action.CarsViewAction.View.ShowSelectableCarActionDialog
import net.devetude.trace.viewmodel.action.CarsViewAction.View.ShowSelectableHistoryActionDialog

class CarsViewModel(
    private val carUseCase: CarUseCase,
    private val historyUseCase: HistoryUseCase
) : ViewModel() {
    private val _viewAction: MutableLiveData<View> = MutableLiveData()
    val viewAction: LiveData<View> = _viewAction

    private val _activityAction: MutableLiveData<Activity> = MutableLiveData()
    val activityAction: LiveData<Activity> = _activityAction

    val carsWithLastHistory: LiveData<List<CarWithLastHistory>> =
        carUseCase.selectAllCarsWithLastHistory()

    fun onViewCreated() = emit(InitViews)

    fun onAddCarFloatingActionButtonClicked() = emit(StartAddCarActivity)

    fun onCarStateButtonClicked(carWithLastHistory: CarWithLastHistory) {
        val car = carWithLastHistory.car
        val isParkingState = carWithLastHistory.lastHistory?.isParkingState
            ?: return emit(ShowSelectableHistoryActionDialog(car))
        val action = if (isParkingState) {
            ShowChangeToDrivingStateConfirmDialog(car)
        } else {
            StartAddParkingHistoryActivity(car.number)
        }
        emit(action)
    }

    fun onParkingLocationAddressButtonClicked(carWithLastHistory: CarWithLastHistory) {
        // TODO: Start map activity here.
    }

    fun onChangeToDrivingStateConfirmDialogNegativeButtonClicked() =
        emit(DismissChangeToDrivingStateConfirmDialog)

    fun onChangeToDrivingStateConfirmDialogPositiveButtonClicked(car: Car) {
        emit(DismissChangeToDrivingStateConfirmDialog)
        insertDrivingHistory(car)
    }

    fun onCarItemLongClicked(car: Car): Boolean {
        emit(ShowSelectableCarActionDialog(car))
        return true
    }

    fun onCarActionSelected(@IntRange(from = 0, to = 1) which: Int, car: Car) {
        when (which) {
            EDIT_CAR_ACTION_WHICH -> emit(StartUpdateCarActivity(car.number))
            DELETE_CAR_ACTION_WHICH -> emit(ShowDeleteCarConfirmDialog(car))
            else -> error("Invalid which=$which")
        }
    }

    fun onDeleteCarConfirmDialogNegativeButtonClicked() = emit(DismissDeleteCarConfirmDialog)

    fun onDeleteCarConfirmDialogPositiveButtonClicked(car: Car) {
        emit(DismissDeleteCarConfirmDialog, ShowProgressDialog)
        viewModelScope.launch {
            carUseCase.delete(car)
                .onFailure { emit(DismissProgressDialog, ShowFailToDeleteCarToast) }
                .onSuccess { emit(DismissProgressDialog) }
        }
    }

    fun onHistoryActionSelected(@IntRange(from = 0, to = 1) which: Int, car: Car) {
        when (which) {
            INITIALIZE_TO_DRIVING_STATE_ACTION_WHICH -> insertDrivingHistory(car)
            INITIALIZE_TO_PARKING_STATE_ACTION_WHICH ->
                emit(StartAddParkingHistoryActivity(car.number))
            else -> error("Invalid which=$which")
        }
    }

    private fun insertDrivingHistory(car: Car) {
        emit(ShowProgressDialog)
        viewModelScope.launch {
            historyUseCase.insert(car.toDrivingHistory())
            emit(DismissProgressDialog)
        }
    }

    private fun emit(vararg actions: CarsViewAction) = actions.forEach {
        when (it) {
            is View -> _viewAction.value = it
            is Activity -> _activityAction.value = it
        }.exhaustive()
    }

    companion object {
        private const val EDIT_CAR_ACTION_WHICH = 0
        private const val DELETE_CAR_ACTION_WHICH = 1

        private const val INITIALIZE_TO_DRIVING_STATE_ACTION_WHICH = 0
        private const val INITIALIZE_TO_PARKING_STATE_ACTION_WHICH = 1
    }
}
