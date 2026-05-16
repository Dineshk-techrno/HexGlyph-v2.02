package com.hexglyph.a1.feature.decode

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexglyph.a1.core.security.secureWipe
import com.hexglyph.a1.core.steganography.HexGlyphSteganographyEngine
import com.hexglyph.a1.core.storage.BitmapLoader
import com.hexglyph.a1.core.storage.BitmapMemoryManager
import com.hexglyph.a1.data.local.dao.DecodeHistoryDao
import com.hexglyph.a1.data.local.entity.DecodeHistoryEntity
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
class DecodeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val engine:           HexGlyphSteganographyEngine,
    private val bitmapLoader:     BitmapLoader,
    private val bitmapManager:    BitmapMemoryManager,
    private val decodeHistoryDao: DecodeHistoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(DecodeUiState())
    val state: StateFlow<DecodeUiState> = _state.asStateFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        _state.update { it.copy(isDecoding = false, error = throwable.message) }
    }

    fun onEvent(event: DecodeEvent) {
        when (event) {
            is DecodeEvent.ImageSelected   -> _state.update { it.copy(selectedImageUri = event.uri) }
            is DecodeEvent.PasswordChanged -> _state.update { it.copy(password = event.text) }
            is DecodeEvent.StartDecode     -> startDecode()
            is DecodeEvent.DismissError    -> _state.update { it.copy(error = null) }
            is DecodeEvent.ClearResult     -> _state.update { it.copy(decodedMessage = null) }
        }
    }

    private fun startDecode() {
        val current  = _state.value
        val imageUri = current.selectedImageUri ?: return

        _state.update { it.copy(isDecoding = true, error = null) }

        viewModelScope.launch(errorHandler) {
            val password = current.password.toCharArray()
            var plaintext: ByteArray? = null

            try {
                val bitmap = bitmapLoader.load(imageUri)
                plaintext  = engine.decode(bitmap, password)
                bitmapManager.recycle(bitmap)

                val message = plaintext.toString(Charsets.UTF_8)

                decodeHistoryDao.insert(
                    DecodeHistoryEntity(
                        timestamp   = System.currentTimeMillis(),
                        imageUri    = imageUri.toString(),
                        payloadSize = plaintext.size,
                        success     = true,
                        errorMsg    = null
                    )
                )

                _state.update { it.copy(isDecoding = false, decodedMessage = message) }

            } catch (e: Exception) {
                decodeHistoryDao.insert(
                    DecodeHistoryEntity(
                        timestamp   = System.currentTimeMillis(),
                        imageUri    = imageUri.toString(),
                        payloadSize = 0,
                        success     = false,
                        errorMsg    = e.message
                    )
                )
                _state.update { it.copy(isDecoding = false, error = e.message ?: "Decode failed") }
            } finally {
                plaintext?.secureWipe()
                password.fill('\u0000')
            }
        }
    }
}
