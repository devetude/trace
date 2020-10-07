package net.devetude.trace.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import net.devetude.trace.entity.HistoryWithCar
import net.devetude.trace.holder.HistoriesViewHolder
import net.devetude.trace.model.HistoriesViewType

class HistoriesAdapter : PagedListAdapter<HistoryWithCar, HistoriesViewHolder>(DIFFER) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriesViewHolder =
        HistoriesViewType.of(viewType).viewHolderCreator.create(parent)

    override fun onBindViewHolder(holder: HistoriesViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.onBind(item)
    }

    override fun getItemViewType(position: Int): Int =
        HistoriesViewType.of(getItem(position)?.history?.isParkingState).ordinal

    override fun onViewRecycled(holder: HistoriesViewHolder) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }

    companion object {
        private val DIFFER: DiffUtil.ItemCallback<HistoryWithCar> =
            object : DiffUtil.ItemCallback<HistoryWithCar>() {
                override fun areItemsTheSame(
                    oldItem: HistoryWithCar,
                    newItem: HistoryWithCar
                ): Boolean = oldItem.history.id == newItem.history.id

                override fun areContentsTheSame(
                    oldItem: HistoryWithCar,
                    newItem: HistoryWithCar
                ): Boolean = oldItem == newItem
            }
    }
}
