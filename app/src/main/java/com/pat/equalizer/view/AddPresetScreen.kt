package com.pat.equalizer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pat.equalizer.viewmodel.AddPresetUiAction
import com.pat.equalizer.viewmodel.AddPresetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddPresetScreen(navController: NavHostController) {
    val viewmodel = hiltViewModel<AddPresetViewModel>()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var text by rememberSaveable { mutableStateOf("") }
        Text("Add new preset")
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Preset name") })
        Button(onClick = {
            coroutineScope.launch {
                viewmodel.emitAction(AddPresetUiAction.AddCustomPreset(text))
            }
        }, modifier = Modifier.height(ButtonDefaults.LargeContainerHeight), shapes = ButtonDefaults.shapes()) { Text("Add") }
    }

    if (viewmodel.getCurrentState().added) {
        navController.popBackStack()
    }
}

@Preview
@Composable
private fun AddPresetScreenPreview() {

}