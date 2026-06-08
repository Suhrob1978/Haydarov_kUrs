package com.kinopolka.app.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kinopolka.app.data.model.BacklogStatus

@Composable
fun PosterImage(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url.ifBlank { null },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

/** Звёздный рейтинг 0..10, отображается как 5 звёзд. */
@Composable
fun RatingStars(rating: Int, max: Int = 10, size: Dp = 18.dp) {
    val filledStars = (rating / 2.0).toInt()
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            if (index < filledStars) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(size))
            } else {
                Icon(Icons.Outlined.Star, null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(size))
            }
        }
    }
}

@Composable
fun StatusChip(status: BacklogStatus) {
    val container = when (status) {
        BacklogStatus.WANT -> MaterialTheme.colorScheme.secondaryContainer
        BacklogStatus.WATCHING -> MaterialTheme.colorScheme.tertiaryContainer
        BacklogStatus.WATCHED -> MaterialTheme.colorScheme.primaryContainer
    }
    Surface(color = container, shape = MaterialTheme.shapes.small) {
        Text(
            text = status.title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
