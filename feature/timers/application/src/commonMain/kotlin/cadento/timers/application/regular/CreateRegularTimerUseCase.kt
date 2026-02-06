package cadento.timers.application.regular

import cadento.timers.application.TimerIdProvider
import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerName
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.RegularTimerState
import kotlin.time.Clock

class CreateRegularTimerUseCase(
    private val repository: TimerRepository,
    private val clock: Clock,
    private val idProvider: TimerIdProvider,
) {
    suspend fun execute(
        name: TimerName,
    ): Result {
        val now = clock.now()
        val timer = RegularTimer(
            id = idProvider.nextId(),
            name = name,
            creationTime = now,
            state = RegularTimerState.Inactive(now, null),
            linkedTask = null,
        )
        return repository.createTimer(timer)
            .map { Result.Success(timer) }
            .getOrElse { Result.Error(it) }
    }

    sealed interface Result {
        data class Success(val timer: RegularTimer) : Result
        data class Error(val error: Throwable) : Result
    }
}
