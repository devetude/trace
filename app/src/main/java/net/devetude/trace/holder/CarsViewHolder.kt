package net.devetude.trace.holder

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.devetude.trace.R
import net.devetude.trace.databinding.ItemCarWithLastHistoryBinding
import net.devetude.trace.databinding.ItemEmptyBinding
import net.devetude.trace.entity.CarWithLastHistory
import net.devetude.trace.entity.Location
import net.devetude.trace.entity.PairedBluetoothDevice
import net.devetude.trace.model.CarState
import net.devetude.trace.model.CarsItem.CarWithLastHistoryItem
import net.devetude.trace.model.PairedBluetoothDeviceState.NO_PAIRED
import net.devetude.trace.model.PairedBluetoothDeviceState.PAIRED
import net.devetude.trace.model.ParkingFloorType
import net.devetude.trace.viewmodel.CarsViewModel

sealed class CarsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class EmptyViewHolder(
        private val binding: ItemEmptyBinding
    ) : CarsViewHolder(binding.root) {
        fun onBind() {
            binding.emptyTextView.text = itemView.context.getString(R.string.empty_car)
        }
    }

    class CarViewLastHistoryViewHolder(
        private val binding: ItemCarWithLastHistoryBinding
    ) : CarsViewHolder(binding.root) {
        fun onBind(
            carWithLastHistoryItem: CarWithLastHistoryItem,
            carsViewModel: CarsViewModel
        ) = with(carWithLastHistoryItem.carWithLastHistory) {
            initBinding(carWithLastHistory = this, carsViewModel = carsViewModel)
            bindCarThumbnailImageView(car.imagePath)
            bindCarStateButton(lastHistory?.isParkingState)
            bindParkingLocationAddressButton(lastHistory?.parkingLocation?.address)
            bindPairedCarBluetoothDeviceImageView(car.pairedBluetoothDevice)
            bindParkingLocationFloorAndSpaceTextView(lastHistory?.parkingLocation)
            bindCarNumberTextView(car.number)
            bindCarModelNameTextView(car.modelName)
        }

        fun onViewRecycled() = Glide.with(itemView.context).clear(binding.carThumbnailImageView)

        private fun initBinding(
            carWithLastHistory: CarWithLastHistory,
            carsViewModel: CarsViewModel
        ) {
            binding.apply {
                this.carWithLastHistory = carWithLastHistory
                this.carsViewModel = carsViewModel
            }
        }

        private fun bindCarThumbnailImageView(imagePath: String?) {
            Glide.with(itemView.context)
                .load(imagePath)
                .placeholder(R.drawable.ic_white_no_image)
                .into(binding.carThumbnailImageView)
        }

        private fun bindCarStateButton(isParkingState: Boolean?) {
            binding.carStateButton.apply {
                val state = CarState.of(isParkingState)
                setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, state.drawableStartRes),
                    null /* top */,
                    null /* right */,
                    null /* bottom */
                )
                text = itemView.context.getString(state.textStringRes)
                contentDescription = itemView.context.getString(state.contentDescriptionStringRes)
            }
        }

        private fun bindParkingLocationAddressButton(address: String?) {
            binding.parkingLocationAddressButton.apply {
                isVisible = !address.isNullOrBlank()
                text = address
            }
        }

        @SuppressLint(value = ["SetTextI18n"])
        private fun bindParkingLocationFloorAndSpaceTextView(parkingLocation: Location?) {
            if (parkingLocation == null) {
                binding.parkingLocationFloorAndSpaceTextView.isVisible = false
                return
            }
            binding.parkingLocationFloorAndSpaceTextView.apply {
                val floorText = parkingLocation.floor.toFloorTextOrEmpty()
                val spaceText = parkingLocation.space.orEmpty()
                isVisible = !floorText.isBlank() || !spaceText.isBlank()
                text = "$floorText $spaceText".trim()
            }
        }

        private fun bindPairedCarBluetoothDeviceImageView(
            pairedBluetoothDevice: PairedBluetoothDevice?
        ) {
            binding.pairedCarBluetoothDeviceStateImageView.apply {
                val state = if (pairedBluetoothDevice == null) NO_PAIRED else PAIRED
                setImageDrawable(ContextCompat.getDrawable(context, state.drawableStartRes))
                contentDescription = itemView.context.getString(state.contentDescriptionStringRes)
            }
        }

        private fun bindCarNumberTextView(number: String) {
            binding.carNumberTextView.text = number
        }

        private fun bindCarModelNameTextView(modelName: String) {
            binding.carModelNameTextView.text = modelName
        }

        private fun Int.toFloorTextOrEmpty(): String = if (this != 0) {
            itemView.context.getString(ParkingFloorType.of(floor = this).stringRes)
        } else {
            ""
        }
    }
}
