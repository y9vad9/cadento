package cadento.timers.application.regular

import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerId
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.RegularTimerState
import kotlin.time.Clock

class StartRegularTimerUseCase(
    private val repository: TimerRepository,
    private val clock: Clock,
) {
    suspend fun execute(id: TimerId): Result {
        val timer = repository.getRegularTimer(id).getOrElse { return Result.Error(it) }
            ?: return Result.TimerNotFound

        val now = clock.now()
        val updatedTimer = timer.transition { state ->
             when (state) {
                 is RegularTimerState.Inactive -> state.start(now).nextState
                 else -> state 
             }
        }
        
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
