package net.devetude.trace.holder.creator

import android.view.ViewGroup
import androidx.annotation.UiThread
import net.devetude.trace.common.extension.layoutInflater
import net.devetude.trace.databinding.ItemCarWithLastHistoryBinding
import net.devetude.trace.databinding.ItemEmptyBinding
import net.devetude.trace.holder.CarsViewHolder
import net.devetude.trace.holder.CarsViewHolder.CarViewLastHistoryViewHolder
import net.devetude.trace.holder.CarsViewHolder.EmptyViewHolder

sealed class CarsViewHolderCreator {
    @UiThread
    abstract fun create(parent: ViewGroup): CarsViewHolder

    object EmptyViewHolderCreator : CarsViewHolderCreator() {
        @UiThread
        override fun create(parent: ViewGroup): CarsViewHolder = EmptyViewHolder(
            ItemEmptyBinding.inflate(
                parent.context.layoutInflater,
                parent,
                false /* attachToParent */
            )
        )
    }

    object CarViewHolderCreator : CarsViewHolderCreator() {
        @UiThread
        override fun create(parent: ViewGroup): CarsViewHolder = CarViewLastHistoryViewHolder(
            ItemCarWithLastHistoryBinding.inflate(
                parent.context.layoutInflater,
                parent,
                false /* attachToParent */
            )
        )
    }
}
