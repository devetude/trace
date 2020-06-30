package net.devetude.trace.model

import androidx.annotation.IntRange
import net.devetude.trace.holder.creator.CarsViewHolderCreator
import net.devetude.trace.holder.creator.CarsViewHolderCreator.CarViewHolderCreator
import net.devetude.trace.holder.creator.CarsViewHolderCreator.EmptyViewHolderCreator

enum class CarsViewType(val viewHolderCreator: CarsViewHolderCreator) {
    EMPTY(EmptyViewHolderCreator),
    CAR_WITH_LAST_HISTORY(CarViewHolderCreator);

    companion object {
        private val ORDINAL_LOOKUP: Map<Int, CarsViewType> by lazy {
            values().associateBy { it.ordinal }
        }

        fun of(@IntRange(from = 0, to = 0) ordinal: Int): CarsViewType =
            ORDINAL_LOOKUP[ordinal] ?: error("Invalid ordinal=$ordinal")
    }
}
