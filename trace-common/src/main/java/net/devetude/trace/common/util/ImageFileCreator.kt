package net.devetude.trace.common.util

import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import androidx.annotation.WorkerThread
import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.StandardCharsets

@Suppress(names = ["UnstableApiUsage"])
class ImageFileCreator(private val context: Context) {
    suspend fun createAsync(): Result<File> =
        withContext(Dispatchers.IO) { runCatching { create() } }

    @WorkerThread
    fun create(): File {
        val directory = context.getExternalFilesDir(DIRECTORY_PICTURES)
            ?: error("Fail to get external file directory")
        val prefix = System.currentTimeMillis().toString().toHashString().stretch()
        return File.createTempFile(prefix, SUFFIX, directory)
    }

    private fun String.toHashString(): String =
        HASH_FUNCTION.hashString("${HASH_SALT}$this", StandardCharsets.UTF_8).toString()

    private fun String.stretch(): String {
        var result = ""
        repeat(STRETCHING_COUNT) {
            result = result.toHashString()
        }
        return result
    }

    companion object {
        private val HASH_FUNCTION: HashFunction = Hashing.sha256()
        private const val HASH_SALT = "TRACE_CAR_IMAGE_"
        private const val STRETCHING_COUNT = 100

        private const val SUFFIX = ".jpg"
    }
}
