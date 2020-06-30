package net.devetude.trace.common.binding

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import net.devetude.trace.common.annotation.ZeroOrPositiveFloat
import net.devetude.trace.common.binding.sam.OnClickListener
import net.devetude.trace.common.extension.createRoundedCornersOptions
import net.devetude.trace.common.extension.logClickEvent

@BindingAdapter(value = ["onClick", "targetName"], requireAll = false)
fun ImageButton.onClick(listener: OnClickListener, targetName: String?) = setOnClickListener {
    targetName?.let(context::logClickEvent)
    listener.onClick()
}

@BindingAdapter(value = ["src", "placeholder", "cornerRadius"])
fun ImageButton.src(
    imagePath: String?,
    placeholder: Drawable,
    @ZeroOrPositiveFloat radius: Float
) {
    Glide.with(context)
        .load(imagePath)
        .placeholder(placeholder)
        .apply(createRoundedCornersOptions(radius.toInt()))
        .into(this /* view */)
}
