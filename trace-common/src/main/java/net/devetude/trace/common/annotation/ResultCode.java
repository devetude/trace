package net.devetude.trace.common.annotation;

import android.app.Activity;

import androidx.annotation.IntRange;

@IntRange(from = Activity.RESULT_OK, to = Activity.RESULT_FIRST_USER)
public @interface ResultCode {
}
