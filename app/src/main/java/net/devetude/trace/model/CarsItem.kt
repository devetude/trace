package net.devetude.trace.model

import net.devetude.trace.entity.CarWithLastHistory
import net.devetude.trace.model.CarsViewType.CAR_WITH_LAST_HISTORY
import net.devetude.trace.model.CarsViewType.EMPTY

sealed class CarsItem(val viewType: CarsViewType) {
    object EmptyItem : CarsItem(EMPTY)

    data class CarWithLastHistoryItem(
        val carWithLastHistory: CarWithLastHistory
    ) : CarsItem(CAR_WITH_LAST_HISTORY)
}
