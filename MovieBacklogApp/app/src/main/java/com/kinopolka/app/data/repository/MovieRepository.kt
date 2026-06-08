package com.kinopolka.app.data.repository

import com.kinopolka.app.data.model.Movie
import com.kinopolka.app.data.remote.ApiService
import com.kinopolka.app.data.remote.apiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: ApiService,
) {
    /** Каталог фильмов с опциональным поиском по названию/жанру. */
    suspend fun catalog(query: String?): Result<List<Movie>> = apiCall {
        api.movies(query?.takeIf { it.isNotBlank() })
    }
}
