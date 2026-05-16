package com.hexglyph.a1.core.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BitmapLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Loads a [Bitmap] from a content [Uri] using scoped storage,
     * scaled down to fit within [maxDimension] while preserving aspect ratio.
     *
     * Always returns ARGB_8888 config regardless of source format.
     *
     * @throws IllegalArgumentException if the URI cannot be opened.
     */
    suspend fun load(uri: Uri, maxDimension: Int = 4096): Bitmap =
        withContext(Dispatchers.Default) {

            // Step 1: decode bounds only (no pixel allocation)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            } ?: throw IllegalArgumentException("Cannot open URI: $uri")

            // Step 2: compute safe inSampleSize
            options.inSampleSize      = computeSampleSize(options, maxDimension)
            options.inJustDecodeBounds = false
            options.inPreferredConfig  = Bitmap.Config.ARGB_8888

            // Step 3: decode pixels
            val sampled = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            } ?: throw IllegalArgumentException("Cannot open URI for decode: $uri")

            // Step 4: ensure ARGB_8888 (some sources return RGB_565)
            if (sampled.config == Bitmap.Config.ARGB_8888) {
                sampled
            } else {
                val converted = sampled.copy(Bitmap.Config.ARGB_8888, false)
                sampled.recycle()
                converted
            }
        }

    // -------------------------------------------------------------------------

    private fun computeSampleSize(options: BitmapFactory.Options, maxDim: Int): Int {
        val (w, h) = options.outWidth to options.outHeight
        var sample = 1
        while ((w / sample) > maxDim || (h / sample) > maxDim) {
            sample *= 2
        }
        return sample
    }
}
