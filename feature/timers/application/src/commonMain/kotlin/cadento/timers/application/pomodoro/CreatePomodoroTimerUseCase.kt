package cadento.timers.application.pomodoro

import cadento.timers.application.TimerIdProvider
import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerName
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.PomodoroTimerSettings
import cadento.timers.domain.PomodoroTimerState
import kotlin.time.Clock

class CreatePomodoroTimerUseCase(
    private val repository: TimerRepository,
    private val clock: Clock,
    private val idProvider: TimerIdProvider,
) {
    suspend fun execute(
        name: TimerName,
        settings: PomodoroTimerSettings = PomodoroTimerSettings(),
    ): Result {
        val now = clock.now()
        val timer = PomodoroTimer(
            id = idProvider.nextId(),
            name = name,
            creationTime = now,
            state = PomodoroTimerState.Inactive(now),
            linkedTask = null,
            settings = settings,
        )
        return repository.createTimer(timer)
            .map { Result.Success(timer) }
            .getOrElse { Result.Error(it) }
    }

    sealed interface Result {
        data class Success(val timer: PomodoroTimer) : Result
        data class Error(val error: Throwable) : Result
    }
}
