package com.kinopolka.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kinopolka.app.data.model.BacklogStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Профиль") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier.size(96.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.user?.displayName?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(state.user?.displayName ?: "—", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(state.user?.email ?: "", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(32.dp))
            Text("Статистика бэклога", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            StatsBlock(state.stats)

            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = { viewModel.logout(); onLoggedOut() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Выйти из аккаунта")
            }
        }
    }
}

@Composable
private fun StatsBlock(stats: BacklogStats?) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard("Хочу", stats?.want ?: 0, Modifier.weight(1f))
        StatCard("Смотрю", stats?.watching ?: 0, Modifier.weight(1f))
        StatCard("Посмотрел", stats?.watched ?: 0, Modifier.weight(1f))
    }
    Spacer(Modifier.height(12.dp))
    StatCard("Всего фильмов в бэклоге", stats?.total ?: 0, Modifier.fillMaxWidth(), big = true)
}

@Composable
private fun StatCard(label: String, value: Long, modifier: Modifier = Modifier, big: Boolean = false) {
    Card(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                value.toString(),
                style = if (big) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}
