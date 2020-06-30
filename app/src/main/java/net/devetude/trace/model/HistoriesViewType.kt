package net.devetude.trace.model

import androidx.annotation.IntRange
import net.devetude.trace.holder.creator.HistoriesViewHolderCreator
import net.devetude.trace.holder.creator.HistoriesViewHolderCreator.DrivingHistoryViewHolderCreator
import net.devetude.trace.holder.creator.HistoriesViewHolderCreator.InvalidViewHolderCreator
import net.devetude.trace.holder.creator.HistoriesViewHolderCreator.ParkingHistoryViewHolderCreator

enum class HistoriesViewType(val viewHolderCreator: HistoriesViewHolderCreator) {
    INVALID(InvalidViewHolderCreator),
    PARKING_HISTORY(ParkingHistoryViewHolderCreator),
    DRIVING_HISTORY(DrivingHistoryViewHolderCreator);

    companion object {
        private val ORDINAL_LOOKUP: Map<Int, HistoriesViewType> by lazy {
            values().associateBy { it.ordinal }
        }

        fun of(@IntRange(from = 0, to = 0) ordinal: Int): HistoriesViewType =
            ORDINAL_LOOKUP[ordinal] ?: error("Invalid ordinal=$ordinal")

        fun of(isParkingState: Boolean?): HistoriesViewType {
            isParkingState ?: return INVALID
            return if (isParkingState) PARKING_HISTORY else DRIVING_HISTORY
        }
    }
}
