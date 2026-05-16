package com.hexglyph.a1.feature.encode

import android.net.Uri

data class EncodeUiState(
    val selectedImageUri: Uri?    = null,
    val message:          String  = "",
    val password:         String  = "",
    val isEncrypting:     Boolean = false,
    val resultUri:        Uri?    = null,
    val error:            String? = null,
    val maxPayloadBytes:  Int     = 0
)

sealed class EncodeEvent {
    data class ImageSelected(val uri: Uri)        : EncodeEvent()
    data class MessageChanged(val text: String)   : EncodeEvent()
    data class PasswordChanged(val text: String)  : EncodeEvent()
    data object StartEncode                        : EncodeEvent()
    data object DismissError                       : EncodeEvent()
    data object ClearResult                        : EncodeEvent()
}
