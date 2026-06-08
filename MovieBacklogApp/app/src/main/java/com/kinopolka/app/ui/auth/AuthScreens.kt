package com.kinopolka.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onNavigateRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthScaffold(
        title = "Кинополка",
        subtitle = "Ваш личный бэклог фильмов",
        error = state.error,
        onDismissError = viewModel::clearError,
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Электронная почта") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { viewModel.login(email, password, onLoggedIn) },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
        ) {
            if (state.loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            else Text("Войти")
        }
        TextButton(onClick = onNavigateRegister, modifier = Modifier.fillMaxWidth()) {
            Text("Нет аккаунта? Зарегистрироваться")
        }
    }
}

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onNavigateLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthScaffold(
        title = "Регистрация",
        subtitle = "Создайте аккаунт, чтобы вести бэклог",
        error = state.error,
        onDismissError = viewModel::clearError,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Электронная почта") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { viewModel.register(email, password, name, onRegistered) },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
        ) {
            if (state.loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            else Text("Зарегистрироваться")
        }
        TextButton(onClick = onNavigateLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}

@Composable
private fun AuthScaffold(
    title: String,
    subtitle: String,
    error: String?,
    onDismissError: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error)
            onDismissError()
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))
            content()
            Spacer(Modifier.height(40.dp))
        }
    }
}
