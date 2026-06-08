package com.kinopolka.app.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Сигнал об изменении данных бэклога (добавление, правка, удаление). */
@Singleton
class AppEventBus @Inject constructor() {
    private val _backlogChanged = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val backlogChanged: SharedFlow<Unit> = _backlogChanged.asSharedFlow()

    fun notifyBacklogChanged() {
        _backlogChanged.tryEmit(Unit)
    }
}
