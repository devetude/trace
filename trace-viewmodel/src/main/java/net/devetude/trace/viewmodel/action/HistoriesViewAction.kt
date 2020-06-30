package net.devetude.trace.viewmodel.action

import androidx.annotation.Size
import net.devetude.trace.entity.Car

sealed class HistoriesViewAction {
    sealed class State : HistoriesViewAction() {
        object RegisterTimeTickReceiver : State()

        object UnregisterTimeTickReceiver : State()
    }

    sealed class View : HistoriesViewAction() {
        object InitViews : View()

        object UpdateVisibleHistoryItemViews : View()

        object ShowProgressDialog : View()

        object DismissProgressDialog : View()

        object ShowFailToAddHistoryToast : View()

        object CollapseAddHistoryFloatingActionsMenu : View()

        data class ShowSelectableCarsDialog(@Size(min = 1) val selectableCars: List<Car>) : View()
    }

    sealed class Activity : HistoriesViewAction() {
        object StartAddParkingHistoryActivity : Activity()
    }
}
