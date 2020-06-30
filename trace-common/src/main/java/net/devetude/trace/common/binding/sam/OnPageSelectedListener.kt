package net.devetude.trace.common.binding.sam

import net.devetude.trace.common.annotation.ZeroOrPositiveInt

interface OnPageSelectedListener {
    fun onPageSelected(@ZeroOrPositiveInt position: Int)
}
