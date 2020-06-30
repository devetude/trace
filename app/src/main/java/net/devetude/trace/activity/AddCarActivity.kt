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
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import net.devetude.trace.R
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.common.extension.getUriOf
import net.devetude.trace.common.extension.hideSoftKeyboard
import net.devetude.trace.common.extension.loadRoundedCornersImage
import net.devetude.trace.common.extension.showShortToast
import net.devetude.trace.common.util.GalleryImageFileCopier
import net.devetude.trace.common.util.ImageFileCreator
import net.devetude.trace.databinding.ActivityAddCarBinding
import net.devetude.trace.model.ImageActionType
import net.devetude.trace.viewmodel.AddCarViewModel
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.StartBluetoothEnableRequestActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.StartImageCaptureActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Activity.StartImageGalleryActivity
import net.devetude.trace.viewmodel.action.AddCarViewAction.Io.CopyGalleryImageAsync
import net.devetude.trace.viewmodel.action.AddCarViewAction.Io.CreateImageFileAsync
import net.devetude.trace.viewmodel.action.AddCarViewAction.State.SetBluetoothEnabled
import net.devetude.trace.viewmodel.action.AddCarViewAction.State.SetCameraEnabled
import net.devetude.trace.viewmodel.action.AddCarViewAction.State.SetSelectableBluetoothDevices
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
import org.koin.android.viewmodel.ext.android.viewModel

class AddCarActivity : AppCompatActivity() {
    private val viewModel: AddCarViewModel by viewModel()
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }
    private val imageFileCreator: ImageFileCreator by lazy { ImageFileCreator(context = this) }
    private val glideRequestManager: RequestManager by lazy {
        Glide.with(this /* activity */)
    }
    private val galleryImageFileCopier: GalleryImageFileCopier by lazy {
        GalleryImageFileCopier(contentResolver, imageFileCreator)
    }

    private lateinit var binding: ActivityAddCarBinding

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        observeActions()
        viewModel.onCreate()
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
        binding = DataBindingUtil.setContentView<ActivityAddCarBinding>(
            this /* activity */,
            R.layout.activity_add_car
        ).also { it.viewModel = viewModel }
    }

    private fun observeActions() = with(viewModel) {
        stateAction.observe(
            this@AddCarActivity /* owner */,
            Observer {
                when (it) {
                    SetBluetoothEnabled -> {
                        setBluetoothEnabled()
                    }
                    SetCameraEnabled -> {
                        setCameraEnabled()
                    }
                    SetSelectableBluetoothDevices -> {
                        setSelectableBluetoothDevices()
                    }
                }.exhaustive()
            }
        )
        viewAction.observe(
            this@AddCarActivity /* owner */,
            Observer {
                when (it) {
                    InitViews -> {
                        initViews()
                    }
                    is ShowImageActionDialog -> {
                        showImageActionDialog(it.isDeleteImageSelectable)
                    }
                    is EnableAddButton -> {
                        enableAddButton(it.value)
                    }
                    ShowSelectableBluetoothDeviceDialog -> {
                        showSelectableBluetoothDeviceDialog()
                    }
                    ShowNoSelectableBluetoothDeviceToast -> {
                        showShortToast(R.string.not_paired_devices)
                    }
                    ShowFailToLoadImageToast -> {
                        showShortToast(R.string.failed_to_load_the_picture)
                    }
                    is SetCarBluetoothDeviceButton -> {
                        setCarBluetoothDeviceButton(it.text)
                    }
                    is SetCarThumbnailImageButtonAsync -> {
                        setCarThumbnailImageButtonAsync(it.uri)
                    }
                    ShowBluetoothActivationRequiredToast -> {
                        showShortToast(R.string.bluetooth_activation)
                    }
                    ShowProgressDialog -> {
                        showProgressDialog()
                    }
                    DismissProgressDialog -> {
                        dismissProgressDialog()
                    }
                    ShowFailToAddCarToast -> {
                        showShortToast(R.string.failed_to_add_car)
                    }
                    HideSoftKeyboard -> {
                        hideSoftKeyboard(binding.root)
                    }
                }.exhaustive()
            }
        )
        activityAction.observe(
            this@AddCarActivity /* owner */,
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
            this@AddCarActivity /* owner */,
            Observer {
                when (it) {
                    CreateImageFileAsync -> {
                        createImageFileAsync()
                    }
                    is CopyGalleryImageAsync -> {
                        copyGalleryImageAsync(it.uri)
                    }
                }.exhaustive()
            }
        )
    }

    private fun initViews() {
        title = getString(R.string.add_a_new_car)
        actionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun enableAddButton(value: Boolean) {
        binding.addButton.isEnabled = value
    }

    private fun setSelectableBluetoothDevices() {
        viewModel.selectableBluetoothDevices = bluetoothAdapter?.bondedDevices.orEmpty().toList()
    }

    private fun setBluetoothEnabled() {
        viewModel.isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
    }

    private fun setCameraEnabled() {
        viewModel.isCameraEnabled = packageManager.hasSystemFeature(FEATURE_CAMERA_ANY)
    }

    private fun createImageFileAsync() {
        lifecycleScope.launch {
            imageFileCreator.createAsync()
                .onFailure { viewModel.onCreateImageFileAsyncFailed() }
                .onSuccess { viewModel.onCreateImageFileAsyncSucceed(getUriOf(it)) }
        }
    }

    private fun copyGalleryImageAsync(uri: Uri) {
        lifecycleScope.launch {
            galleryImageFileCopier.copyAsync(uri)
                .onFailure { viewModel.onCopyGalleryImageAsyncFailed() }
                .onSuccess { viewModel.onCopyGalleryImageAsyncSucceed(getUriOf(it)) }
        }
    }

    private fun startImageGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_GALLERY_REQ_CODE)
    }

    private fun startImageCaptureActivity(uri: Uri) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, IMAGE_CAPTURE_REQ_CODE)
    }

    private fun setCarBluetoothDeviceButton(text: String?) {
        binding.carBluetoothDeviceButton.text = text ?: getString(R.string.not_selected)
    }

    private fun startBluetoothEnableRequestActivity() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, ACTIVATE_BLUETOOTH_REQ_CODE)
    }

    private fun setCarThumbnailImageButtonAsync(uri: Uri?) {
        glideRequestManager.loadRoundedCornersImage(
            uri,
            resources.getDimensionPixelSize(R.dimen.corner_round),
            R.drawable.ic_white_camera,
            binding.carThumbnailImageButton
        )
    }

    companion object {
        private const val IMAGE_CAPTURE_REQ_CODE = 1
        private const val IMAGE_GALLERY_REQ_CODE = 2
        private const val ACTIVATE_BLUETOOTH_REQ_CODE = 3

        fun createIntent(context: Context): Intent = Intent(context, AddCarActivity::class.java)
    }
}
