package com.kinopolka.server.service

import com.kinopolka.server.domain.MovieRepository
import com.kinopolka.server.web.MovieDto
import com.kinopolka.server.web.toDto
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.net.HttpURLConnection
import java.net.URI

@Service
class MovieService(private val movies: MovieRepository) {

    fun catalog(query: String?): List<MovieDto> {
        val list = if (query.isNullOrBlank()) {
            movies.findAll()
        } else {
            movies.findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(query, query)
        }
        return list.sortedByDescending { it.rating }.map { it.toDto() }
    }

    /** Скачивает постер с внешнего URL и отдаёт клиенту через наш сервер. */
    fun posterBytes(movieId: Long): Pair<ByteArray, String> {
        val movie = movies.findById(movieId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден")
        }
        if (movie.posterUrl.isBlank()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Постер не задан")
        }
        return try {
            val connection = URI(movie.posterUrl).toURL().openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "KinopolkaServer/1.0")
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            connection.inputStream.use { bytes ->
                val type = connection.contentType?.takeIf { it.startsWith("image/") } ?: "image/jpeg"
                bytes.readBytes() to type
            }
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Не удалось загрузить постер")
        }
    }
}
