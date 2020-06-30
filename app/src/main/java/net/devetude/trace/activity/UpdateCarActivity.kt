package net.devetude.trace.activity

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.FEATURE_CAMERA_ANY
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import net.devetude.trace.R
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.common.extension.getUriOf
import net.devetude.trace.common.extension.hideSoftKeyboard
import net.devetude.trace.common.extension.showShortToast
import net.devetude.trace.common.util.GalleryImageFileCopier
import net.devetude.trace.common.util.ImageFileCreator
import net.devetude.trace.databinding.ActivityUpdateCarBinding
import net.devetude.trace.model.ImageActionType
import net.devetude.trace.viewmodel.UpdateCarViewModel
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.StartBluetoothEnableRequestActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.StartImageCaptureActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Activity.StartImageGalleryActivity
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Io.CopyGalleryImageAsync
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.Io.CreateImageFileAsync
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.State.SetBluetoothEnabled
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.State.SetCameraEnabled
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.State.SetSelectableBluetoothDevices
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.DismissProgressDialog
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.HideSoftKeyboard
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowBluetoothActivationRequiredToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowFailToLoadImageToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowFailToUpdateCarToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowImageActionDialog
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowNoSelectableBluetoothDeviceToast
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowProgressDialog
import net.devetude.trace.viewmodel.action.UpdateCarViewAction.View.ShowSelectableBluetoothDeviceDialog
import org.koin.android.viewmodel.ext.android.viewModel

class UpdateCarActivity : AppCompatActivity() {
    private val viewModel: UpdateCarViewModel by viewModel()
    private val imageFileCreator: ImageFileCreator by lazy { ImageFileCreator(context = this) }
    private val galleryImageFileCopier: GalleryImageFileCopier by lazy {
        GalleryImageFileCopier(contentResolver, imageFileCreator)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private lateinit var binding: ActivityUpdateCarBinding

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        observeActions()
        viewModel.onCreate(intent.getStringExtra(CAR_NUMBER_KEY))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_CAPTURE_REQ_CODE -> {
                viewModel.onImageCaptureActivityResult(resultCode)
            }
            IMAGE_GALLERY_REQ_CODE -> {
                viewModel.onImageGalleryActivityResult(resultCode, data?.data)
            }
            ACTIVATE_BLUETOOTH_REQ_CODE -> {
                viewModel.onActivateBluetoothActivityResult(resultCode)
            }
            else -> {
                error("Undefined requestCode=$requestCode")
            }
        }.exhaustive()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView<ActivityUpdateCarBinding>(
            this /* activity */,
            R.layout.activity_update_car
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
    }

    private fun observeActions() = with(viewModel) {
        viewAction.observe(
            this@UpdateCarActivity /* owner */,
            Observer {
                when (it) {
                    HideSoftKeyboard -> {
                        hideSoftKeyboard(binding.root)
                    }
                    is ShowImageActionDialog -> {
                        showImageActionDialog(it.isDeleteImageSelectable)
                    }
                    ShowProgressDialog -> {
                        showProgressDialog()
                    }
                    DismissProgressDialog -> {
                        dismissProgressDialog()
                    }
                    ShowFailToLoadImageToast -> {
                        showShortToast(R.string.failed_to_load_the_picture)
                    }
                    ShowNoSelectableBluetoothDeviceToast -> {
                        showShortToast(R.string.not_paired_devices)
                    }
                    ShowSelectableBluetoothDeviceDialog -> {
                        showSelectableBluetoothDeviceDialog()
                    }
                    ShowBluetoothActivationRequiredToast -> {
                        showShortToast(R.string.bluetooth_activation)
                    }
                    ShowFailToUpdateCarToast -> {
                        showShortToast(R.string.failed_to_update_car)
                    }
                }.exhaustive()
            }
        )
        stateAction.observe(
            this@UpdateCarActivity /* owner */,
            Observer {
                when (it) {
                    SetCameraEnabled -> {
                        setCameraEnabled()
                    }
                    SetBluetoothEnabled -> {
                        setBluetoothEnabled()
                    }
                    SetSelectableBluetoothDevices -> {
                        setSelectableBluetoothDevices()
                    }
                }.exhaustive()
            }
        )
        activityAction.observe(
            this@UpdateCarActivity /* owner */,
            Observer {
                when (it) {
                    StartImageGalleryActivity -> {
                        startImageGalleryActivity()
                    }
                    is StartImageCaptureActivity -> {
                        startImageCaptureActivity(it.uri)
                    }
                    StartBluetoothEnableRequestActivity -> {
                        startBluetoothEnableRequestActivity()
                    }
                    FinishActivity -> {
                        finish()
                    }
                }.exhaustive()
            }
        )
        ioAction.observe(
            this@UpdateCarActivity /* owner */,
            Observer {
                when (it) {
                    CreateImageFileAsync -> {
                        createImageFileAsync()
                    }
                    is CopyGalleryImageAsync -> {
                        copyGalleryImageAsync(it.uri)
                    }
                }
            }
        )
    }

    private fun setCameraEnabled() {
        viewModel.isCameraEnabled = packageManager.hasSystemFeature(FEATURE_CAMERA_ANY)
    }

    private fun startImageGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_GALLERY_REQ_CODE)
    }

    private fun showImageActionDialog(isDeleteImageSelectable: Boolean) {
        val adapter = ArrayAdapter<String>(
            this /* context */,
            android.R.layout.simple_list_item_1,
            ImageActionType.of(isDeleteImageSelectable).stringResources.map(::getString)
        )
        MaterialAlertDialogBuilder(this /* context */)
            .setAdapter(adapter) { _: DialogInterface, which: Int ->
                viewModel.onImageActionSelected(which)
            }
            .create()
            .show()
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this /* context */).apply {
            setCancelable(false)
            setMessage(getString(R.string.loading))
        }
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun createImageFileAsync() {
        lifecycleScope.launch {
            imageFileCreator.createAsync()
                .onFailure { viewModel.onCreateImageFileAsyncFailed() }
                .onSuccess { viewModel.onCreateImageFileAsyncSucceed(getUriOf(it)) }
        }
    }

    private fun startImageCaptureActivity(uri: Uri) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, IMAGE_CAPTURE_REQ_CODE)
    }

    private fun copyGalleryImageAsync(uri: Uri) {
        lifecycleScope.launch {
            galleryImageFileCopier.copyAsync(uri)
                .onFailure { viewModel.onCopyGalleryImageAsyncFailed() }
                .onSuccess { viewModel.onCopyGalleryImageAsyncSucceed(getUriOf(it)) }
        }
    }

    private fun startBluetoothEnableRequestActivity() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, ACTIVATE_BLUETOOTH_REQ_CODE)
    }

    private fun setBluetoothEnabled() {
        viewModel.isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
    }

    private fun setSelectableBluetoothDevices() {
        viewModel.selectableBluetoothDevices = bluetoothAdapter?.bondedDevices.orEmpty().toList()
    }

    private fun showSelectableBluetoothDeviceDialog() {
        val selectableBluetoothDevices = viewModel.selectableBluetoothDevices
        val adapter = ArrayAdapter<String>(
            this /* context */,
            android.R.layout.simple_list_item_1,
            selectableBluetoothDevices.map { it?.name ?: getString(R.string.not_selected) }
        )
        MaterialAlertDialogBuilder(this /* context */)
            .setAdapter(adapter) { _: DialogInterface, which: Int ->
                viewModel.onBluetoothDeviceSelected(selectableBluetoothDevices[which])
            }
            .create()
            .show()
    }

    companion object {
        private const val CAR_NUMBER_KEY = "CAR_NUMBER_KEY"

        private const val IMAGE_CAPTURE_REQ_CODE = 1
        private const val IMAGE_GALLERY_REQ_CODE = 2
        private const val ACTIVATE_BLUETOOTH_REQ_CODE = 3

        fun createIntent(context: Context, carNumber: String): Intent =
            Intent(context, UpdateCarActivity::class.java).putExtra(CAR_NUMBER_KEY, carNumber)
    }
}
