package cadento.timers.application

import cadento.timers.domain.LinkedTimerTask
import cadento.timers.domain.TimerId

class LinkTaskToTimerUseCase(
    private val repository: TimerRepository,
) {
    suspend fun execute(timerId: TimerId, task: LinkedTimerTask): Result {
        val timer = repository.getTimer(timerId).getOrElse { return Result.Error(it) }
            ?: return Result.TimerNotFound
        
        val updatedTimer = timer.linkTask(task)
        
        return repository.updateTimer(updatedTimer)
            .map { timer ->
                if (timer == null) Result.TimerNotFound else Result.Success
            }
            .getOrElse { Result.Error(it) }
    }
    
    sealed interface Result {
        data object Success : Result
        data object TimerNotFound : Result
        data class Error(val error: Throwable) : Result
    }
}
