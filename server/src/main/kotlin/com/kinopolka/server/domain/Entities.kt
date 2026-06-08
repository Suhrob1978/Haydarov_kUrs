package com.kinopolka.server.domain

import jakarta.persistence.*
import java.time.Instant

/** Статус фильма в личном бэклоге пользователя. */
enum class BacklogStatus { WANT, WATCHING, WATCHED }

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Column(nullable = false)
    var displayName: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()
}

/** Фильм из общего каталога. */
@Entity
@Table(name = "movies")
class Movie(
    @Column(nullable = false)
    var title: String,

    @Column(name = "release_year", nullable = false)
    var year: Int,

    @Column(nullable = false)
    var genre: String,

    @Column(length = 2000)
    var description: String = "",

    @Column(nullable = false)
    var posterUrl: String = "",

    /** Средняя оценка по данным TMDB-подобного источника, 0..10. */
    var rating: Double = 0.0,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}

/**
 * Элемент личного бэклога: связь «пользователь — фильм» с пользовательским
 * статусом, оценкой и заметкой.
 */
@Entity
@Table(
    name = "backlog_items",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "movie_id"])]
)
class BacklogItem(
    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    var movie: Movie,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BacklogStatus = BacklogStatus.WANT,

    /** Пользовательская оценка 0..10, 0 — не оценено. */
    var userRating: Int = 0,

    @Column(length = 1000)
    var note: String = "",
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var addedAt: Instant = Instant.now()

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
}
