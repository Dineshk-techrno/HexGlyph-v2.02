package com.hexglyph.a1.feature.export

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexglyph.a1.core.storage.BitmapLoader
import com.hexglyph.a1.core.storage.BitmapMemoryManager
import com.hexglyph.a1.core.storage.ExportResult
import com.hexglyph.a1.core.storage.MediaStoreExporter
import com.hexglyph.a1.data.local.dao.ExportHistoryDao
import com.hexglyph.a1.data.local.entity.ExportHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bitmapLoader:    BitmapLoader,
    private val bitmapManager:   BitmapMemoryManager,
    private val exporter:        MediaStoreExporter,
    private val exportHistoryDao: ExportHistoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(ExportUiState())
    val state: StateFlow<ExportUiState> = _state.asStateFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        _state.update { it.copy(isExporting = false, error = throwable.message) }
    }

    init {
        viewModelScope.launch {
            exportHistoryDao.observeAll().collect { history ->
                _state.update { it.copy(exportHistory = history) }
            }
        }
    }

    fun onEvent(event: ExportEvent) {
        when (event) {
            is ExportEvent.ImageSelected -> _state.update { it.copy(pendingImageUri = event.uri) }
            is ExportEvent.StartExport   -> startExport()
            is ExportEvent.DismissError  -> _state.update { it.copy(error = null) }
            is ExportEvent.ClearAll      -> viewModelScope.launch { exportHistoryDao.deleteAll() }
        }
    }

    private fun startExport() {
        val uri = _state.value.pendingImageUri ?: return
        _state.update { it.copy(isExporting = true, error = null) }

        viewModelScope.launch(errorHandler) {
            val bitmap   = bitmapLoader.load(uri)
            val fileName = "hexglyph_export_${System.currentTimeMillis()}"
            val result   = exporter.exportPng(bitmap, fileName)
            bitmapManager.recycle(bitmap)

            when (result) {
                is ExportResult.Success -> {
                    exportHistoryDao.insert(
                        ExportHistoryEntity(
                            timestamp  = System.currentTimeMillis(),
                            exportUri  = result.uri.toString(),
                            fileName   = result.fileName,
                            formatMime = "image/png"
                        )
                    )
                    _state.update {
                        it.copy(isExporting = false, lastExportedUri = result.uri)
                    }
                }
                is ExportResult.Failure ->
                    _state.update { it.copy(isExporting = false, error = result.reason) }
            }
        }
    }
}
