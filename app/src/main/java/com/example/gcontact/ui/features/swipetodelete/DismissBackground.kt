package com.example.gcontact.ui.features.swipetodelete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays a background for swipe-to-dismiss actions.
 * Changes color based on the swipe direction.
 *
 * @param dismissState The state of the swipe-to-dismiss interaction.
 */
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {

    // Determine background color based on the swipe direction
    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
        MaterialTheme.colorScheme.background // Default background color
    else
        MaterialTheme.colorScheme.errorContainer // Error color for delete action

    // Card to provide structure and styling
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = ShapeDefaults.Large
    ) {
        // Box to position the delete icon
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color) // Apply the calculated background color
                .padding(24.dp, 8.dp), // Padding around the content
            contentAlignment = Alignment.CenterEnd // Align icon to the end
        ) {
            // Delete icon
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null, // No description needed for a visual indicator
            )
        }
    }
}
