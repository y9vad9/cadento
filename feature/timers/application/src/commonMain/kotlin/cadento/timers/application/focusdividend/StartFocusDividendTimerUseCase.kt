package cadento.timers.application.focusdividend

import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerId
import kotlin.time.Clock

class StartFocusDividendTimerUseCase(
    private val repository: TimerRepository,
    private val clock: Clock,
) {
    suspend fun execute(id: TimerId): Result {
        val timer = repository.getFocusDividendTimer(id).getOrElse { return Result.Error(it) }
            ?: return Result.TimerNotFound

        val now = clock.now()
        val updatedTimer = timer.start(now)
        
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
