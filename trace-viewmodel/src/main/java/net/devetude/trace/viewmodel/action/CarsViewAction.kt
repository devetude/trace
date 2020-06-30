package net.devetude.trace.viewmodel.action

import net.devetude.trace.entity.Car

sealed class CarsViewAction {
    sealed class View : CarsViewAction() {
        object InitViews : View()

        object DismissChangeToDrivingStateConfirmDialog : View()

        object ShowProgressDialog : View()

        object DismissProgressDialog : View()

        object DismissDeleteCarConfirmDialog : View()

        object ShowFailToDeleteCarToast : View()

        data class ShowSelectableHistoryActionDialog(val car: Car) : View()

        data class ShowSelectableCarActionDialog(val car: Car) : View()

        data class ShowChangeToDrivingStateConfirmDialog(val car: Car) : View()

        data class ShowDeleteCarConfirmDialog(val car: Car) : View()
    }

    sealed class Activity : CarsViewAction() {
        object StartAddCarActivity : Activity()

        data class StartAddParkingHistoryActivity(val carNumber: String) : Activity()

        data class StartUpdateCarActivity(val carNumber: String) : Activity()
    }
}
