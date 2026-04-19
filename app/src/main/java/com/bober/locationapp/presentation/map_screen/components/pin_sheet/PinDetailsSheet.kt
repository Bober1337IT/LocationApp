package com.bober.locationapp.presentation.map_screen.components.pin_sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bober.locationapp.domain.model.Pin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDetailsSheet(
    pin: Pin,
    sheetMode: PinSheetMode,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
    onSheetModeChange: (PinSheetMode) -> Unit,
    onSave: (name: String, description: String?, color: Long) -> Unit,
) {
    var editedName by remember(pin.id) { mutableStateOf(pin.name) }
    var editedDescription by remember(pin.id) { mutableStateOf(pin.description ?: "") }
    var editedColor by remember(pin.id) { mutableStateOf(pin.color) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = when (sheetMode) {
                        PinSheetMode.DETAILS -> "Pin Details"
                        PinSheetMode.EDITING -> "Edit Pin"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        when (sheetMode) {
                            PinSheetMode.DETAILS -> onSheetModeChange(PinSheetMode.EDITING)
                            PinSheetMode.EDITING ->
                                onSave(editedName, editedDescription, editedColor)
                        }
                    },
                ) {
                    Icon(
                        imageVector = when (sheetMode) {
                            PinSheetMode.DETAILS -> Icons.Default.Edit
                            PinSheetMode.EDITING -> Icons.Default.Check
                        },
                        contentDescription = when (sheetMode) {
                            PinSheetMode.DETAILS -> "Edit"
                            PinSheetMode.EDITING -> "Save"
                        },
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (sheetMode) {
                PinSheetMode.EDITING -> {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Color", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPicker(
                        selectedColor = editedColor,
                        onColorSelected = { editedColor = it },
                    )
                }
                PinSheetMode.DETAILS -> {
                    if (pin.name.isNotBlank()) {
                        Text(
                            text = pin.name,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                    Text(
                        text = pin.city,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    if (!pin.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pin.description,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Coordinates: ${pin.latitude}, ${pin.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}
