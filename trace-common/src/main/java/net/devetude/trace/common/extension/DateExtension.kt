package net.devetude.trace.common.extension

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toRelativeTimeSpanString(): CharSequence = DateUtils.getRelativeTimeSpanString(
    time,
    System.currentTimeMillis(),
    DateUtils.SECOND_IN_MILLIS
)

private const val FORMATTED_DATE_PATTERN = "yy.MM.dd hh:mm a"

fun Date.toFormattedDateString(locale: Locale): String =
    SimpleDateFormat(FORMATTED_DATE_PATTERN, locale).format(this /* date */)
