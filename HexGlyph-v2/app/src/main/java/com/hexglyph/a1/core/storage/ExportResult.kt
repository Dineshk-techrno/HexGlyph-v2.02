package com.hexglyph.a1.core.storage

import android.net.Uri

sealed class ExportResult {
    data class Success(val uri: Uri, val fileName: String) : ExportResult()
    data class Failure(val reason: String, val cause: Throwable? = null) : ExportResult()
}
