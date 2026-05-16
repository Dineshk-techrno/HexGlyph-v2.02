package com.hexglyph.a1.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexglyph.a1.data.local.dao.DecodeHistoryDao
import com.hexglyph.a1.data.local.dao.EncodeHistoryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val encodeHistoryDao: EncodeHistoryDao,
    private val decodeHistoryDao: DecodeHistoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                encodeHistoryDao.observeAll(),
                decodeHistoryDao.observeAll()
            ) { enc, dec -> enc to dec }
                .collect { (enc, dec) ->
                    _state.update { it.copy(encodeHistory = enc, decodeHistory = dec) }
                }
        }
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.TabSelected -> _state.update { it.copy(selectedTab = event.index) }
            is HistoryEvent.ClearAll    -> viewModelScope.launch {
                encodeHistoryDao.deleteAll()
                decodeHistoryDao.deleteAll()
            }
        }
    }
}
