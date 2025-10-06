package com.pat.equalizer.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTitleAppBar(text: String, modifier: Modifier = Modifier) {
    TopAppBar(title = {
        Text(text = text, style = MaterialTheme.typography.headlineLarge, modifier = modifier)
    })
}

@Preview(showBackground = true)
@Composable
private fun ScreenTitleAppBarPreview() {
    ScreenTitleAppBar("Screen Title")
}