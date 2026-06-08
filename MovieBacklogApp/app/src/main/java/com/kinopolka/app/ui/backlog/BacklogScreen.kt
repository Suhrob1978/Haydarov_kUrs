package com.kinopolka.app.ui.backlog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kinopolka.app.data.model.BacklogItem
import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.ui.common.EmptyState
import com.kinopolka.app.ui.common.PosterImage
import com.kinopolka.app.ui.common.RatingStars
import com.kinopolka.app.ui.common.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacklogScreen(viewModel: BacklogViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var editing by remember { mutableStateOf<BacklogItem?>(null) }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeError()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мой бэклог") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            FilterRow(state.filter, viewModel::setFilter)
            when {
                state.loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                state.items.isEmpty() -> EmptyState("В этом списке пока пусто.\nДобавьте фильмы из каталога.")
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        BacklogRow(item, onClick = { editing = item })
                    }
                }
            }
        }
    }

    editing?.let { item ->
        EditBacklogSheet(
            item = item,
            onDismiss = { editing = null },
            onSave = { status, rating, note ->
                viewModel.update(item, status, rating, note)
                editing = null
            },
            onDelete = {
                viewModel.remove(item)
                editing = null
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(filter: BacklogStatus?, onSelect: (BacklogStatus?) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(selected = filter == null, onClick = { onSelect(null) }, label = { Text("Все") })
        BacklogStatus.entries.forEach { status ->
            FilterChip(
                selected = filter == status,
                onClick = { onSelect(status) },
                label = { Text(shortTitle(status)) },
            )
        }
    }
}

private fun shortTitle(status: BacklogStatus) = when (status) {
    BacklogStatus.WANT -> "Хочу"
    BacklogStatus.WATCHING -> "Смотрю"
    BacklogStatus.WATCHED -> "Посмотрел"
}

@Composable
private fun BacklogRow(item: BacklogItem, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp)) {
            PosterImage(
                url = item.movie.posterUrl,
                modifier = Modifier.size(width = 70.dp, height = 100.dp).clip(RoundedCornerShape(8.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${item.movie.year} • ${item.movie.genre}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                StatusChip(item.status)
                if (item.userRating > 0) {
                    Spacer(Modifier.height(6.dp))
                    RatingStars(item.userRating)
                }
                if (item.note.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(item.note, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBacklogSheet(
    item: BacklogItem,
    onDismiss: () -> Unit,
    onSave: (BacklogStatus, Int, String) -> Unit,
    onDelete: () -> Unit,
) {
    var status by remember { mutableStateOf(item.status) }
    var rating by remember { mutableIntStateOf(item.userRating) }
    var note by remember { mutableStateOf(item.note) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text(item.movie.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Text("Статус", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BacklogStatus.entries.forEach { s ->
                    FilterChip(selected = status == s, onClick = { status = s }, label = { Text(shortTitle(s)) })
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Моя оценка: ${if (rating == 0) "—" else rating}", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt() },
                valueRange = 0f..10f,
                steps = 9,
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Заметка") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Удалить")
                }
                Button(onClick = { onSave(status, rating, note) }, modifier = Modifier.weight(1f)) {
                    Text("Сохранить")
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
