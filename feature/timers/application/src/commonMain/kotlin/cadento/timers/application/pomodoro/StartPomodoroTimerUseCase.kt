package cadento.timers.application.pomodoro

import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerId
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.PomodoroTimerState
import kotlin.time.Clock

class StartPomodoroTimerUseCase(
    private val repository: TimerRepository,
    private val clock: Clock,
) {
    suspend fun execute(id: TimerId): Result {
        val timer = repository.getPomodoroTimer(id).getOrElse { return Result.Error(it) }
            ?: return Result.TimerNotFound

        val now = clock.now()
        val updatedTimer = timer.transition { state ->
             when (state) {
                 is PomodoroTimerState.Inactive -> state.start(now, timer.settings).nextState
                 is PomodoroTimerState.Paused -> state.resume(timer.settings, now).nextState
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
