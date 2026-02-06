package cadento.timers.application

import cadento.timers.domain.TimerId

class DeleteTimerUseCase(
    private val repository: TimerRepository,
) {
    suspend fun execute(id: TimerId): Result {
        return repository.deleteTimer(id)
            .map { result ->
                when (result) {
                    DeleteTimerResult.Success -> Result.Success
                    DeleteTimerResult.TimerNotFound -> Result.TimerNotFound
                }
            }
            .getOrElse { Result.Error(it) }
    }

    sealed interface Result {
        data object Success : Result
        data object TimerNotFound : Result
        data class Error(val error: Throwable) : Result
    }
}
