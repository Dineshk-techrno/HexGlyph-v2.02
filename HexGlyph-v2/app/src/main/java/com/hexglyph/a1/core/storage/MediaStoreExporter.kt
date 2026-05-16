package com.hexglyph.a1.core.storage

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Saves [bitmap] as a lossless PNG into Pictures/HexGlyph via MediaStore.
     *
     * Supported range: Android 10 (API 29) through Android 15 (API 35).
     *  - RELATIVE_PATH  : API 29+  (minSdk = 29, always safe)
     *  - IS_PENDING     : API 29+  (atomic write guard, always enabled)
     *  - Dynamic colour : API 31+  (guarded in Theme.kt)
     *
     * @param bitmap    ARGB_8888 bitmap to export.
     * @param fileName  Base file name without extension.
     * @return          [ExportResult.Success] with the resulting [Uri], or [ExportResult.Failure].
     */
    @RequiresApi(Build.VERSION_CODES.Q)   // minSdk = 29 = Q, so this is always satisfied
    suspend fun exportPng(bitmap: Bitmap, fileName: String): ExportResult =
        withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                    put(MediaStore.Images.Media.MIME_TYPE,    "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/HexGlyph")
                    put(MediaStore.Images.Media.IS_PENDING, 1)   // atomic write — API 29+
                }

                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return@withContext ExportResult.Failure("MediaStore insert returned null")

                resolver.openOutputStream(uri)?.use { stream ->
                    val ok = bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.flush()
                    if (!ok) {
                        resolver.delete(uri, null, null)
                        return@withContext ExportResult.Failure("Bitmap.compress returned false")
                    }
                } ?: run {
                    resolver.delete(uri, null, null)
                    return@withContext ExportResult.Failure("openOutputStream returned null")
                }

                // Publish: clear IS_PENDING so the image is visible in the gallery
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)

                ExportResult.Success(uri, "$fileName.png")

            } catch (e: Exception) {
                ExportResult.Failure("Export failed: ${e.message}", e)
            }
        }
}
