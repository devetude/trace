package net.devetude.trace.common.extension

fun Boolean?.nullToFalse(): Boolean = this ?: false

fun <T> T.exhaustive(): T = this
