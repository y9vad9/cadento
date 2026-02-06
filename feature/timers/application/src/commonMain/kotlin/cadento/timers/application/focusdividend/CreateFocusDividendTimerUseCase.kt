package cadento.timers.application.focusdividend

import cadento.timers.application.TimerIdProvider
import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerName
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.FocusDividendTimerSettings
import cadento.timers.domain.FocusDividendTimerState
import kotlin.time.Clock

class CreateFocusDividendTimerUseCase(
    private val repository: TimerRepository,
    private val clock: Clock,
    private val idProvider: TimerIdProvider,
) {
    suspend fun execute(
        name: TimerName,
        settings: FocusDividendTimerSettings = FocusDividendTimerSettings(),
    ): Result {
        val now = clock.now()
        val timer = FocusDividendTimer(
            id = idProvider.nextId(),
            name = name,
            creationTime = now,
            state = FocusDividendTimerState.Terminated(now, null),
            linkedTask = null,
            settings = settings,
        )
        return repository.createTimer(timer)
            .map { Result.Success(timer) }
            .getOrElse { Result.Error(it) }
    }

    sealed interface Result {
        data class Success(val timer: FocusDividendTimer) : Result
        data class Error(val error: Throwable) : Result
    }
}
