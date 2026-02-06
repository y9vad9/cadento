package cadento.timers.application

import cadento.timers.domain.TimerId

class UnlinkTaskFromTimerUseCase(
    private val repository: TimerRepository,
) {
    suspend fun execute(timerId: TimerId): Result {
        val timer = repository.getTimer(timerId).getOrElse { return Result.Error(it) }
            ?: return Result.TimerNotFound
        
        return try {
            val updatedTimer = timer.unlinkTask()
            repository.updateTimer(updatedTimer)
                .map { timer ->
                    if (timer == null) Result.TimerNotFound else Result.Success
                }
                .getOrElse { Result.Error(it) }
        } catch (e: IllegalArgumentException) {
            Result.Error(e)
        }
    }
    
    sealed interface Result {
        data object Success : Result
        data object TimerNotFound : Result
        data class Error(val error: Throwable) : Result
    }
}
