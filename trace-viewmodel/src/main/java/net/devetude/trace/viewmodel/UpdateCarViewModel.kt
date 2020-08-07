package net.devetude.trace.viewmodel

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothDevice
import android.net.Uri
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.devetude.trace.common.annotation.ResultCode
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.PairedBluetoothDevice
import net.devetude.trace.usecase.car.CarUseCase
import net.devetude.trace.viewmodel.action.UpdateCarViewAction
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.StartBluetoothEnableRequestActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.StartImageCaptureActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.StartImageGalleryActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Io
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Io.CopyGalleryImageAsync
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Io.CreateImageFileAsync
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.State
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.State.SetBluetoothEnabled
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.State.SetCameraEnabled
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.State.SetSelectableBluetoothDevices
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.DismissProgressDialog
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.HideSoftKeyboard
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowBluetoothActivationRequiredToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowFailToLoadImageToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowFailToUpdateCarToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowImageActionDialog
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowNoSelectableBluetoothDeviceToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowProgressDialog
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowSelectableBluetoothDeviceDialog

class UpdateCarViewModel(private val carUseCase: CarUseCase) : ViewModel() {
    private val _viewAction: MutableLiveData<View> = MutableLiveData()
    val viewAction: LiveData<View> = _viewAction

    private val _stateAction: MutableLiveData<State> = MutableLiveData()
    val stateAction: LiveData<State> = _stateAction

    private val _activityAction: MutableLiveData<Activity> = MutableLiveData()
    val activityAction: LiveData<Activity> = _activityAction

    private val _ioAction: MutableLiveData<Io> = MutableLiveData()
    val ioAction: LiveData<Io> = _ioAction

    private val _car: MutableLiveData<Car> = MutableLiveData()
    val car: LiveData<Car> = _car

    val isUpdateButtonEnabled: LiveData<Boolean> =
        Transformations.map(car) { it.modelName.isNotBlank() }

    private var tmpCarImageUri: Uri? = null
    var isBluetoothEnabled: Boolean = false
    var isCameraEnabled: Boolean = false
    var selectableBluetoothDevices: List<BluetoothDevice?> = emptyList()

    fun onCreate(carNumber: String?) {
        require(!carNumber.isNullOrBlank()) { "carNumber=$carNumber is null or blank" }
        viewModelScope.launch { carUseCase.selectCarBy(carNumber)?.let { _car.value = it } }
    }

    fun onCarThumbnailImageButtonClicked() {
        emit(HideSoftKeyboard, SetCameraEnabled)
        if (!isCameraEnabled) return emit(StartImageGalleryActivity)
        val isDeleteImageSelectable = !car.value?.imagePath.isNullOrBlank()
        emit(ShowImageActionDialog(isDeleteImageSelectable))
    }

    fun onImageActionSelected(@IntRange(from = 0, to = 2) which: Int) = when (which) {
        CAPTURE_IMAGE_WHICH -> emit(ShowProgressDialog, CreateImageFileAsync)
        IMAGE_GALLERY_WHICH -> emit(StartImageGalleryActivity)
        DELETE_IMAGE_WHICH -> _car.value = car.value.getOrThrow().copy(imagePath = null)
        else -> error("Invalid which=$which")
    }.exhaustive()

    fun onCreateImageFileAsyncFailed() = emit(DismissProgressDialog, ShowFailToLoadImageToast)

    fun onCreateImageFileAsyncSucceed(uri: Uri) {
        tmpCarImageUri = uri
        emit(DismissProgressDialog, StartImageCaptureActivity(uri))
    }

    fun onActivateBluetoothActivityResult(@ResultCode resultCode: Int) {
        if (resultCode != RESULT_OK) return emit(ShowBluetoothActivationRequiredToast)
    }

    fun onImageCaptureActivityResult(@ResultCode resultCode: Int) {
        if (resultCode != RESULT_OK) return
        val carImageUri = tmpCarImageUri ?: return
        _car.value = car.value.getOrThrow().copy(imagePath = carImageUri.path)
        tmpCarImageUri = null
    }

    fun onImageGalleryActivityResult(@ResultCode resultCode: Int, uri: Uri?) {
        if (resultCode != RESULT_OK) return
        tmpCarImageUri = uri ?: return
        emit(CopyGalleryImageAsync(uri))
    }

    fun onCopyGalleryImageAsyncFailed() = emit(ShowFailToLoadImageToast)

    fun onCopyGalleryImageAsyncSucceed(uri: Uri) {
        _car.value = car.value.getOrThrow().copy(imagePath = uri.path)
        tmpCarImageUri = null
    }

    fun onCarModelNameChanged(carModelName: String) {
        val currentCar = car.value.getOrThrow()
        if (carModelName == currentCar.modelName) return
        _car.value = currentCar.copy(modelName = carModelName)
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
    fun onBluetoothDeviceSelected(device: BluetoothDevice?) {
        val pairedBluetoothDevice = device?.let(PairedBluetoothDevice.Companion::of)
        _car.value = car.value.getOrThrow().copy(pairedBluetoothDevice = pairedBluetoothDevice)
    }

    fun onUpdateButtonClicked() {
        emit(HideSoftKeyboard, ShowProgressDialog)
        viewModelScope.launch {
            carUseCase.update(car.value.getOrThrow())
                .onFailure { emit(DismissProgressDialog, ShowFailToUpdateCarToast) }
                .onSuccess { emit(DismissProgressDialog, FinishActivity) }
        }
    }

    private fun emit(vararg actions: UpdateCarViewAction) = actions.forEach {
        when (it) {
            is View -> _viewAction.value = it
            is State -> _stateAction.value = it
            is Activity -> _activityAction.value = it
            is Io -> _ioAction.value = it
        }.exhaustive()
    }

    private fun Car?.getOrThrow(): Car {
        this ?: error("car is null")
        return this
    }

    companion object {
        private const val CAPTURE_IMAGE_WHICH = 0
        private const val IMAGE_GALLERY_WHICH = 1
        private const val DELETE_IMAGE_WHICH = 2
    }
}
