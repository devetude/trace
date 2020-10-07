package net.devetude.trace.viewmodel

import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Point
import android.net.Uri
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.launch
import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude
import net.devetude.trace.common.annotation.ParkingFloor
import net.devetude.trace.common.annotation.ResultCode
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.entity.Car
import net.devetude.trace.entity.History.ParkingHistory
import net.devetude.trace.entity.Location
import net.devetude.trace.entity.TextElement
import net.devetude.trace.usecase.car.CarUseCase
import net.devetude.trace.usecase.geocoding.GeocodingUseCase
import net.devetude.trace.usecase.history.HistoryUseCase
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Activity
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Activity.StartImageCaptureActivity
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Activity.StartImageGalleryActivity
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Io
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Io.CopyGalleryImageAsync
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Io.CreateImageFileAsync
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Io.RecognizeText
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.CheckLocationPermissions
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.RequestLocationPermissions
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.RequestLocationUpdates
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.SetAnalysisParkingThumbnailImageOption
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.SetCameraEnabled
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.StopLocationUpdates
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.DismissProgressDialog
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.HideSoftKeyboard
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.InitParkingLocationMap
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.InitViews
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.SetAddButton
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.SetParkingFloorButton
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.SetParkingLocationAddress
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.SetParkingLocationMap
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.SetParkingSpaceTextInputEditText
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.SetParkingThumbnailImageButtonAsync
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.SetSelectedCarButton
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowCheckLocationActivationToast
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowCheckLocationPermissionsToast
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowFailToAddParkingHistoryToast
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowFailToLoadImageToast
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowImageActionDialog
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowParkingLocationMapAndAddressGroup
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowProgressDialog
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowSelectableCarsDialog
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.View.ShowSelectableParkingFloorsDialog
import kotlin.math.pow
import kotlin.math.sqrt

class AddParkingHistoryViewModel(
    private val carUseCase: CarUseCase,
    private val historyUseCase: HistoryUseCase,
    private val geocodingUseCase: GeocodingUseCase
) : ViewModel() {
    private val _viewAction: MutableLiveData<View> = MutableLiveData()
    val viewAction: LiveData<View> = _viewAction

    private val _stateAction: MutableLiveData<State> = MutableLiveData()
    val stateAction: LiveData<State> = _stateAction

    private val _activityAction: MutableLiveData<Activity> = MutableLiveData()
    val activityAction: LiveData<Activity> = _activityAction

    private val _ioAction: MutableLiveData<Io> = MutableLiveData()
    val ioAction: LiveData<Io> = _ioAction

    private var selectedCar: Car? = null
    private var parkingLocation: Location = Location()
    private var parkingImageUri: Uri? = null
    var isCameraEnabled: Boolean = false
    var shouldAnalysisParkingThumbnailImage: Boolean = false

    fun onCreate(carNumber: String?) {
        emit(InitViews, CheckLocationPermissions)
        if (carNumber.isNullOrBlank()) return
        viewModelScope.launch {
            carUseCase.selectCarBy(carNumber)?.let {
                selectedCar = it
                emit(SetSelectedCarButton(selectedCar), SetAddButton(isAddButtonEnabled()))
            }
        }
    }

    fun onPause() = emit(StopLocationUpdates)

    fun onSelectedCarButtonClicked() {
        emit(HideSoftKeyboard, ShowProgressDialog)
        viewModelScope.launch {
            val cars = carUseCase.selectAllCars()
            if (cars.isNotEmpty()) {
                val selectableCars = listOf(null) + cars
                emit(ShowSelectableCarsDialog(selectableCars))
            }
            emit(DismissProgressDialog)
        }
    }

    fun onCarSelected(car: Car?) {
        selectedCar = car
        emit(SetSelectedCarButton(selectedCar), SetAddButton(isAddButtonEnabled()))
    }

    fun onParkingThumbnailImageButtonClicked() {
        emit(HideSoftKeyboard, SetCameraEnabled)
        if (!isCameraEnabled) return emit(StartImageGalleryActivity)
        emit(ShowImageActionDialog(isDeleteImageSelectable = parkingImageUri != null))
    }

    fun onImageActionSelected(@IntRange(from = 0, to = 2) which: Int) = when (which) {
        CAPTURE_IMAGE_WHICH -> emit(ShowProgressDialog, CreateImageFileAsync)
        IMAGE_GALLERY_WHICH -> emit(StartImageGalleryActivity)
        DELETE_IMAGE_WHICH -> {
            parkingImageUri = null
            emit(SetParkingThumbnailImageButtonAsync(parkingImageUri))
        }
        else -> error("Invalid which=$which")
    }.exhaustive()

    fun onCreateImageFileAsyncFailed() = emit(DismissProgressDialog, ShowFailToLoadImageToast)

    fun onCreateImageFileAsyncSucceed(uri: Uri) {
        parkingImageUri = uri
        emit(DismissProgressDialog, StartImageCaptureActivity(uri))
    }

    fun onImageCaptureActivityResult(@ResultCode resultCode: Int) {
        if (resultCode != RESULT_OK) return
        parkingImageUri?.let {
            emit(SetParkingThumbnailImageButtonAsync(it), SetAnalysisParkingThumbnailImageOption)
            if (shouldAnalysisParkingThumbnailImage) emit(RecognizeText(it))
        }
    }

    fun onImageGalleryActivityResult(@ResultCode resultCode: Int, uri: Uri?) {
        if (resultCode != RESULT_OK) return
        uri?.let { emit(CopyGalleryImageAsync(it)) }
    }

    fun onCopyGalleryImageAsyncFailed() = emit(ShowFailToLoadImageToast)

    fun onCopyGalleryImageAsyncSucceed(uri: Uri) {
        parkingImageUri = uri
        emit(SetParkingThumbnailImageButtonAsync(uri), SetAnalysisParkingThumbnailImageOption)
        if (shouldAnalysisParkingThumbnailImage) emit(RecognizeText(uri))
    }

    fun onSelectedParkingFloorButtonClicked() =
        emit(HideSoftKeyboard, ShowSelectableParkingFloorsDialog)

    fun onParkingFloorSelected(@ParkingFloor floor: Int) {
        parkingLocation = parkingLocation.copy(floor = floor)
        emit(SetParkingFloorButton(floor))
    }

    fun onParkingSpaceTextInputEditTextChanged(space: String) {
        parkingLocation = parkingLocation.copy(space = space)
    }

    fun onLocationPermissionsPermitted() =
        emit(ShowParkingLocationMapAndAddressGroup, RequestLocationUpdates)

    fun onLocationDisabled() = emit(ShowCheckLocationActivationToast)

    fun onLocationPermissionsDeniedBefore() = emit(ShowCheckLocationPermissionsToast)

    fun onLocationPermissionsRequestRequired() = emit(RequestLocationPermissions)

    fun onRequestLocationPermissionsResult(grantResults: IntArray) {
        if (!grantResults.all { it == PERMISSION_GRANTED }) return
        emit(ShowParkingLocationMapAndAddressGroup, RequestLocationUpdates)
    }

    fun onLocationCoordinateChanged(@Latitude latitude: Double?, @Longitude longitude: Double?) {
        if (latitude == null || longitude == null) return
        if (latitude == parkingLocation.latitude && longitude == parkingLocation.longitude) return
        parkingLocation = parkingLocation.copy(latitude = latitude, longitude = longitude)
        emit(SetParkingLocationMap(latitude, longitude))
        viewModelScope.launch {
            geocodingUseCase.requestAddress(latitude, longitude)
                .onFailure { emit(SetParkingLocationAddress(address = "")) }
                .onSuccess {
                    val address = it.toAddress()
                    if (address.isNotBlank()) {
                        parkingLocation = parkingLocation.copy(address = address)
                    }
                    emit(SetParkingLocationAddress(address))
                }
        }
    }

    fun onMapReady() {
        val latitude = parkingLocation.latitude ?: return
        val longitude = parkingLocation.longitude ?: return
        emit(InitParkingLocationMap, SetParkingLocationMap(latitude, longitude))
    }

    fun onAddButtonClicked() {
        val selectedCarNumber = selectedCar?.number ?: error("selectedCar is null")
        emit(ShowProgressDialog)
        viewModelScope.launch {
            val parkingHistory = ParkingHistory(
                selectedCarNumber,
                parkingImageUri?.path,
                parkingLocation
            )
            runCatching { historyUseCase.insert(parkingHistory) }
                .onFailure { emit(DismissProgressDialog, ShowFailToAddParkingHistoryToast) }
                .onSuccess { emit(DismissProgressDialog, FinishActivity) }
        }
    }

    fun onRecognizeTextSucceed(text: Text, imageCenterPoint: Point) {
        val textElements = text.toTextElements()
        val selectedCarNumber = selectedCar?.number
        val floorTextElements = textElements.filterNotFloor()
        val centerPoint1 = selectedCarNumber
            ?.let { floorTextElements.firstOrNull()?.centerPoint }
            ?: imageCenterPoint
        val floorTextElement = floorTextElements.getClosestElementFrom(centerPoint1)
        val floor = floorTextElement.toFloor()
        parkingLocation = parkingLocation.copy(floor = floor)
        emit(SetParkingFloorButton(floor))
        val centerPoint2 = floorTextElement?.centerPoint ?: centerPoint1
        val space = textElements.filterFloor().getClosestElementFrom(centerPoint2)?.text
        if (space.isNullOrBlank()) return
        parkingLocation = parkingLocation.copy(space = space)
        emit(SetParkingSpaceTextInputEditText(space))
    }

    private fun Text.toTextElements(): List<TextElement> {
        val textElements = mutableListOf<TextElement>()
        for (textBlock in textBlocks) {
            for (line in textBlock.lines) {
                for (element in line.elements) {
                    val centerPoint = element.boundingBox
                        ?.let { Point(it.centerX(), it.centerY()) }
                        ?: continue
                    textElements.add(TextElement(element.text, centerPoint))
                }
            }
        }
        return textElements
    }

    private fun List<TextElement>.filterNotFloor(): List<TextElement> =
        filter { it.text.matches(BASEMENT_REGEX) || it.text.matches(GROUND_REGEX) }

    private fun List<TextElement>.filterFloor(): List<TextElement> =
        filter { !it.text.matches(BASEMENT_REGEX) && !it.text.matches(GROUND_REGEX) }

    private fun List<TextElement>.getClosestElementFrom(point: Point): TextElement? =
        sortedWith { a: TextElement, b: TextElement ->
            val aDist = a.centerPoint.getDistanceFrom(point)
            val bDist = b.centerPoint.getDistanceFrom(point)
            compareValues(aDist, bDist)
        }.firstOrNull()

    private fun Point.getDistanceFrom(point: Point): Double = sqrt(
        x = (x - point.x).toDouble().pow(x = 2.0) + (y - point.y).toDouble().pow(x = 2.0)
    )

    private fun TextElement?.toFloor(): Int = runCatching {
        val text = this@toFloor?.text ?: return 0
        return if (text.startsWith(BASEMENT_PREFIX)) {
            text.replace(BASEMENT_PREFIX, newValue = "").toInt().unaryMinus()
        } else {
            text.replace(GROUND_PREFIX, newValue = "").toInt()
        }
    }.getOrDefault(defaultValue = 0)

    private fun isAddButtonEnabled(): Boolean = selectedCar != null

    private fun emit(vararg actions: AddParkingHistoryAction) = actions.forEach {
        when (it) {
            is View -> _viewAction.value = it
            is State -> _stateAction.value = it
            is Activity -> _activityAction.value = it
            is Io -> _ioAction.value = it
        }.exhaustive()
    }

    companion object {
        private const val CAPTURE_IMAGE_WHICH = 0
        private const val IMAGE_GALLERY_WHICH = 1
        private const val DELETE_IMAGE_WHICH = 2

        private const val BASEMENT_PREFIX = "B"
        private const val GROUND_PREFIX = "F"

        private val BASEMENT_REGEX = Regex(pattern = "^$BASEMENT_PREFIX\\d{0,2}\$")
        private val GROUND_REGEX = Regex(pattern = "^$GROUND_PREFIX\\d{0,2}\$")
    }
}
