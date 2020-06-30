package net.devetude.trace.common.binding

import android.widget.Button
import androidx.databinding.BindingAdapter
import net.devetude.trace.common.binding.sam.OnClickListener
import net.devetude.trace.common.extension.logClickEvent

@BindingAdapter(value = ["onClick", "targetName"], requireAll = false)
fun Button.onClick(listener: OnClickListener, targetName: String?) = setOnClickListener {
    targetName?.let(context::logClickEvent)
    listener.onClick()
}
