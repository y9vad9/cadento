package cadento.timers.application

import cadento.timers.domain.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTimersUseCase(
    private val repository: TimerRepository,
) {
    fun execute(
        filter: TimerFilter = TimerFilter(),
        sort: TimerSort = TimerSort.ByCreationTimeDesc,
    ): Flow<Result> {
        return repository.observeTimers(filter, sort).map { result ->
            result.map { Result.Success(it) }
                .getOrElse { Result.Error(it) }
        }
    }

    sealed interface Result {
        data class Success(val timers: List<Timer>) : Result
        data class Error(val error: Throwable) : Result
    }
}
