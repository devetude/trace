package net.devetude.trace.common.annotation;

import androidx.annotation.IntDef;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static androidx.core.app.NotificationCompat.PRIORITY_LOW;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

@IntDef({
        PRIORITY_DEFAULT,
        PRIORITY_LOW,
        PRIORITY_MIN,
        PRIORITY_HIGH,
        PRIORITY_MAX})
public @interface NotificationPriority {
}
