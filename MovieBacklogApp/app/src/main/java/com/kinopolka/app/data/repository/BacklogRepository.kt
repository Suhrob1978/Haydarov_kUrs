package com.kinopolka.app.data.repository

import com.kinopolka.app.data.model.BacklogItem
import com.kinopolka.app.data.model.BacklogStats
import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.data.model.UpdateBacklogRequest
import com.kinopolka.app.data.remote.AddToBacklogRequest
import com.kinopolka.app.data.remote.ApiService
import com.kinopolka.app.data.remote.apiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BacklogRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun list(status: BacklogStatus?): Result<List<BacklogItem>> = apiCall {
        api.backlog(status)
    }

    suspend fun stats(): Result<BacklogStats> = apiCall {
        api.stats()
    }

    suspend fun add(movieId: Long, status: BacklogStatus): Result<BacklogItem> = apiCall {
        api.addToBacklog(AddToBacklogRequest(movieId = movieId, status = status))
    }

    suspend fun update(itemId: Long, request: UpdateBacklogRequest): Result<BacklogItem> = apiCall {
        api.updateBacklog(itemId, request)
    }

    suspend fun remove(itemId: Long): Result<Unit> = apiCall {
        api.removeFromBacklog(itemId)
    }
}
