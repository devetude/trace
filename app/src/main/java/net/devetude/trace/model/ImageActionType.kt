package net.devetude.trace.model

import net.devetude.trace.R

enum class ImageActionType(
    val stringResources: List<Int>,
    val isDeleteImageSelectable: Boolean
) {
    IS_IMAGE_SET(
        listOf(R.string.take_picture, R.string.select_picture),
        isDeleteImageSelectable = false
    ),
    IS_NOT_IMAGE_SET(
        listOf(R.string.take_picture, R.string.select_picture, R.string.delete_image),
        isDeleteImageSelectable = true
    );

    companion object {
        private val IS_DELETE_IMAGE_SELECTABLE_LOOKUP: Map<Boolean, ImageActionType> by lazy {
            values().associateBy { it.isDeleteImageSelectable }
        }

        fun of(isDeleteImageSelectable: Boolean): ImageActionType =
            IS_DELETE_IMAGE_SELECTABLE_LOOKUP[isDeleteImageSelectable]
                ?: error("Undefined isDeleteImageSelectable=$isDeleteImageSelectable")
    }
}
