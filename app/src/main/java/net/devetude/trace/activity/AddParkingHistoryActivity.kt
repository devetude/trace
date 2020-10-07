package net.devetude.trace.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.FEATURE_CAMERA_ANY
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.widget.ArrayAdapter
import androidx.annotation.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons.BLACK
import kotlinx.coroutines.launch
import net.devetude.trace.R
import net.devetude.trace.common.annotation.Latitude
import net.devetude.trace.common.annotation.Longitude
import net.devetude.trace.common.annotation.ParkingFloor
import net.devetude.trace.common.extension.exhaustive
import net.devetude.trace.common.extension.findMapFragmentById
import net.devetude.trace.common.extension.getBooleanSharedPreference
import net.devetude.trace.common.extension.getUriOf
import net.devetude.trace.common.extension.hideSoftKeyboard
import net.devetude.trace.common.extension.loadRoundedCornersImage
import net.devetude.trace.common.extension.locationManager
import net.devetude.trace.common.extension.showShortToast
import net.devetude.trace.common.util.GalleryImageFileCopier
import net.devetude.trace.common.util.ImageFileCreator
import net.devetude.trace.databinding.ActivityAddParkingHistoryBinding
import net.devetude.trace.entity.Car
import net.devetude.trace.location.LocationChangeListener
import net.devetude.trace.location.LocationPermissionChecker
import net.devetude.trace.location.LocationPermissionChecker.Companion.LOCATION_PERMISSIONS
import net.devetude.trace.location.LocationRequester
import net.devetude.trace.model.ImageActionType
import net.devetude.trace.model.LocationPermissionsCheckResult.DENIED_BEFORE
import net.devetude.trace.model.LocationPermissionsCheckResult.LOCATION_DISABLED
import net.devetude.trace.model.LocationPermissionsCheckResult.PERMITTED
import net.devetude.trace.model.LocationPermissionsCheckResult.REQUEST_REQUIRED
import net.devetude.trace.model.ParkingFloorType
import net.devetude.trace.viewmodel.AddParkingHistoryViewModel
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Activity.FinishActivity
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Activity.StartImageCaptureActivity
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Activity.StartImageGalleryActivity
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Io.CopyGalleryImageAsync
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Io.CreateImageFileAsync
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.Io.RecognizeText
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.CheckLocationPermissions
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.RequestLocationPermissions
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.RequestLocationUpdates
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.SetAnalysisParkingThumbnailImageOption
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.SetCameraEnabled
import net.devetude.trace.viewmodel.action.AddParkingHistoryAction.State.StopLocationUpdates
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
import org.koin.android.viewmodel.ext.android.viewModel

class AddParkingHistoryActivity : AppCompatActivity(), LocationChangeListener, OnMapReadyCallback {
    private val viewModel: AddParkingHistoryViewModel by viewModel()
    private val imageFileCreator: ImageFileCreator by lazy { ImageFileCreator(context = this) }
    private val galleryImageFileCopier: GalleryImageFileCopier by lazy {
        GalleryImageFileCopier(contentResolver, imageFileCreator)
    }
    private val glideRequestManager: RequestManager by lazy {
        Glide.with(this /* activity */)
    }
    private val locationRequester: LocationRequester by lazy {
        LocationRequester(locationManager, locationChangeListener = this)
    }
    private val locationPermissionChecker: LocationPermissionChecker by lazy {
        LocationPermissionChecker(activity = this)
    }
    private val parkingLocationMarker: Marker by lazy { Marker() }
    private val textRecognizer: TextRecognizer by lazy { TextRecognition.getClient() }

    private lateinit var binding: ActivityAddParkingHistoryBinding

    private var progressDialog: ProgressDialog? = null
    private var parkingLocationMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        observeActions()
        viewModel.onCreate(intent.getStringExtra(CAR_NUMBER_KEY))
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
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
            else -> {
                error("Undefined requestCode=$requestCode")
            }
        }.exhaustive()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSIONS_REQ_CODE -> {
                viewModel.onRequestLocationPermissionsResult(grantResults)
            }
            else -> {
                error("Undefined requestCode=$requestCode")
            }
        }
    }

    override fun onLocationCoordinateChanged(
        @Latitude latitude: Double,
        @Longitude longitude: Double
    ) = viewModel.onLocationCoordinateChanged(latitude, longitude)

    override fun onMapReady(parkingLocationMap: NaverMap) {
        this.parkingLocationMap = parkingLocationMap
        viewModel.onMapReady()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView<ActivityAddParkingHistoryBinding>(
            this /* activity */,
            R.layout.activity_add_parking_history
        ).also { it.viewModel = viewModel }
    }

    private fun observeActions() = with(viewModel) {
        viewAction.observe(
            this@AddParkingHistoryActivity /* owner */,
            Observer {
                when (it) {
                    InitViews -> {
                        initViews()
                    }
                    InitParkingLocationMap -> {
                        initParkingLocationMap()
                    }
                    is SetSelectedCarButton -> {
                        setSelectedCarButton(it.selectedCar)
                    }
                    is ShowSelectableCarsDialog -> {
                        showSelectableCarsDialog(it.selectableCars)
                    }
                    ShowProgressDialog -> {
                        showProgressDialog()
                    }
                    DismissProgressDialog -> {
                        progressDialog?.dismiss()
                    }
                    HideSoftKeyboard -> {
                        hideSoftKeyboard(binding.root)
                    }
                    is ShowImageActionDialog -> {
                        showImageActionDialog(it.isDeleteImageSelectable)
                    }
                    ShowFailToLoadImageToast -> {
                        showShortToast(R.string.failed_to_load_the_picture)
                    }
                    is SetParkingThumbnailImageButtonAsync -> {
                        setParkingThumbnailImageButtonAsync(it.uri)
                    }
                    is SetAddButton -> {
                        setAddButton(it.isEnabled)
                    }
                    ShowFailToAddParkingHistoryToast -> {
                        showShortToast(R.string.failed_to_add_parking_history)
                    }
                    ShowSelectableParkingFloorsDialog -> {
                        showSelectableParkingFloorsDialog()
                    }
                    is SetParkingFloorButton -> {
                        setParkingFloorButton(it.floor)
                    }
                    is SetParkingSpaceTextInputEditText -> {
                        setParkingSpaceTextInputEditText(it.space)
                    }
                    ShowCheckLocationActivationToast -> {
                        showShortToast(R.string.check_location_activation)
                    }
                    ShowCheckLocationPermissionsToast -> {
                        showShortToast(R.string.check_location_permissions)
                    }
                    ShowParkingLocationMapAndAddressGroup -> {
                        showParkingLocationMapAndAddressGroup()
                    }
                    is SetParkingLocationMap -> {
                        setParkingLocationMap(it.latitude, it.longitude)
                    }
                    is SetParkingLocationAddress -> {
                        setParkingLocationAddress(it.address)
                    }
                }.exhaustive()
            }
        )
        stateAction.observe(
            this@AddParkingHistoryActivity /* owner */,
            Observer {
                when (it) {
                    SetCameraEnabled -> {
                        setCameraEnabled()
                    }
                    CheckLocationPermissions -> {
                        checkLocationPermissions()
                    }
                    RequestLocationUpdates -> {
                        locationRequester.request()
                    }
                    RequestLocationPermissions -> {
                        requestLocationPermissions()
                    }
                    StopLocationUpdates -> {
                        stopLocationUpdates()
                    }
                    SetAnalysisParkingThumbnailImageOption -> {
                        setAnalysisParkingThumbnailImageOption()
                    }
                }.exhaustive()
            }
        )
        activityAction.observe(
            this@AddParkingHistoryActivity /* owner */,
            Observer {
                when (it) {
                    StartImageGalleryActivity -> {
                        startImageGalleryActivity()
                    }
                    is StartImageCaptureActivity -> {
                        startImageCaptureActivity(it.uri)
                    }
                    FinishActivity -> {
                        finish()
                    }
                }.exhaustive()
            }
        )
        ioAction.observe(
            this@AddParkingHistoryActivity /* owner */,
            Observer {
                when (it) {
                    CreateImageFileAsync -> {
                        createImageFileAsync()
                    }
                    is CopyGalleryImageAsync -> {
                        copyGalleryImageAsync(it.uri)
                    }
                    is RecognizeText -> {
                        recognizeText(it.uri)
                    }
                }.exhaustive()
            }
        )
    }

    private fun initViews() {
        initActionBar()
        initParkingMapFragment()
    }

    private fun initActionBar() {
        title = getString(R.string.add_parking_history)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initParkingMapFragment() = supportFragmentManager
        .findMapFragmentById(R.id.parking_location_map_fragment)
        ?.getMapAsync(this /* callback */)
        ?: MapFragment.newInstance().also {
            supportFragmentManager.beginTransaction()
                .add(R.id.parking_location_map_fragment, it)
                .commit()
        }

    private fun initParkingLocationMap() {
        parkingLocationMap?.uiSettings?.apply {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isZoomControlEnabled = true
            isIndoorLevelPickerEnabled = true
        }
    }

    private fun setSelectedCarButton(selectedCar: Car?) {
        binding.selectedCarButton.text = selectedCar.toStringOrNotSelected()
    }

    private fun showSelectableCarsDialog(@Size(min = 1) selectableCars: List<Car?>) {
        val adapter = ArrayAdapter<String>(
            this /* context */,
            android.R.layout.simple_list_item_1,
            selectableCars.map { it.toStringOrNotSelected() }
        )
        MaterialAlertDialogBuilder(this /* context */)
            .setAdapter(adapter) { _: DialogInterface, which: Int ->
                viewModel.onCarSelected(selectableCars[which])
            }
            .create()
            .show()
    }

    private fun Car?.toStringOrNotSelected(): String =
        this?.let { getString(R.string.car_model_name_and_number, it.modelName, it.number) }
            ?: getString(R.string.not_selected)

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this /* context */).apply {
            setCancelable(false)
            setMessage(getString(R.string.loading))
        }
        progressDialog?.show()
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

    private fun setCameraEnabled() {
        viewModel.isCameraEnabled =
            packageManager.hasSystemFeature(FEATURE_CAMERA_ANY)
    }

    private fun createImageFileAsync() {
        lifecycleScope.launch {
            imageFileCreator.createAsync()
                .onFailure { viewModel.onCreateImageFileAsyncFailed() }
                .onSuccess { viewModel.onCreateImageFileAsyncSucceed(getUriOf(it)) }
        }
    }

    private fun setParkingThumbnailImageButtonAsync(uri: Uri?) {
        glideRequestManager.loadRoundedCornersImage(
            uri,
            resources.getDimensionPixelSize(R.dimen.corner_round),
            R.drawable.ic_white_camera,
            binding.parkingThumbnailImageButton
        )
    }

    private fun copyGalleryImageAsync(uri: Uri) {
        lifecycleScope.launch {
            galleryImageFileCopier.copyAsync(uri)
                .onFailure { viewModel.onCopyGalleryImageAsyncFailed() }
                .onSuccess { viewModel.onCopyGalleryImageAsyncSucceed(getUriOf(it)) }
        }
    }

    private fun recognizeText(uri: Uri) {
        val inputImage = InputImage.fromFilePath(this, uri)
        val centerPoint = Point(inputImage.width / 2 /* x */, inputImage.height / 2 /* y */)
        textRecognizer.process(inputImage)
            .addOnSuccessListener { viewModel.onRecognizeTextSucceed(it, centerPoint) }
    }

    private fun setAddButton(isEnabled: Boolean) {
        binding.addButton.isEnabled = isEnabled
    }

    private fun showSelectableParkingFloorsDialog() {
        val adapter = ArrayAdapter<String>(
            this /* context */,
            android.R.layout.simple_list_item_1,
            ParkingFloorType.values().map { getString(it.stringRes) }
        )
        MaterialAlertDialogBuilder(this /* context */)
            .setAdapter(adapter) { _: DialogInterface, which: Int ->
                viewModel.onParkingFloorSelected(ParkingFloorType.values()[which].floor)
            }
            .create()
            .show()
    }

    private fun setParkingFloorButton(@ParkingFloor floor: Int) {
        val type = ParkingFloorType.of(floor)
        binding.parkingFloorButton.text = getString(type.stringRes)
    }

    private fun startImageGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_GALLERY_REQ_CODE)
    }

    private fun startImageCaptureActivity(uri: Uri) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, IMAGE_CAPTURE_REQ_CODE)
    }

    private fun checkLocationPermissions() {
        when (locationPermissionChecker.check()) {
            PERMITTED -> viewModel.onLocationPermissionsPermitted()
            LOCATION_DISABLED -> viewModel.onLocationDisabled()
            DENIED_BEFORE -> viewModel.onLocationPermissionsDeniedBefore()
            REQUEST_REQUIRED -> viewModel.onLocationPermissionsRequestRequired()
        }.exhaustive()
    }

    private fun requestLocationPermissions() = ActivityCompat.requestPermissions(
        this /* activity */,
        LOCATION_PERMISSIONS,
        LOCATION_PERMISSIONS_REQ_CODE
    )

    private fun stopLocationUpdates() = locationManager.removeUpdates(this /* listener */)

    private fun setAnalysisParkingThumbnailImageOption() {
        viewModel.shouldAnalysisParkingThumbnailImage =
            getBooleanSharedPreference(R.string.parking_image_analysis_key, defaultValue = false)
    }

    private fun showParkingLocationMapAndAddressGroup() {
        binding.parkingLocationMapAndAddressGroup.isVisible = true
    }

    private fun setParkingLocationMap(
        @Latitude latitude: Double,
        @Longitude longitude: Double
    ) {
        val map = parkingLocationMap ?: return
        val latLng = LatLng(latitude, longitude)
        parkingLocationMarker.apply {
            position = latLng
            icon = BLACK
            iconTintColor = getColor(R.color.colorAccent)
            this.map = map
        }
        val cameraUpdate = CameraUpdate.scrollTo(latLng)
        map.moveCamera(cameraUpdate)
    }

    private fun setParkingLocationAddress(@Size(min = 0) address: String) {
        binding.parkingLocationAddressTextView.text = if (address.isBlank()) {
            getString(R.string.current_location_is_unknown)
        } else {
            address
        }
    }

    private fun setParkingSpaceTextInputEditText(space: String) {
        binding.parkingSpaceTextInputEditText.text = SpannableStringBuilder(space)
    }

    companion object {
        private const val IMAGE_CAPTURE_REQ_CODE = 1
        private const val IMAGE_GALLERY_REQ_CODE = 2
        private const val LOCATION_PERMISSIONS_REQ_CODE = 3

        private const val CAR_NUMBER_KEY = "CAR_NUMBER_KEY"

        fun createIntent(context: Context, carNumber: String? = null): Intent =
            Intent(context, AddParkingHistoryActivity::class.java)
                .putExtra(CAR_NUMBER_KEY, carNumber)
    }
}
