package cadento.timers.application.regular

import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerId
import cadento.timers.domain.RegularTimer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRegularTimerUseCase(
    private val repository: TimerRepository,
) {
    fun execute(id: TimerId): Flow<Result> {
        return repository.observeRegularTimer(id).map { result ->
            result.map { timer ->
                if (timer == null) Result.TimerNotFound else Result.Success(timer)
            }.getOrElse { Result.Error(it) }
        }
    }

    sealed interface Result {
        data class Success(val timer: RegularTimer) : Result
        data object TimerNotFound : Result
        data class Error(val error: Throwable) : Result
    }
}
