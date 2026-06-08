package com.kinopolka.server.web

import com.kinopolka.server.domain.BacklogStatus
import com.kinopolka.server.service.AuthService
import com.kinopolka.server.service.BacklogService
import com.kinopolka.server.service.MovieService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody req: RegisterRequest): AuthResponse = authService.register(req)

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): AuthResponse = authService.login(req)
}

@RestController
@RequestMapping("/api/me")
class MeController(private val authService: AuthService) {

    @GetMapping
    fun me(@RequestAttribute("userId") userId: Long): UserDto = authService.currentUser(userId)
}

@RestController
@RequestMapping("/api/movies")
class MovieController(private val movieService: MovieService) {

    @GetMapping
    fun catalog(@RequestParam(required = false) query: String?): List<MovieDto> =
        movieService.catalog(query)
}

@RestController
@RequestMapping("/api/backlog")
class BacklogController(private val backlogService: BacklogService) {

    @GetMapping
    fun list(
        @RequestAttribute("userId") userId: Long,
        @RequestParam(required = false) status: BacklogStatus?,
    ): List<BacklogItemDto> = backlogService.list(userId, status)

    @GetMapping("/stats")
    fun stats(@RequestAttribute("userId") userId: Long): BacklogStatsDto =
        backlogService.stats(userId)

    @PostMapping
    fun add(
        @RequestAttribute("userId") userId: Long,
        @Valid @RequestBody req: AddToBacklogRequest,
    ): BacklogItemDto = backlogService.add(userId, req)

    @PatchMapping("/{itemId}")
    fun update(
        @RequestAttribute("userId") userId: Long,
        @PathVariable itemId: Long,
        @Valid @RequestBody req: UpdateBacklogRequest,
    ): BacklogItemDto = backlogService.update(userId, itemId, req)

    @DeleteMapping("/{itemId}")
    fun remove(
        @RequestAttribute("userId") userId: Long,
        @PathVariable itemId: Long,
    ) = backlogService.remove(userId, itemId)
}
