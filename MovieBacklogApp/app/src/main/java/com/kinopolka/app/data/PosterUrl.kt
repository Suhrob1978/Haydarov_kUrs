package com.kinopolka.app.data

import com.kinopolka.app.BuildConfig
import com.kinopolka.app.data.model.BacklogItem
import com.kinopolka.app.data.model.Movie

private fun normalizedBaseUrl(): String {
    val url = BuildConfig.BASE_URL
    return if (url.endsWith("/")) url else "$url/"
}

/** Постеры грузим через сервер — TMDB часто недоступен с телефона напрямую. */
fun Movie.withServerPoster(): Movie {
    val base = normalizedBaseUrl()
    val proxied = "${base}api/movies/$id/poster"
    return if (posterUrl == proxied) this else copy(posterUrl = proxied)
}

fun BacklogItem.withServerPoster(): BacklogItem = copy(movie = movie.withServerPoster())
