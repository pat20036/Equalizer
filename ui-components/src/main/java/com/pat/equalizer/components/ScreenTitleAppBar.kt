package com.pat.equalizer.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTitleAppBar(
    text: String,
    modifier: Modifier = Modifier,
    backAction: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(title = {
        Text(text = text, style = MaterialTheme.typography.headlineLarge, modifier = modifier)
    }, navigationIcon = {
        backAction?.let {
            IconButton(onClick = it) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
        }
    }, actions = actions)
}

@Preview(showBackground = true)
@Composable
private fun ScreenTitleAppBarPreview() {
    ScreenTitleAppBar("Screen Title")
}