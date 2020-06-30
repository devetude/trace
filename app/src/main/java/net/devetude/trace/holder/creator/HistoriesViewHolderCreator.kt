package net.devetude.trace.holder.creator

import android.view.ViewGroup
import androidx.annotation.UiThread
import net.devetude.trace.common.extension.layoutInflater
import net.devetude.trace.databinding.ItemDrivingHistoryBinding
import net.devetude.trace.databinding.ItemParkingHistoryBinding
import net.devetude.trace.holder.HistoriesViewHolder
import net.devetude.trace.holder.HistoriesViewHolder.DrivingHistoryViewHolder
import net.devetude.trace.holder.HistoriesViewHolder.ParkingHistoryViewHolder

sealed class HistoriesViewHolderCreator {
    @UiThread
    abstract fun create(parent: ViewGroup): HistoriesViewHolder

    object InvalidViewHolderCreator : HistoriesViewHolderCreator() {
        override fun create(parent: ViewGroup): HistoriesViewHolder = error("Can't create")
    }

    object ParkingHistoryViewHolderCreator : HistoriesViewHolderCreator() {
        @UiThread
        override fun create(parent: ViewGroup): HistoriesViewHolder = ParkingHistoryViewHolder(
            ItemParkingHistoryBinding.inflate(
                parent.context.layoutInflater,
                parent,
                false /* attachToParent */
            )
        )
    }

    object DrivingHistoryViewHolderCreator : HistoriesViewHolderCreator() {
        @UiThread
        override fun create(parent: ViewGroup): HistoriesViewHolder = DrivingHistoryViewHolder(
            ItemDrivingHistoryBinding.inflate(
                parent.context.layoutInflater,
                parent,
                false /* attachToParent */
            )
        )
    }
}
