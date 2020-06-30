package net.devetude.trace.common.annotation;

import androidx.annotation.FloatRange;

@FloatRange(from = Latitude.SOUTH, to = Latitude.NORTH)
public @interface Latitude {
    double SOUTH = -90.0;
    double NORTH = 90.0;
}
