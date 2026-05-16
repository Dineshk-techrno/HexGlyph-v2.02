package com.hexglyph.a1.feature.history

import com.hexglyph.a1.data.local.entity.DecodeHistoryEntity
import com.hexglyph.a1.data.local.entity.EncodeHistoryEntity

data class HistoryUiState(
    val encodeHistory: List<EncodeHistoryEntity> = emptyList(),
    val decodeHistory: List<DecodeHistoryEntity> = emptyList(),
    val selectedTab:   Int = 0
)

sealed class HistoryEvent {
    data class TabSelected(val index: Int) : HistoryEvent()
    data object ClearAll                   : HistoryEvent()
}
