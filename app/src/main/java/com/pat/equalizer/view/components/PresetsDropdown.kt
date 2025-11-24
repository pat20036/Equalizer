package com.pat.equalizer.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pat.equalizer.R
import com.pat.equalizer.core.model.Preset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PresetsDropdown(
    presets: List<Preset>,
    selectedPreset: Preset,
    onPresetClick: (preset: Preset) -> Unit,
    onPresetDelete: (preset: Preset) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.preset_section_title),
            modifier = Modifier
                .padding(end = 16.dp)
        )
        ExposedDropdownMenuBox(
            modifier = Modifier
                .weight(1f),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedPreset.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp)
            )

            ExposedDropdownMenu(
                modifier = Modifier.animateContentSize(),
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(16.dp)
            ) {
                presets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPresetClick(preset)
                                expanded = false
                            }) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }


        AnimatedVisibility(visible = selectedPreset.isCustom, enter = scaleIn(), exit = scaleOut()) {
            IconButton(
                onClick = {
                    onPresetDelete(presets.first { it.selected })
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PresetsDropdownPreview() {
    val presets = listOf(
        Preset(name = "Rock", id = 0, bands = emptyList()),
        Preset(name = "Pop", id = 1, bands = emptyList()),
        Preset(name = "Jazz", id = 2, bands = emptyList()),
        Preset(name = "Custom Preset", id = 3, bands = emptyList(), selected = true, isCustom = true)
    )

    PresetsDropdown(
        presets = presets,
        selectedPreset = presets.first { it.selected },
        onPresetClick = {},
        onPresetDelete = {}
    )
}
