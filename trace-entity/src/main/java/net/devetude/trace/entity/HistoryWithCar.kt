package net.devetude.trace.entity

import androidx.room.Embedded

data class HistoryWithCar(
    @Embedded val history: History,
    @Embedded val car: Car
)
