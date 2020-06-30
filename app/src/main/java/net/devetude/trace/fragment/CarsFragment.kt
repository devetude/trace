package net.devetude.trace.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import net.devetude.trace.R
import net.devetude.trace.activity.AddCarActivity
import net.devetude.trace.activity.AddParkingHistoryActivity
import net.devetude.trace.activity.UpdateCarActivity
import net.devetude.trace.adapter.CarsAdapter
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.common.extension.showShortToast
import net.devetude.trace.databinding.FragmentCarsBinding
import net.devetude.trace.entity.Car
import net.devetude.trace.model.CarsItem.CarWithLastHistoryItem
import net.devetude.trace.viewmodel.CarsViewModel
import net.devetude.trace.viewmodel.action.CarsViewAction.Activity.StartAddCarActivity
import net.devetude.trace.viewmodel.action.CarsViewAction.Activity.StartAddParkingHistoryActivity
import net.devetude.trace.viewmodel.action.CarsViewAction.Activity.StartUpdateCarActivity
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
import org.koin.android.viewmodel.ext.android.viewModel

class CarsFragment : Fragment() {
    private val viewModel: CarsViewModel by viewModel()
    private val carsAdapter: CarsAdapter by lazy { CarsAdapter(viewModel) }
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

    private lateinit var binding: FragmentCarsBinding

    private var progressDialog: ProgressDialog? = null
    private var changeToDrivingStateConfirmDialog: Dialog? = null
    private var deleteCarConfirmDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCarsBinding.inflate(
            inflater,
            container,
            false /* attachToRoot */
        ).also { it.viewModel = viewModel }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext())
            .setCurrentScreen(requireActivity(), SCREEN_NAME, null)
        observeActions()
        viewModel.onViewCreated()
    }

    private fun observeActions() = with(viewModel) {
        viewAction.observe(
            this@CarsFragment /* owner */,
            Observer {
                when (it) {
                    InitViews -> {
                        initViews()
                    }
                    is ShowChangeToDrivingStateConfirmDialog -> {
                        showChangeToDrivingStateConfirmDialog(it.car)
                    }
                    DismissChangeToDrivingStateConfirmDialog -> {
                        changeToDrivingStateConfirmDialog?.dismiss()
                    }
                    ShowProgressDialog -> {
                        showProgressDialog()
                    }
                    DismissProgressDialog -> {
                        progressDialog?.dismiss()
                    }
                    is ShowSelectableCarActionDialog -> {
                        showSelectableCarActionDialog(it.car)
                    }
                    is ShowDeleteCarConfirmDialog -> {
                        showDeleteCarConfirmDialog(it.car)
                    }
                    DismissDeleteCarConfirmDialog -> {
                        deleteCarConfirmDialog?.dismiss()
                    }
                    ShowFailToDeleteCarToast -> {
                        requireContext().showShortToast(R.string.failed_to_delete_car)
                    }
                    is ShowSelectableHistoryActionDialog -> {
                        showSelectableHistoryActionDialog(it.car)
                    }
                }.exhaustive()
            }
        )
        activityAction.observe(
            this@CarsFragment /* owner */,
            Observer {
                when (it) {
                    StartAddCarActivity -> {
                        startAddCarActivity()
                    }
                    is StartAddParkingHistoryActivity -> {
                        startAddParkingHistoryActivity(it.carNumber)
                    }
                    is StartUpdateCarActivity -> {
                        startUpdateCarActivity(it.carNumber)
                    }
                }.exhaustive()
            }
        )
    }

    private fun initViews() {
        binding.carRecyclerView.apply {
            adapter = carsAdapter
            layoutManager = linearLayoutManager
            itemAnimator = null
            setHasFixedSize(true)
        }
        viewModel.carsWithLastHistory.observe(
            this /* owner */,
            Observer { carsAdapter.submitList(it.map(::CarWithLastHistoryItem)) }
        )
    }

    private fun showChangeToDrivingStateConfirmDialog(car: Car) {
        changeToDrivingStateConfirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.change_car_state)
            .setMessage(R.string.change_to_driving_state_confirm_message)
            .setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int ->
                viewModel.onChangeToDrivingStateConfirmDialogNegativeButtonClicked()
            }
            .setPositiveButton(R.string.confirm) { _: DialogInterface, _: Int ->
                viewModel.onChangeToDrivingStateConfirmDialogPositiveButtonClicked(car)
            }
            .create()
        changeToDrivingStateConfirmDialog?.show()
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(requireContext()).apply {
            setCancelable(false)
            setMessage(getString(R.string.loading))
        }
        progressDialog?.show()
    }

    private fun startAddCarActivity() {
        val intent = AddCarActivity.createIntent(requireContext())
        startActivity(intent)
    }

    private fun startAddParkingHistoryActivity(carNumber: String) {
        val intent = AddParkingHistoryActivity.createIntent(requireContext(), carNumber)
        startActivity(intent)
    }

    private fun showSelectableCarActionDialog(car: Car) {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOf(R.string.edit, R.string.delete).map(::getString)
        )
        MaterialAlertDialogBuilder(requireContext())
            .setAdapter(adapter) { _: DialogInterface, which: Int ->
                viewModel.onCarActionSelected(which, car)
            }
            .create()
            .show()
    }

    private fun showDeleteCarConfirmDialog(car: Car) {
        deleteCarConfirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_car)
            .setMessage(getString(R.string.delete_car_confirm_message, car.modelName, car.number))
            .setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int ->
                viewModel.onDeleteCarConfirmDialogNegativeButtonClicked()
            }
            .setPositiveButton(R.string.confirm) { _: DialogInterface, _: Int ->
                viewModel.onDeleteCarConfirmDialogPositiveButtonClicked(car)
            }
            .create()
        deleteCarConfirmDialog?.show()
    }

    private fun showSelectableHistoryActionDialog(car: Car) {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOf(R.string.set_to_driving_state, R.string.add_parking_history).map(::getString)
        )
        MaterialAlertDialogBuilder(requireContext())
            .setAdapter(adapter) { _: DialogInterface, which: Int ->
                viewModel.onHistoryActionSelected(which, car)
            }
            .create()
            .show()
    }

    private fun startUpdateCarActivity(carNumber: String) {
        val intent = UpdateCarActivity.createIntent(requireContext(), carNumber)
        requireActivity().startActivity(intent)
    }

    companion object {
        private const val SCREEN_NAME = "CarsFragment"
    }
}
