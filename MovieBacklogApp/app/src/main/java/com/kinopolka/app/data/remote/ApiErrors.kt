package com.kinopolka.app.data.remote

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

/** Ошибка обращения к API с понятным пользователю текстом. */
class ApiError(message: String) : Exception(message)

private val gson = Gson()

/** Превращает любое исключение сетевого слоя в читаемое сообщение. */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> {
        val body = runCatching { response()?.errorBody()?.string() }.getOrNull()
        val parsed = body?.let {
            runCatching { gson.fromJson(it, ErrorResponse::class.java)?.error }.getOrNull()
        }
        parsed ?: "Ошибка сервера (код ${code()})"
    }
    is IOException -> "Нет соединения с сервером"
    else -> message ?: "Неизвестная ошибка"
}

/** Выполняет сетевой вызов и оборачивает результат в [Result] с понятной ошибкой. */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (t: Throwable) {
        Result.failure(ApiError(t.toUserMessage()))
    }
