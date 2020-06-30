package net.devetude.trace.viewmodel.action

import androidx.annotation.IdRes
import net.devetude.trace.common.annotation.MainViewPagePosition

sealed class MainViewAction {
    sealed class View : MainViewAction() {
        object InitViews : View()

        data class SetCurrentPage(@IdRes val id: Int) : View()

        data class SetBottomNavigationItem(@MainViewPagePosition val position: Int) : View()

        data class SetActionBarTitle(@MainViewPagePosition val position: Int) : View()
    }
}
