package com.kinopolka.app.data.remote

import com.kinopolka.app.data.model.BacklogItem
import com.kinopolka.app.data.model.BacklogStats
import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.data.model.Movie
import com.kinopolka.app.data.model.UpdateBacklogRequest
import com.kinopolka.app.data.model.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Описание REST API сервера «Кинополка». */
interface ApiService {

    // ---- Авторизация (открытые эндпоинты) ----
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/me")
    suspend fun me(): UserDto

    // ---- Каталог фильмов ----
    @GET("api/movies")
    suspend fun movies(@Query("query") query: String? = null): List<Movie>

    // ---- Личный бэклог ----
    @GET("api/backlog")
    suspend fun backlog(@Query("status") status: BacklogStatus? = null): List<BacklogItem>

    @GET("api/backlog/stats")
    suspend fun stats(): BacklogStats

    @POST("api/backlog")
    suspend fun addToBacklog(@Body request: AddToBacklogRequest): BacklogItem

    @PATCH("api/backlog/{itemId}")
    suspend fun updateBacklog(
        @Path("itemId") itemId: Long,
        @Body request: UpdateBacklogRequest,
    ): BacklogItem

    @DELETE("api/backlog/{itemId}")
    suspend fun removeFromBacklog(@Path("itemId") itemId: Long)
}
