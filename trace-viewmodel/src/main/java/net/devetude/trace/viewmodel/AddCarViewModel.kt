package net.devetude.trace.viewmodel

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothDevice
import android.net.Uri
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.devetude.trace.common.annotation.ResultCode
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.PairedBluetoothDevice
import net.devetude.trace.usecase.car.CarUseCase
import net.devetude.trace.viewmodel.action.AddCarViewAction
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.StartBluetoothEnableRequestActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.StartImageCaptureActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.StartImageGalleryActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Io
import net.devetude.trace.viewmodel.action.AddCarViewAction.Io.CopyGalleryImageAsync
import net.devetude.trace.viewmodel.action.AddCarViewAction.Io.CreateImageFileAsync
import net.devetude.trace.viewmodel.action.AddCarViewAction.State
import net.devetude.trace.viewmodel.action.AddCarViewAction.State.SetBluetoothEnabled
import net.devetude.trace.viewmodel.action.AddCarViewAction.State.SetCameraEnabled
import net.devetude.trace.viewmodel.action.AddCarViewAction.State.SetSelectableBluetoothDevices
import net.devetude.trace.viewmodel.action.AddCarViewAction.View
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.DismissProgressDialog
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.EnableAddButton
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.HideSoftKeyboard
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.InitViews
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.SetCarBluetoothDeviceButton
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.SetCarThumbnailImageButtonAsync
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.ShowBluetoothActivationRequiredToast
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.ShowFailToAddCarToast
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.ShowFailToLoadImageToast
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.ShowImageActionDialog
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.ShowNoSelectableBluetoothDeviceToast
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.ShowProgressDialog
import net.devetude.trace.viewmodel.action.AddCarViewAction.View.ShowSelectableBluetoothDeviceDialog

class AddCarViewModel(private val carUseCase: CarUseCase) : ViewModel() {
    private val _viewAction: MutableLiveData<View> = MutableLiveData()
    val viewAction: LiveData<View> = _viewAction

    private val _stateAction: MutableLiveData<State> = MutableLiveData()
    val stateAction: LiveData<State> = _stateAction

    private val _activityAction: MutableLiveData<Activity> = MutableLiveData()
    val activityAction: LiveData<Activity> = _activityAction

    private val _ioAction: MutableLiveData<Io> = MutableLiveData()
    val ioAction: LiveData<Io> = _ioAction

    private var carModelName: String = ""
    private var carNumber: String = ""
    private var carImageUri: Uri? = null
    private var selectedBluetoothDevice: BluetoothDevice? = null

    var isBluetoothEnabled: Boolean = false
    var isCameraEnabled: Boolean = false
    var selectableBluetoothDevices: List<BluetoothDevice?> = emptyList()

    fun onCreate() = emit(InitViews)

    fun onCarThumbnailImageButtonClicked() {
        emit(HideSoftKeyboard, SetCameraEnabled)
        if (!isCameraEnabled) return emit(StartImageGalleryActivity)
        emit(ShowImageActionDialog(isDeleteImageSelectable = carImageUri != null))
    }

    fun onCarModelNameChanged(carModelName: String) {
        this.carModelName = carModelName
        emit(EnableAddButton(isAddButtonEnabled(carModelName, carNumber)))
    }

    fun onCarNumberChanged(carNumber: String) {
        this.carNumber = carNumber
        emit(EnableAddButton(isAddButtonEnabled(carModelName, carNumber)))
    }

    fun onCarBluetoothDeviceButtonClicked() {
        emit(HideSoftKeyboard, SetBluetoothEnabled)
        if (!isBluetoothEnabled) return emit(StartBluetoothEnableRequestActivity)
        emit(SetSelectableBluetoothDevices)
        if (selectableBluetoothDevices.isEmpty()) {
            return emit(ShowNoSelectableBluetoothDeviceToast)
        }
        selectableBluetoothDevices = listOf(null) + selectableBluetoothDevices
        emit(ShowSelectableBluetoothDeviceDialog)
    }

    @SuppressLint(value = ["MissingPermission"])
    fun onAddButtonClicked() {
        emit(HideSoftKeyboard, ShowProgressDialog)
        val pairedBluetoothDevice = selectedBluetoothDevice?.let {
            PairedBluetoothDevice(it.address, it.name)
        }
        val car = Car(
            carNumber,
            carModelName,
            carImageUri?.path,
            pairedBluetoothDevice
        )
        viewModelScope.launch {
            carUseCase.insert(car)
                .onFailure { emit(DismissProgressDialog, ShowFailToAddCarToast) }
                .onSuccess { emit(DismissProgressDialog, FinishActivity) }
        }
    }

    fun onImageActionSelected(@IntRange(from = 0, to = 2) which: Int) =
        when (which) {
            CAPTURE_IMAGE_WHICH -> {
                emit(ShowProgressDialog, CreateImageFileAsync)
            }
            IMAGE_GALLERY_WHICH -> {
                emit(StartImageGalleryActivity)
            }
            DELETE_IMAGE_WHICH -> {
                carImageUri = null
                emit(SetCarThumbnailImageButtonAsync(carImageUri))
            }
            else -> {
                error("Invalid which=$which")
            }
        }.exhaustive()

    fun onCreateImageFileAsyncFailed() = emit(DismissProgressDialog, ShowFailToLoadImageToast)

    fun onCreateImageFileAsyncSucceed(uri: Uri) {
        carImageUri = uri
        emit(DismissProgressDialog, StartImageCaptureActivity(uri))
    }

    @SuppressLint(value = ["MissingPermission"])
    fun onBluetoothDeviceSelected(device: BluetoothDevice?) {
        selectedBluetoothDevice = device
        emit(SetCarBluetoothDeviceButton(selectedBluetoothDevice?.name))
    }

    fun onImageCaptureActivityResult(@ResultCode resultCode: Int) {
        if (resultCode != RESULT_OK) return
        carImageUri?.let { emit(SetCarThumbnailImageButtonAsync(it)) }
    }

    fun onImageGalleryActivityResult(@ResultCode resultCode: Int, uri: Uri?) {
        if (resultCode != RESULT_OK) return
        uri?.let { emit(CopyGalleryImageAsync(it)) }
    }

    fun onCopyGalleryImageAsyncFailed() = emit(ShowFailToLoadImageToast)

    fun onCopyGalleryImageAsyncSucceed(uri: Uri) {
        carImageUri = uri
        emit(SetCarThumbnailImageButtonAsync(uri))
    }

    fun onActivateBluetoothActivityResult(@ResultCode resultCode: Int) {
        if (resultCode != RESULT_OK) return emit(ShowBluetoothActivationRequiredToast)
    }

    private fun emit(vararg actions: AddCarViewAction) = actions.forEach {
        when (it) {
            is View -> _viewAction.value = it
            is State -> _stateAction.value = it
            is Activity -> _activityAction.value = it
            is Io -> _ioAction.value = it
        }.exhaustive()
    }

    private fun isAddButtonEnabled(carModelName: String, carNumber: String): Boolean =
        !carModelName.isBlank() && !carNumber.isBlank()

    companion object {
        private const val CAPTURE_IMAGE_WHICH = 0
        private const val IMAGE_GALLERY_WHICH = 1
        private const val DELETE_IMAGE_WHICH = 2
    }
}
