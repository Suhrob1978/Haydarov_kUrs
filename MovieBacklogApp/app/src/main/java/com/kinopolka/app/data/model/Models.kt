package com.kinopolka.app.data.model

/** Статус фильма в личном бэклоге пользователя. */
enum class BacklogStatus {
    WANT,
    WATCHING,
    WATCHED;

    /** Человекочитаемое название статуса для интерфейса. */
    val title: String
        get() = when (this) {
            WANT -> "Хочу посмотреть"
            WATCHING -> "Смотрю"
            WATCHED -> "Посмотрел"
        }
}

/** Фильм из общего каталога (приходит с сервера). */
data class Movie(
    val id: Long,
    val title: String,
    val year: Int,
    val genre: String,
    val description: String,
    val posterUrl: String,
    val rating: Double,
)

/** Элемент личного бэклога: фильм + пользовательский статус, оценка и заметка. */
data class BacklogItem(
    val id: Long,
    val movie: Movie,
    val status: BacklogStatus,
    val userRating: Int,
    val note: String,
)

/** Сводная статистика по бэклогу пользователя. */
data class BacklogStats(
    val want: Long,
    val watching: Long,
    val watched: Long,
    val total: Long,
)

/** Данные пользователя (без пароля). */
data class UserDto(
    val id: Long,
    val email: String,
    val displayName: String,
)

/** Тело запроса на изменение элемента бэклога (любое поле может быть null). */
data class UpdateBacklogRequest(
    val status: BacklogStatus? = null,
    val userRating: Int? = null,
    val note: String? = null,
)
