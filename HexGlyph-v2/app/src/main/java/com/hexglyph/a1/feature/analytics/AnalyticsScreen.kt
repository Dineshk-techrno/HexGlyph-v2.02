package com.hexglyph.a1.feature.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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

private val dateFmt = SimpleDateFormat("dd MMM HH:mm:ss", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack:    () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onEvent(AnalyticsEvent.ClearAll) }) {
                        Text("Clear")
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
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = Dimens.maxContentWidth)
                    .padding(Dimens.paddingMd),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingMd)
            ) {
                // Summary cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMd)
                    ) {
                        StatCard(
                            label = "Encodes",
                            value = state.totalEncodes.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Decodes",
                            value = state.totalDecodes.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMd)
                    ) {
                        StatCard(
                            label = "Avg encode",
                            value = state.avgEncodeDurMs?.let { "%.0f ms".format(it) } ?: "—",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Avg decode",
                            value = state.avgDecodeDurMs?.let { "%.0f ms".format(it) } ?: "—",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        "Recent events",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = Dimens.paddingSm)
                    )
                }

                if (state.recentEvents.isEmpty()) {
                    item { Text("No analytics data yet.") }
                }

                items(state.recentEvents) { event ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Dimens.paddingMd)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    event.eventType,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    dateFmt.format(Date(event.timestamp)),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Text(
                                "${event.durationMs} ms  •  ${event.payloadSize} bytes  •  ${event.imageWidth}×${event.imageHeight}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingMd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
