package com.kinopolka.app.data.repository

import com.kinopolka.app.data.AppEventBus
import com.kinopolka.app.data.model.BacklogItem
import com.kinopolka.app.data.model.BacklogStats
import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.data.model.UpdateBacklogRequest
import com.kinopolka.app.data.remote.AddToBacklogRequest
import com.kinopolka.app.data.remote.ApiService
import com.kinopolka.app.data.remote.apiCall
import com.kinopolka.app.data.withServerPoster
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BacklogRepository @Inject constructor(
    private val api: ApiService,
    private val events: AppEventBus,
) {
    suspend fun list(status: BacklogStatus?): Result<List<BacklogItem>> = apiCall {
        api.backlog(status).map { it.withServerPoster() }
    }

    suspend fun stats(): Result<BacklogStats> = apiCall {
        api.stats()
    }

    suspend fun add(movieId: Long, status: BacklogStatus): Result<BacklogItem> = apiCall {
        api.addToBacklog(AddToBacklogRequest(movieId = movieId, status = status)).withServerPoster()
    }.also { result -> if (result.isSuccess) events.notifyBacklogChanged() }

    suspend fun update(itemId: Long, request: UpdateBacklogRequest): Result<BacklogItem> = apiCall {
        api.updateBacklog(itemId, request).withServerPoster()
    }.also { result -> if (result.isSuccess) events.notifyBacklogChanged() }

    suspend fun remove(itemId: Long): Result<Unit> = apiCall {
        api.removeFromBacklog(itemId)
    }.also { result -> if (result.isSuccess) events.notifyBacklogChanged() }
}
