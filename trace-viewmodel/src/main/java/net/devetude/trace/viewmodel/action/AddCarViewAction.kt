package net.devetude.trace.viewmodel.action

import android.net.Uri

sealed class AddCarViewAction {
    sealed class State : AddCarViewAction() {
        object SetBluetoothEnabled : State()

        object SetCameraEnabled : State()

        object SetSelectableBluetoothDevices : State()
    }

    sealed class View : AddCarViewAction() {
        object InitViews : View()

        object ShowFailToLoadImageToast : View()

        object ShowSelectableBluetoothDeviceDialog : View()

        object ShowNoSelectableBluetoothDeviceToast : View()

        object ShowBluetoothActivationRequiredToast : View()

        object ShowProgressDialog : View()

        object DismissProgressDialog : View()

        object ShowFailToAddCarToast : View()

        object HideSoftKeyboard : View()

        data class ShowImageActionDialog(val isDeleteImageSelectable: Boolean) : View()

        data class SetCarBluetoothDeviceButton(val text: String?) : View()

        data class EnableAddButton(val value: Boolean) : View()

        data class SetCarThumbnailImageButtonAsync(val uri: Uri?) : View()
    }

    sealed class Io : AddCarViewAction() {
        object CreateImageFileAsync : Io()

        data class CopyGalleryImageAsync(val uri: Uri) : Io()
    }

    sealed class Activity : AddCarViewAction() {
        object StartImageGalleryActivity : Activity()

        object StartBluetoothEnableRequestActivity : Activity()

        object FinishActivity : Activity()

        class StartImageCaptureActivity(val uri: Uri) : Activity()
    }
}
