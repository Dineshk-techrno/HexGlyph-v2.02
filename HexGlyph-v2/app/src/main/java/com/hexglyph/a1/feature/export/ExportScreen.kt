package com.hexglyph.a1.feature.export

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun ExportScreen(
    onBack:    () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val state             by viewModel.state.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.onEvent(ExportEvent.ImageSelected(it)) } }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(ExportEvent.DismissError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    .padding(Dimens.paddingLg),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingMd)
            ) {
                item {
                    OutlinedButton(
                        onClick  = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (state.pendingImageUri != null) "Image selected ✓"
                            else "Select image to re-export"
                        )
                    }
                }

                item {
                    Button(
                        onClick  = { viewModel.onEvent(ExportEvent.StartExport) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled  = !state.isExporting && state.pendingImageUri != null
                    ) {
                        if (state.isExporting) CircularProgressIndicator()
                        else Text("Export as PNG")
                    }
                }

                state.lastExportedUri?.let {
                    item {
                        Text(
                            "Saved to Pictures/HexGlyph ✓",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Text(
                        "Export history",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                if (state.exportHistory.isEmpty()) {
                    item { Text("No exports yet.") }
                }

                items(state.exportHistory) { record ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Dimens.paddingMd)) {
                            Text(record.fileName, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                dateFmt.format(Date(record.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
