package com.kinopolka.server.service

import com.kinopolka.server.domain.MovieRepository
import com.kinopolka.server.web.MovieDto
import com.kinopolka.server.web.toDto
import org.springframework.stereotype.Service

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
}
