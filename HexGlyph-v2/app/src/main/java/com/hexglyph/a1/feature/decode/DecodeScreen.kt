package com.hexglyph.a1.feature.decode

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
import androidx.compose.material3.Card
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
fun DecodeScreen(
    onBack:    () -> Unit,
    viewModel: DecodeViewModel = hiltViewModel()
) {
    val state             by viewModel.state.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.onEvent(DecodeEvent.ImageSelected(it)) } }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(DecodeEvent.DismissError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Decode") },
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
                        else "Select stego-image"
                    )
                }

                OutlinedTextField(
                    value               = state.password,
                    onValueChange       = { viewModel.onEvent(DecodeEvent.PasswordChanged(it)) },
                    label               = { Text("Password") },
                    modifier            = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions     = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Button(
                    onClick  = { viewModel.onEvent(DecodeEvent.StartDecode) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled  = !state.isDecoding && state.selectedImageUri != null
                ) {
                    if (state.isDecoding) CircularProgressIndicator()
                    else Text("Decode")
                }

                state.decodedMessage?.let { msg ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Dimens.paddingMd)) {
                            Text(
                                text  = "Decoded message",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text  = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = Dimens.paddingSm)
                            )
                        }
                    }
                }
            }
        }
    }
}
