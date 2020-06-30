package net.devetude.trace.common.util

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream

class GalleryImageFileCopier(
    private val contentResolver: ContentResolver,
    private val imageFileCreator: ImageFileCreator
) {
    suspend fun copyAsync(uri: Uri): Result<File> = withContext(Dispatchers.IO) {
        runCatching { copy(uri) }
    }

    private fun copy(uri: Uri): File {
        val file = imageFileCreator.create()
        val inputStream = contentResolver.openInputStream(uri)
            ?: error("Fail to open input stream")
        inputStream.use { FileOutputStream(file).use { IOUtils.copy(inputStream, it) } }
        return file
    }
}
