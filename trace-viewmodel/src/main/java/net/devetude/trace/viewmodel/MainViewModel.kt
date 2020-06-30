package net.devetude.trace.viewmodel

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.devetude.trace.common.annotation.MainViewPagePosition
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.viewmodel.action.MainViewAction
import net.devetude.trace.viewmodel.action.MainViewAction.View
import net.devetude.trace.viewmodel.action.MainViewAction.View.InitViews
import net.devetude.trace.viewmodel.action.MainViewAction.View.SetActionBarTitle
import net.devetude.trace.viewmodel.action.MainViewAction.View.SetBottomNavigationItem
import net.devetude.trace.viewmodel.action.MainViewAction.View.SetCurrentPage

class MainViewModel : ViewModel() {
    private val _viewAction: MutableLiveData<View> = MutableLiveData()
    val viewAction: LiveData<View> = _viewAction

    fun onCreate() = emit(InitViews)

    fun onBottomNavigationItemSelected(@IdRes idRes: Int) = emit(SetCurrentPage(idRes))

    fun onPageSelected(@MainViewPagePosition position: Int) =
        emit(SetBottomNavigationItem(position), SetActionBarTitle(position))

    private fun emit(vararg actions: MainViewAction) = actions.forEach {
        when (it) {
            is View -> _viewAction.value = it
        }.exhaustive()
    }
}
