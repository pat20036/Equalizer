package com.pat.equalizer.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pat.equalizer.R
import com.pat.equalizer.components.ScreenTitleAppBar
import com.pat.equalizer.modifiers.defaultHorizontalPadding
import com.pat.equalizer.view.AddPresetScreenBackStackKey.ADDED_PRESET_ID_KEY
import com.pat.equalizer.viewmodel.AddPresetUiAction
import com.pat.equalizer.viewmodel.AddPresetUiEvent
import com.pat.equalizer.viewmodel.AddPresetViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun AddPresetScreen(navController: NavHostController) {
    val viewmodel = hiltViewModel<AddPresetViewModel>()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AddPresetScreenContent(navController = navController, onClickSaveButton = {
        coroutineScope.launch {
            viewmodel.emitAction(AddPresetUiAction.AddCustomPreset(it))
        }
    })

    LaunchedEffect(Unit) {
        viewmodel.oneTimeEventsChannel.receiveAsFlow().collectLatest { event ->
            when (event) {
                is AddPresetUiEvent.Error -> {
                    Toast.makeText(context, context.getString(R.string.validation_error_message), Toast.LENGTH_SHORT).show()
                }

                is AddPresetUiEvent.Success -> {
                    with(navController) {
                        previousBackStackEntry?.savedStateHandle?.set(ADDED_PRESET_ID_KEY, event.addedPreset)
                        popBackStack()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddPresetScreenContent(
    navController: NavHostController,
    onClickSaveButton: (name: String) -> Unit = {}
) {
    Scaffold(topBar = {
        ScreenTitleAppBar(
            text = stringResource(R.string.add_preset_screen_title),
            backAction = { navController.popBackStack() })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .defaultHorizontalPadding()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var text by rememberSaveable { mutableStateOf("") }

            Text(
                text = stringResource(R.string.add_preset_hint),
                style = MaterialTheme.typography.bodySmall
            )

            LoadingIndicator(modifier = Modifier.size(196.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                value = text, onValueChange = { text = it },
                label = { Text(text = stringResource(R.string.preset_name_placeholder)) }
            )
            Button(
                onClick = {
                    onClickSaveButton(text)
                }, modifier = Modifier
                    .height(ButtonDefaults.LargeContainerHeight)
                    .fillMaxWidth(), shapes = ButtonDefaults.shapes()
            ) { Text(stringResource(R.string.save_button_text)) }
        }
    }
}

object AddPresetScreenBackStackKey {
    const val ADDED_PRESET_ID_KEY = "addedPreset"
}

@Preview
@Composable
private fun AddPresetScreenPreview() {
    AddPresetScreenContent(rememberNavController())
}