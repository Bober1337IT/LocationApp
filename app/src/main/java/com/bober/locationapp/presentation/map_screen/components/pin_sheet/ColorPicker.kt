package com.bober.locationapp.presentation.map_screen.components.pin_sheet

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Long> = listOf(
        Color(0xFF1E88E5).toColorLong(), // Blue
        Color(0xFF43A047).toColorLong(), // Green
        Color(0xFFF4511E).toColorLong(), // Orange
        Color(0xFFE53935).toColorLong(), // Red
        Color(0xFF8E24AA).toColorLong(), // Purple
        Color(0xFF3949AB).toColorLong(), // Indigo
        Color(0xFF00ACC1).toColorLong(), // Cyan
        Color(0xFF6D4C41).toColorLong(), // Brown
    )
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        colors.forEach { colorLong ->
            val isSelected = colorLong == selectedColor
            val borderColor =
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            val borderWidth = if (isSelected) 3.dp else 1.dp

            Row(
                modifier = Modifier
                    .size(28.dp)
                    .border(borderWidth, borderColor, CircleShape)
                    .border(8.dp, Color(colorLong.toULong()), CircleShape)
                    .clickable { onColorSelected(colorLong) }
            ) {}
        }
    }
}

