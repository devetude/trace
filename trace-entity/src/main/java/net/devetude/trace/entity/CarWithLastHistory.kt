package net.devetude.trace.entity

import androidx.room.Embedded

data class CarWithLastHistory(
    @Embedded val car: Car,
    @Embedded val lastHistory: History?
)
