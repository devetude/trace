package net.devetude.trace.model

import net.devetude.trace.common.annotation.PositiveFloat
import net.devetude.trace.common.annotation.PositiveInt

enum class LocationRequestConfiguration(
    @PositiveInt val intervalMs: Long,
    @PositiveFloat val minDistanceM: Float
) {
    DEBUG(intervalMs = 15_000L, minDistanceM = 50.0f),
    RELEASE(intervalMs = 30_000L, minDistanceM = 100.0f);

    companion object {
        fun of(isDebug: Boolean): LocationRequestConfiguration =
            if (isDebug) DEBUG else RELEASE
    }
}
