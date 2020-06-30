package net.devetude.trace.viewmodel.action

import android.net.Uri
import androidx.annotation.Size
import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude
import net.devetude.trace.entity.Car

sealed class AddParkingHistoryAction {
    sealed class State : AddParkingHistoryAction() {
        object SetCameraEnabled : State()

        object CheckLocationPermissions : State()

        object RequestLocationUpdates : State()

        object RequestLocationPermissions : State()

        object StopLocationUpdates : State()

        object SetAnalysisParkingThumbnailImageOption : State()
    }

    sealed class View : AddParkingHistoryAction() {
        object InitViews : View()

        object InitParkingLocationMap : View()

        object ShowProgressDialog : View()

        object DismissProgressDialog : View()

        object HideSoftKeyboard : View()

        object ShowFailToLoadImageToast : View()

        object ShowFailToAddParkingHistoryToast : View()

        object ShowSelectableParkingFloorsDialog : View()

        object ShowCheckLocationActivationToast : View()

        object ShowCheckLocationPermissionsToast : View()

        object ShowParkingLocationMapAndAddressGroup : View()

        data class ShowImageActionDialog(val isDeleteImageSelectable: Boolean) : View()

        data class SetParkingThumbnailImageButtonAsync(val uri: Uri?) : View()

        data class SetSelectedCarButton(val selectedCar: Car?) : View()

        data class ShowSelectableCarsDialog(@Size(min = 1) val selectableCars: List<Car?>) : View()

        data class SetAddButton(val isEnabled: Boolean) : View()

        data class SetParkingFloorButton(val floor: Int) : View()

        data class SetParkingSpaceTextInputEditText(val space: String) : View()

        data class SetParkingLocationMap(
            @Latitude val latitude: Double,
            @Longitude val longitude: Double
        ) : View()

        data class SetParkingLocationAddress(@Size(min = 0) val address: String) : View()
    }

    sealed class Activity : AddParkingHistoryAction() {
        object StartImageGalleryActivity : Activity()

        object FinishActivity : Activity()

        data class StartImageCaptureActivity(val uri: Uri) : Activity()
    }

    sealed class Io : AddParkingHistoryAction() {
        object CreateImageFileAsync : Io()

        data class CopyGalleryImageAsync(val uri: Uri) : Io()

        data class RecognizeText(val uri: Uri) : Io()
    }
}
