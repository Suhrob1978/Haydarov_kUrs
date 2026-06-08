package com.kinopolka.app.ui.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.data.model.Movie
import com.kinopolka.app.ui.common.EmptyState
import com.kinopolka.app.ui.common.PosterImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(viewModel: CatalogViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selected by remember { mutableStateOf<Movie?>(null) }

    LaunchedEffect(state.message, state.error) {
        val text = state.message ?: state.error
        if (text != null) {
            snackbarHostState.showSnackbar(text)
            viewModel.consumeMessages()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Каталог фильмов") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text("Поиск по названию или жанру") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                trailingIcon = {
                    TextButton(onClick = viewModel::search) { Text("Найти") }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when {
                state.loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                state.movies.isEmpty() -> EmptyState("Ничего не найдено")
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.movies, key = { it.id }) { movie ->
                        MovieRow(movie) { selected = movie }
                    }
                }
            }
        }
    }

    selected?.let { movie ->
        AddToBacklogSheet(
            movie = movie,
            onDismiss = { selected = null },
            onPick = { status ->
                viewModel.addToBacklog(movie, status)
                selected = null
            },
        )
    }
}

@Composable
private fun MovieRow(movie: Movie, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp)) {
            PosterImage(
                url = movie.posterUrl,
                modifier = Modifier.size(width = 80.dp, height = 110.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${movie.year} • ${movie.genre}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(" ${movie.rating}", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    movie.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToBacklogSheet(
    movie: Movie,
    onDismiss: () -> Unit,
    onPick: (BacklogStatus) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text(movie.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Добавить в список:", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            BacklogStatus.entries.forEach { status ->
                FilledTonalButton(
                    onClick = { onPick(status) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Text(status.title)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
