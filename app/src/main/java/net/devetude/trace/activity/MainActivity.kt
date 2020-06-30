package net.devetude.trace.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import net.devetude.trace.R
import net.devetude.trace.adapter.MainContentsAdapter
import net.devetude.trace.common.annotation.MainViewPagePosition
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.databinding.ActivityMainBinding
import net.devetude.trace.model.MainContentsType
import net.devetude.trace.viewmodel.MainViewModel
import net.devetude.trace.viewmodel.action.MainViewAction.View.InitViews
import net.devetude.trace.viewmodel.action.MainViewAction.View.SetActionBarTitle
import net.devetude.trace.viewmodel.action.MainViewAction.View.SetBottomNavigationItem
import net.devetude.trace.viewmodel.action.MainViewAction.View.SetCurrentPage
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        observeActions()
        viewModel.onCreate()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this /* activity */,
            R.layout.activity_main
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
    }

    private fun observeActions() = with(viewModel) {
        viewAction.observe(
            this@MainActivity /* owner */,
            Observer {
                when (it) {
                    InitViews -> {
                        initViews()
                    }
                    is SetCurrentPage -> {
                        setCurrentPage(it.id)
                    }
                    is SetBottomNavigationItem -> {
                        setBottomNavigationItem(it.position)
                    }
                    is SetActionBarTitle -> {
                        setActionBarTitle(it.position)
                    }
                }.exhaustive()
            }
        )
    }

    private fun initViews() {
        binding.contentsViewPager.adapter = MainContentsAdapter(supportFragmentManager)
        title = getString(MainContentsType.of(binding.contentsViewPager.currentItem).titleStringRes)
    }

    private fun setCurrentPage(@IdRes idRes: Int) {
        val pageTypeId = MainContentsType.getIdBy(idRes)
        binding.contentsViewPager.setCurrentItem(pageTypeId, false /* smoothScroll */)
    }

    private fun setBottomNavigationItem(@MainViewPagePosition position: Int) {
        binding.bottomNavigationView.selectedItemId =
            MainContentsType.of(position).bottomNavigationItemIdRes
    }

    private fun setActionBarTitle(@MainViewPagePosition position: Int) {
        title = getString(MainContentsType.of(position).titleStringRes)
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
