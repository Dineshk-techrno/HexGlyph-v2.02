package com.hexglyph.a1.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexglyph.a1.data.local.dao.AnalyticsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsDao: AnalyticsDao
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsUiState())
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            analyticsDao.observeRecent(limit = 100).collect { events ->
                val encodes = events.filter { it.eventType == "ENCODE" }
                val decodes = events.filter { it.eventType == "DECODE" }

                _state.update {
                    it.copy(
                        recentEvents   = events,
                        avgEncodeDurMs = encodes.map { e -> e.durationMs }.average().takeIf { encodes.isNotEmpty() },
                        avgDecodeDurMs = decodes.map { e -> e.durationMs }.average().takeIf { decodes.isNotEmpty() },
                        totalEncodes   = encodes.size,
                        totalDecodes   = decodes.size
                    )
                }
            }
        }
    }

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            is AnalyticsEvent.ClearAll -> viewModelScope.launch { analyticsDao.deleteAll() }
        }
    }
}
