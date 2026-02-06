package cadento.timers.application.pomodoro

import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerId
import cadento.timers.domain.PomodoroTimer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPomodoroTimerUseCase(
    private val repository: TimerRepository,
) {
    fun execute(id: TimerId): Flow<Result> {
        return repository.observePomodoroTimer(id).map { result ->
            result.map { timer ->
                if (timer == null) Result.TimerNotFound else Result.Success(timer)
            }.getOrElse { Result.Error(it) }
        }
    }

    sealed interface Result {
        data class Success(val timer: PomodoroTimer) : Result
        data object TimerNotFound : Result
        data class Error(val error: Throwable) : Result
    }
}
