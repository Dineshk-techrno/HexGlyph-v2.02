package com.hexglyph.a1.feature.analytics

import com.hexglyph.a1.data.local.entity.AnalyticsEntity

data class AnalyticsUiState(
    val recentEvents:    List<AnalyticsEntity> = emptyList(),
    val avgEncodeDurMs:  Double?               = null,
    val avgDecodeDurMs:  Double?               = null,
    val totalEncodes:    Int                   = 0,
    val totalDecodes:    Int                   = 0
)

sealed class AnalyticsEvent {
    data object ClearAll : AnalyticsEvent()
}
