package net.devetude.trace.common.annotation;

import androidx.annotation.FloatRange;

@FloatRange(from = Longitude.WEST, to = Longitude.EAST)
public @interface Longitude {
    double WEST = -180.0;
    double EAST = 180.0;
}
