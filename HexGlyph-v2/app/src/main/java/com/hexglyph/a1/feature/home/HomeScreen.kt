package com.hexglyph.a1.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hexglyph.a1.core.ui.theme.Dimens

@Composable
fun HomeScreen(
    onNavigateToEncode:    () -> Unit,
    onNavigateToDecode:    () -> Unit,
    onNavigateToHistory:   () -> Unit,
    onNavigateToSettings:  () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = Dimens.maxContentWidth)
                    .padding(horizontal = Dimens.paddingLg),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingMd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text  = "HexGlyph",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick   = onNavigateToEncode,
                    modifier  = Modifier.fillMaxWidth()
                ) { Text("Encode") }

                Button(
                    onClick   = onNavigateToDecode,
                    modifier  = Modifier.fillMaxWidth()
                ) { Text("Decode") }

                OutlinedButton(
                    onClick   = onNavigateToHistory,
                    modifier  = Modifier.fillMaxWidth()
                ) { Text("History") }

                OutlinedButton(
                    onClick   = onNavigateToAnalytics,
                    modifier  = Modifier.fillMaxWidth()
                ) { Text("Analytics") }

                OutlinedButton(
                    onClick   = onNavigateToSettings,
                    modifier  = Modifier.fillMaxWidth()
                ) { Text("Settings") }
            }
        }
    }
}
