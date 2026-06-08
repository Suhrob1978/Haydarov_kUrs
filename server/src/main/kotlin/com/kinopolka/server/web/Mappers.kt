package com.kinopolka.server.web

import com.kinopolka.server.domain.BacklogItem
import com.kinopolka.server.domain.Movie
import com.kinopolka.server.domain.User

fun User.toDto() = UserDto(id = id, email = email, displayName = displayName)

fun Movie.toDto() = MovieDto(
    id = id,
    title = title,
    year = year,
    genre = genre,
    description = description,
    posterUrl = posterUrl,
    rating = rating,
)

fun BacklogItem.toDto() = BacklogItemDto(
    id = id,
    movie = movie.toDto(),
    status = status,
    userRating = userRating,
    note = note,
)
