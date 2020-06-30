package net.devetude.trace.common.annotation;

import androidx.annotation.IntDef;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_MAX;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_MIN;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_NONE;

@IntDef({
        IMPORTANCE_NONE,
        IMPORTANCE_MIN,
        IMPORTANCE_LOW,
        IMPORTANCE_DEFAULT,
        IMPORTANCE_HIGH,
        IMPORTANCE_MAX})
public @interface NotificationImportance {
}
