package com.hexglyph.a1.feature.encode

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexglyph.a1.core.security.secureWipe
import com.hexglyph.a1.core.steganography.HexGlyphSteganographyEngine
import com.hexglyph.a1.core.storage.BitmapLoader
import com.hexglyph.a1.core.storage.BitmapMemoryManager
import com.hexglyph.a1.core.storage.ExportResult
import com.hexglyph.a1.core.storage.MediaStoreExporter
import com.hexglyph.a1.data.local.dao.EncodeHistoryDao
import com.hexglyph.a1.data.local.entity.EncodeHistoryEntity
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
class EncodeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val engine:         HexGlyphSteganographyEngine,
    private val bitmapLoader:   BitmapLoader,
    private val bitmapManager:  BitmapMemoryManager,
    private val exporter:       MediaStoreExporter,
    private val encodeHistoryDao: EncodeHistoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(EncodeUiState())
    val state: StateFlow<EncodeUiState> = _state.asStateFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        _state.update { it.copy(isEncrypting = false, error = throwable.message) }
    }

    fun onEvent(event: EncodeEvent) {
        when (event) {
            is EncodeEvent.ImageSelected    -> onImageSelected(event.uri)
            is EncodeEvent.MessageChanged   -> _state.update { it.copy(message = event.text) }
            is EncodeEvent.PasswordChanged  -> _state.update { it.copy(password = event.text) }
            is EncodeEvent.StartEncode      -> startEncode()
            is EncodeEvent.DismissError     -> _state.update { it.copy(error = null) }
            is EncodeEvent.ClearResult      -> _state.update { it.copy(resultUri = null) }
        }
    }

    private fun onImageSelected(uri: Uri) {
        viewModelScope.launch(errorHandler) {
            val bitmap = bitmapLoader.load(uri)
            val maxBytes = engine.maxPayloadBytes(bitmap)
            bitmapManager.recycle(bitmap)
            _state.update { it.copy(selectedImageUri = uri, maxPayloadBytes = maxBytes) }
        }
    }

    private fun startEncode() {
        val current = _state.value
        val imageUri = current.selectedImageUri ?: return
        if (current.message.isBlank()) {
            _state.update { it.copy(error = "Message cannot be empty.") }
            return
        }

        _state.update { it.copy(isEncrypting = true, error = null) }

        viewModelScope.launch(errorHandler) {
            val plaintext = current.message.toByteArray(Charsets.UTF_8)
            val password  = current.password.toCharArray()

            try {
                val bitmap      = bitmapLoader.load(imageUri)
                val stegobitmap = engine.encode(bitmap, plaintext, password)
                bitmapManager.recycle(bitmap)

                val fileName = "hexglyph_${System.currentTimeMillis()}"
                val result   = exporter.exportPng(stegobitmap, fileName)
                bitmapManager.recycle(stegobitmap)

                when (result) {
                    is ExportResult.Success -> {
                        encodeHistoryDao.insert(
                            EncodeHistoryEntity(
                                timestamp    = System.currentTimeMillis(),
                                imageUri     = imageUri.toString(),
                                payloadSize  = plaintext.size,
                                isEncrypted  = password.isNotEmpty(),
                                exportedPath = result.uri.toString()
                            )
                        )
                        _state.update { it.copy(isEncrypting = false, resultUri = result.uri) }
                    }
                    is ExportResult.Failure ->
                        _state.update { it.copy(isEncrypting = false, error = result.reason) }
                }
            } finally {
                plaintext.secureWipe()
                password.fill('\u0000')
            }
        }
    }
}
