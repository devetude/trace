package net.devetude.trace.common.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import net.devetude.trace.common.binding.sam.OnPageSelectedListener

@BindingAdapter(value = ["onPageSelected"])
fun ViewPager.onPageSelected(listener: OnPageSelectedListener) =
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) = listener.onPageSelected(position)

        override fun onPageScrollStateChanged(state: Int) = Unit

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) = Unit
    })
