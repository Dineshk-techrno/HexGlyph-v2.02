package com.hexglyph.a1.core.storage

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Safe URI utilities.
 * Never uses file:// URIs or uri.path — content:// only.
 */
@Singleton
class UriResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Returns a human-readable display name for a content [Uri], or null if unavailable.
     */
    fun displayName(uri: Uri): String? =
        context.contentResolver
            .query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }

    /**
     * Returns the MIME type for a content [Uri], or null if unavailable.
     */
    fun mimeType(uri: Uri): String? = context.contentResolver.getType(uri)

    /**
     * Returns true if [uri] points to an image based on its MIME type.
     */
    fun isImage(uri: Uri): Boolean = mimeType(uri)?.startsWith("image/") == true
}
