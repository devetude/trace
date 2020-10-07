package net.devetude.trace.fragment

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent.ACTION_TIME_TICK
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.Size
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.SCREEN_VIEW
import net.devetude.trace.R
import net.devetude.trace.activity.AddParkingHistoryActivity
import net.devetude.trace.adapter.HistoriesAdapter
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.common.extension.showShortToast
import net.devetude.trace.databinding.FragmentHistoriesBinding
import net.devetude.trace.entity.Car
import net.devetude.trace.receiver.TimeTickReceiver
import net.devetude.trace.viewmodel.HistoriesViewModel
import net.devetude.trace.viewmodel.action.HistoriesViewAction.Activity.StartAddParkingHistoryActivity
import net.devetude.trace.viewmodel.action.HistoriesViewAction.State.RegisterTimeTickReceiver
import net.devetude.trace.viewmodel.action.HistoriesViewAction.State.UnregisterTimeTickReceiver
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.CollapseAddHistoryFloatingActionsMenu
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.DismissProgressDialog
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.InitViews
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.ShowFailToAddHistoryToast
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.ShowProgressDialog
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.ShowSelectableCarsDialog
import net.devetude.trace.viewmodel.action.HistoriesViewAction.View.UpdateVisibleHistoryItemViews
import org.koin.android.viewmodel.ext.android.viewModel

class HistoriesFragment : Fragment() {
    private val viewModel: HistoriesViewModel by viewModel()
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }
    private val historiesAdapter: HistoriesAdapter by lazy { HistoriesAdapter() }

    private lateinit var binding: FragmentHistoriesBinding

    private var timeTickReceiver: TimeTickReceiver? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHistoriesBinding.inflate(
            inflater,
            container,
            false /* attachToRoot */
        ).also { it.viewModel = viewModel }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendScreenLogEvent()
        observeActions()
        viewModel.onCreate()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun sendScreenLogEvent() {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, SCREEN_NAME)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, SCREEN_CLASS)
        }
        FirebaseAnalytics.getInstance(requireContext()).logEvent(SCREEN_VIEW, bundle)
    }

    private fun observeActions() = with(viewModel) {
        viewAction.observe(viewLifecycleOwner) {
            when (it) {
                InitViews -> initViews()
                UpdateVisibleHistoryItemViews -> updateVisibleHistoryItemViews()
                ShowProgressDialog -> showProgressDialog()
                DismissProgressDialog -> dismissProgressDialog()
                ShowFailToAddHistoryToast ->
                    requireContext().showShortToast(R.string.failed_to_add_parking_history)
                is ShowSelectableCarsDialog -> showSelectableCarsDialog(it.selectableCars)
                CollapseAddHistoryFloatingActionsMenu ->
                    binding.addHistoryFloatingActionsMenu.collapse()
            }.exhaustive()
        }
        stateAction.observe(viewLifecycleOwner) {
            when (it) {
                RegisterTimeTickReceiver -> registerTimeTickReceiver()
                UnregisterTimeTickReceiver -> unregisterTimeTickReceiver()
            }.exhaustive()
        }
        activityAction.observe(viewLifecycleOwner) {
            when (it) {
                StartAddParkingHistoryActivity -> startAddParkingHistoryActivity()
            }.exhaustive()
        }
    }

    private fun initViews() {
        binding.historiesRecyclerView.apply {
            adapter = historiesAdapter
            layoutManager = linearLayoutManager
            itemAnimator = null
        }
        viewModel.pagedHistoriesWithCar.observe(viewLifecycleOwner) {
            binding.historiesRecyclerView.isVisible = 0 < it.size
            binding.emptyGroup.isVisible = !binding.historiesRecyclerView.isVisible
            historiesAdapter.submitList(it)
        }
        viewModel.areNotCarsExist.observe(viewLifecycleOwner) {
            binding.addHistoryFloatingActionsMenu.isVisible = it
            binding.historiesRecyclerView.isVisible = it
        }
    }

    private fun registerTimeTickReceiver() {
        timeTickReceiver = TimeTickReceiver(viewModel::onTimeTickReceived)
        requireActivity().registerReceiver(timeTickReceiver, IntentFilter(ACTION_TIME_TICK))
    }

    private fun unregisterTimeTickReceiver() {
        timeTickReceiver ?: return
        requireActivity().unregisterReceiver(timeTickReceiver)
        timeTickReceiver = null
    }

    private fun updateVisibleHistoryItemViews() {
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        if (firstVisibleItemPosition == NO_POSITION) return
        val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
        if (lastVisibleItemPosition == NO_POSITION) return
        historiesAdapter.notifyItemRangeChanged(
            firstVisibleItemPosition,
            lastVisibleItemPosition - firstVisibleItemPosition + 1
        )
    }

    private fun startAddParkingHistoryActivity() {
        val intent = AddParkingHistoryActivity.createIntent(requireContext())
        startActivity(intent)
    }

    private fun showSelectableCarsDialog(@Size(min = 1) selectableCars: List<Car>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            selectableCars.map {
                getString(R.string.car_model_name_and_number, it.modelName, it.number)
            }
        )
        MaterialAlertDialogBuilder(requireContext())
            .setAdapter(adapter) { _: DialogInterface, which: Int ->
                viewModel.onCarSelected(selectableCars[which])
            }
            .create()
            .show()
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(requireContext()).apply {
            setCancelable(false)
            setMessage(getString(R.string.loading))
        }
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    companion object {
        private const val SCREEN_NAME = "HistoriesFragment"
        private const val SCREEN_CLASS = "net.devetude.trace.fragment.HistoriesFragment"
    }
}
