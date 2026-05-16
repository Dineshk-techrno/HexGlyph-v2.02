package com.hexglyph.a1.feature.encode

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexglyph.a1.core.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeScreen(
    onBack:      () -> Unit,
    viewModel:   EncodeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Photo picker — ActivityResultContracts.PickVisualMedia (non-deprecated)
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.onEvent(EncodeEvent.ImageSelected(it)) } }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(EncodeEvent.DismissError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Encode") },
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
            Column(
                modifier = Modifier
                    .widthIn(max = Dimens.maxContentWidth)
                    .padding(Dimens.paddingLg),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingMd)
            ) {
                OutlinedButton(
                    onClick  = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (state.selectedImageUri != null) "Image selected ✓"
                        else "Select image"
                    )
                }

                if (state.maxPayloadBytes > 0) {
                    Text(
                        text  = "Max payload: ${state.maxPayloadBytes} bytes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value         = state.message,
                    onValueChange = { viewModel.onEvent(EncodeEvent.MessageChanged(it)) },
                    label         = { Text("Message") },
                    modifier      = Modifier.fillMaxWidth(),
                    minLines      = 3
                )

                OutlinedTextField(
                    value               = state.password,
                    onValueChange       = { viewModel.onEvent(EncodeEvent.PasswordChanged(it)) },
                    label               = { Text("Password") },
                    modifier            = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions     = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Button(
                    onClick  = { viewModel.onEvent(EncodeEvent.StartEncode) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled  = !state.isEncrypting && state.selectedImageUri != null
                ) {
                    if (state.isEncrypting) CircularProgressIndicator()
                    else Text("Encode & Save")
                }

                state.resultUri?.let {
                    Text(
                        text  = "Saved to Pictures/HexGlyph ✓",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
