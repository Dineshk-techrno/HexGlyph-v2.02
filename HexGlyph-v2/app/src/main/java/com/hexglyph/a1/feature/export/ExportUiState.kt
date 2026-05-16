package com.hexglyph.a1.feature.export

import android.net.Uri
import com.hexglyph.a1.data.local.entity.ExportHistoryEntity

data class ExportUiState(
    val exportHistory:    List<ExportHistoryEntity> = emptyList(),
    val pendingImageUri:  Uri?    = null,
    val isExporting:      Boolean = false,
    val lastExportedUri:  Uri?    = null,
    val error:            String? = null
)

sealed class ExportEvent {
    data class ImageSelected(val uri: Uri) : ExportEvent()
    data object StartExport                : ExportEvent()
    data object DismissError               : ExportEvent()
    data object ClearAll                   : ExportEvent()
}
