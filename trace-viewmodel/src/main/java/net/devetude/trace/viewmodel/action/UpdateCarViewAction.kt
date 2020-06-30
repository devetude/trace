package net.devetude.trace.viewmodel.action

import android.net.Uri

sealed class UpdateCarViewAction {
    sealed class State : UpdateCarViewAction() {
        object SetBluetoothEnabled : State()

        object SetCameraEnabled : State()

        object SetSelectableBluetoothDevices : State()
    }

    sealed class View : UpdateCarViewAction() {
        object ShowFailToLoadImageToast : View()

        object ShowSelectableBluetoothDeviceDialog : View()

        object ShowNoSelectableBluetoothDeviceToast : View()

        object ShowBluetoothActivationRequiredToast : View()

        object ShowFailToUpdateCarToast : View()

        object ShowProgressDialog : View()

        object DismissProgressDialog : View()

        object HideSoftKeyboard : View()

        data class ShowImageActionDialog(val isDeleteImageSelectable: Boolean) : View()
    }

    sealed class Io : UpdateCarViewAction() {
        object CreateImageFileAsync : Io()

        data class CopyGalleryImageAsync(val uri: Uri) : Io()
    }

    sealed class Activity : UpdateCarViewAction() {
        object StartImageGalleryActivity : Activity()

        object StartBluetoothEnableRequestActivity : Activity()

        object FinishActivity : Activity()

        class StartImageCaptureActivity(val uri: Uri) : Activity()
    }
}
