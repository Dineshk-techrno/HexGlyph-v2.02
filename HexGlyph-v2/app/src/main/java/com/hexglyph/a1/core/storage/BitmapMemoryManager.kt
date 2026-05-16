package com.hexglyph.a1.core.storage

import android.graphics.Bitmap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralises bitmap lifecycle management to prevent memory leaks.
 */
@Singleton
class BitmapMemoryManager @Inject constructor() {

    /**
     * Recycles [bitmap] if it is not already recycled.
     * Safe to call from any thread.
     */
    fun recycle(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    /**
     * Runs [block] with [bitmap] and recycles it afterward, even on exception.
     */
    inline fun <R> use(bitmap: Bitmap, block: (Bitmap) -> R): R =
        try {
            block(bitmap)
        } finally {
            recycle(bitmap)
        }
}
