package com.hexglyph.a1.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexglyph.a1.core.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFmt = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack:             () -> Unit,
    onNavigateToExport: () -> Unit,
    viewModel:          HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onEvent(HistoryEvent.ClearAll) }) {
                        Text("Clear all")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.widthIn(max = Dimens.maxContentWidth)) {
                TabRow(selectedTabIndex = state.selectedTab) {
                    Tab(
                        selected = state.selectedTab == 0,
                        onClick  = { viewModel.onEvent(HistoryEvent.TabSelected(0)) },
                        text     = { Text("Encode (${state.encodeHistory.size})") }
                    )
                    Tab(
                        selected = state.selectedTab == 1,
                        onClick  = { viewModel.onEvent(HistoryEvent.TabSelected(1)) },
                        text     = { Text("Decode (${state.decodeHistory.size})") }
                    )
                }

                when (state.selectedTab) {
                    0 -> LazyColumn(
                        modifier = Modifier.padding(Dimens.paddingMd),
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSm)
                    ) {
                        if (state.encodeHistory.isEmpty()) {
                            item { Text("No encode history yet.") }
                        }
                        items(state.encodeHistory) { item ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(Dimens.paddingMd)) {
                                    Text(dateFmt.format(Date(item.timestamp)), style = MaterialTheme.typography.labelSmall)
                                    Text("Payload: ${item.payloadSize} bytes", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        if (item.isEncrypted) "Encrypted ✓" else "No encryption",
                                        color = if (item.isEncrypted) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    1 -> LazyColumn(
                        modifier = Modifier.padding(Dimens.paddingMd),
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSm)
                    ) {
                        if (state.decodeHistory.isEmpty()) {
                            item { Text("No decode history yet.") }
                        }
                        items(state.decodeHistory) { item ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(Dimens.paddingMd)) {
                                    Text(dateFmt.format(Date(item.timestamp)), style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        if (item.success) "Success ✓" else "Failed: ${item.errorMsg}",
                                        color = if (item.success) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
