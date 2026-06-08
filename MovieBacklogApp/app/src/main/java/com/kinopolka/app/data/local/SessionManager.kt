package com.kinopolka.app.data.local

import android.content.Context
import com.kinopolka.app.data.model.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хранит сессию пользователя (JWT-токен и базовые данные) в SharedPreferences,
 * чтобы вход сохранялся между запусками приложения.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = !token().isNullOrBlank()

    fun token(): String? = prefs.getString(KEY_TOKEN, null)

    fun save(token: String, user: UserDto) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, user.id)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_NAME, user.displayName)
            .apply()
    }

    fun currentUser(): UserDto? {
        val id = prefs.getLong(KEY_USER_ID, -1L)
        val email = prefs.getString(KEY_EMAIL, null)
        val name = prefs.getString(KEY_NAME, null)
        return if (id >= 0 && email != null && name != null) {
            UserDto(id = id, email = email, displayName = name)
        } else {
            null
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS = "kinopolka_session"
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val KEY_NAME = "name"
    }
}
