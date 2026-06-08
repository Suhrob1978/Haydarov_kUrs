package com.kinopolka.server.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}

interface MovieRepository : JpaRepository<Movie, Long> {
    fun findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(title: String, genre: String): List<Movie>
}

interface BacklogItemRepository : JpaRepository<BacklogItem, Long> {
    fun findByUserIdOrderByUpdatedAtDesc(userId: Long): List<BacklogItem>
    fun findByUserIdAndStatusOrderByUpdatedAtDesc(userId: Long, status: BacklogStatus): List<BacklogItem>
    fun findByUserIdAndMovieId(userId: Long, movieId: Long): BacklogItem?
    fun findByIdAndUserId(id: Long, userId: Long): BacklogItem?
    fun countByUserIdAndStatus(userId: Long, status: BacklogStatus): Long
}
