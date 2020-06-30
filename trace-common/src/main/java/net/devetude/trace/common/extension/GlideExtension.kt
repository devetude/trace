package net.devetude.trace.common.extension

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.devetude.trace.common.annotation.PositiveInt

suspend fun RequestManager.loadCircleCroppedBitmap(path: String): Bitmap? =
    withContext(Dispatchers.IO) {
        runCatching {
            asBitmap()
                .circleCrop()
                .thumbnail(0.1f /* sizeMultiplier */)
                .load(path)
                .submit()
                .get()
        }.getOrNull()
    }

fun RequestManager.loadRoundedCornersImage(
    uri: Uri?,
    @PositiveInt radius: Int,
    @DrawableRes placeholderRes: Int,
    imageView: ImageView
) = load(uri)
    .apply(createRoundedCornersOptions(radius))
    .placeholder(placeholderRes)
    .into(imageView)

fun RequestManager.loadRoundedCornersImage(
    path: String,
    @PositiveInt radius: Int,
    @DrawableRes placeholderRes: Int,
    imageView: ImageView
) = load(path)
    .apply(createRoundedCornersOptions(radius))
    .placeholder(placeholderRes)
    .into(imageView)

internal fun createRoundedCornersOptions(
    @PositiveInt radius: Int
): RequestOptions = RequestOptions().transform(
    CenterCrop(),
    RoundedCorners(radius)
)
