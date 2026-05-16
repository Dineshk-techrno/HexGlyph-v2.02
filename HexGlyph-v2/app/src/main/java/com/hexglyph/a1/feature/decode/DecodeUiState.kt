package com.hexglyph.a1.feature.decode

import android.net.Uri

data class DecodeUiState(
    val selectedImageUri: Uri?    = null,
    val password:         String  = "",
    val isDecoding:       Boolean = false,
    val decodedMessage:   String? = null,
    val error:            String? = null
)

sealed class DecodeEvent {
    data class ImageSelected(val uri: Uri)       : DecodeEvent()
    data class PasswordChanged(val text: String) : DecodeEvent()
    data object StartDecode                       : DecodeEvent()
    data object DismissError                      : DecodeEvent()
    data object ClearResult                       : DecodeEvent()
}
