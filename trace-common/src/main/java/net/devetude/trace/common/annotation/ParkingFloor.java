package net.devetude.trace.common.annotation;

import androidx.annotation.IntRange;

@IntRange(from = ParkingFloor.MIN_FLOOR, to = ParkingFloor.MAX_FLOOR)
public @interface ParkingFloor {
    int MIN_FLOOR = -9;
    int MAX_FLOOR = 9;
}
