package com.kinopolka.server.service

import com.kinopolka.server.domain.*
import com.kinopolka.server.web.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class BacklogService(
    private val backlog: BacklogItemRepository,
    private val movies: MovieRepository,
) {
    fun list(userId: Long, status: BacklogStatus?): List<BacklogItemDto> {
        val items = if (status == null) {
            backlog.findByUserIdOrderByUpdatedAtDesc(userId)
        } else {
            backlog.findByUserIdAndStatusOrderByUpdatedAtDesc(userId, status)
        }
        return items.map { it.toDto() }
    }

    @Transactional
    fun add(userId: Long, req: AddToBacklogRequest): BacklogItemDto {
        val movie = movies.findById(req.movieId).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "Фильм не найден")
        }
        backlog.findByUserIdAndMovieId(userId, req.movieId)?.let {
            throw ApiException(HttpStatus.CONFLICT, "Фильм уже в вашем бэклоге")
        }
        val item = backlog.save(BacklogItem(userId = userId, movie = movie, status = req.status))
        return item.toDto()
    }

    @Transactional
    fun update(userId: Long, itemId: Long, req: UpdateBacklogRequest): BacklogItemDto {
        val item = backlog.findByIdAndUserId(itemId, userId)
            ?: throw ApiException(HttpStatus.NOT_FOUND, "Элемент бэклога не найден")
        req.status?.let { item.status = it }
        req.userRating?.let { item.userRating = it }
        req.note?.let { item.note = it }
        item.updatedAt = Instant.now()
        return backlog.save(item).toDto()
    }

    @Transactional
    fun remove(userId: Long, itemId: Long) {
        val item = backlog.findByIdAndUserId(itemId, userId)
            ?: throw ApiException(HttpStatus.NOT_FOUND, "Элемент бэклога не найден")
        backlog.delete(item)
    }

    fun stats(userId: Long): BacklogStatsDto {
        val want = backlog.countByUserIdAndStatus(userId, BacklogStatus.WANT)
        val watching = backlog.countByUserIdAndStatus(userId, BacklogStatus.WATCHING)
        val watched = backlog.countByUserIdAndStatus(userId, BacklogStatus.WATCHED)
        return BacklogStatsDto(want, watching, watched, want + watching + watched)
    }
}
