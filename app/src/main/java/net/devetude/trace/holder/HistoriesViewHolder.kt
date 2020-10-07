package net.devetude.trace.holder

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.devetude.trace.R
import net.devetude.trace.common.extension.getLocale
import net.devetude.trace.common.extension.loadRoundedCornersImage
import net.devetude.trace.common.extension.toFormattedDateString
import net.devetude.trace.common.extension.toRelativeTimeSpanString
import net.devetude.trace.databinding.ItemDrivingHistoryBinding
import net.devetude.trace.databinding.ItemParkingHistoryBinding
import net.devetude.trace.entity.HistoryWithCar
import net.devetude.trace.entity.Location
import net.devetude.trace.model.ParkingFloorType
import java.util.Date

sealed class HistoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(historyWithCar: HistoryWithCar)

    abstract fun onViewRecycled()

    protected fun bindCarThumbnailImageView(imageView: ImageView, imagePath: String?) {
        imagePath ?: return
        Glide.with(itemView.context)
            .load(imagePath)
            .circleCrop()
            .thumbnail(0.1f /* sizeMultiplier */)
            .into(imageView)
    }

    protected fun bindCarModelNameAndNumberTextView(
        textView: TextView,
        modelName: String,
        number: String
    ) {
        textView.text =
            itemView.context.getString(R.string.car_model_name_and_number, modelName, number)
    }

    protected fun bindDateTextView(textView: TextView, date: Date) {
        textView.text = itemView.context.getString(
            R.string.relative_time_and_date,
            date.toRelativeTimeSpanString(),
            date.toFormattedDateString(itemView.context.getLocale())
        )
    }

    class ParkingHistoryViewHolder(
        private val binding: ItemParkingHistoryBinding
    ) : HistoriesViewHolder(binding.root) {
        override fun onBind(historyWithCar: HistoryWithCar) = with(historyWithCar) {
            bindCarThumbnailImageView(binding.carThumbnailImageView, car.imagePath)
            bindCarModelNameAndNumberTextView(
                binding.carModelNameAndNumberTextView,
                car.modelName,
                car.number
            )
            bindParkingLocationAddressTextView(history.parkingLocation?.address)
            bindParkingLocationFloorAndSpaceTextView(history.parkingLocation)
            bindingParkingThumbnailImageView(history.imagePath)
            bindDateTextView(binding.parkingDateTextView, history.createdDate)
        }

        override fun onViewRecycled() = with(Glide.with(itemView.context)) {
            clear(binding.carThumbnailImageView)
            clear(binding.parkingThumbnailImageView)
        }

        private fun bindParkingLocationAddressTextView(address: String?) {
            if (address.isNullOrBlank()) return
            binding.parkingLocationAddressTextView.apply {
                isVisible = true
                text = address
            }
        }

        @SuppressLint(value = ["SetTextI18n"])
        private fun bindParkingLocationFloorAndSpaceTextView(parkingLocation: Location?) {
            parkingLocation ?: return
            val floorText = parkingLocation.floor.toFloorTextOrEmpty()
            val spaceText = parkingLocation.space.orEmpty()
            binding.parkingLocationFloorAndSpaceTextView.apply {
                isVisible = !floorText.isBlank() || !spaceText.isBlank()
                text = "$floorText $spaceText".trim()
            }
        }

        private fun Int?.toFloorTextOrEmpty(): String = if (this != null && this != 0) {
            itemView.context.getString(ParkingFloorType.of(floor = this).stringRes)
        } else {
            ""
        }

        private fun bindingParkingThumbnailImageView(imagePath: String?) {
            binding.parkingThumbnailImageView.isVisible = imagePath != null
            imagePath ?: return
            Glide.with(itemView.context).loadRoundedCornersImage(
                imagePath,
                itemView.context.resources.getDimensionPixelSize(R.dimen.corner_round),
                R.drawable.ic_white_no_image,
                binding.parkingThumbnailImageView
            )
        }
    }

    class DrivingHistoryViewHolder(
        private val binding: ItemDrivingHistoryBinding
    ) : HistoriesViewHolder(binding.root) {
        override fun onBind(historyWithCar: HistoryWithCar) = with(historyWithCar) {
            bindCarThumbnailImageView(binding.carThumbnailImageView, car.imagePath)
            bindCarModelNameAndNumberTextView(
                binding.carModelNameAndNumberTextView,
                car.modelName,
                car.number
            )
            bindDateTextView(binding.drivingDateTextView, history.createdDate)
        }

        override fun onViewRecycled() =
            Glide.with(itemView.context).clear(binding.carThumbnailImageView)
    }
}
