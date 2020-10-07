package net.devetude.trace.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import net.devetude.trace.holder.CarsViewHolder
import net.devetude.trace.holder.CarsViewHolder.CarViewLastHistoryViewHolder
import net.devetude.trace.holder.CarsViewHolder.EmptyViewHolder
import net.devetude.trace.model.CarsItem
import net.devetude.trace.model.CarsItem.CarWithLastHistoryItem
import net.devetude.trace.model.CarsItem.EmptyItem
import net.devetude.trace.model.CarsViewType
import net.devetude.trace.viewmodel.CarsViewModel

class CarsAdapter(
    private val carsViewModel: CarsViewModel
) : ListAdapter<CarsItem, CarsViewHolder>(DIFFER) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsViewHolder =
        CarsViewType.of(viewType).viewHolderCreator.create(parent)

    override fun onBindViewHolder(holder: CarsViewHolder, position: Int) {
        val item = getItem(position)
        when {
            item is EmptyItem && holder is EmptyViewHolder -> holder.onBind()
            item is CarWithLastHistoryItem && holder is CarViewLastHistoryViewHolder ->
                holder.onBind(item, carsViewModel)
            else -> error("Undefined holder=$holder and item=$item")
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun submitList(list: List<CarsItem>?) {
        if (list == null || list.isEmpty()) return super.submitList(listOf(EmptyItem))
        super.submitList(list)
    }

    companion object {
        private val DIFFER: DiffUtil.ItemCallback<CarsItem> =
            object : DiffUtil.ItemCallback<CarsItem>() {
                override fun areItemsTheSame(
                    oldItem: CarsItem,
                    newItem: CarsItem
                ): Boolean = oldItem.viewType == newItem.viewType

                override fun areContentsTheSame(
                    oldItem: CarsItem,
                    newItem: CarsItem
                ): Boolean = oldItem == newItem
            }
    }
}
