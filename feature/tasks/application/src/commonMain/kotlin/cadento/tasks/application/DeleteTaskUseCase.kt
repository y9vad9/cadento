package cadento.tasks.application

import cadento.tasks.domain.TaskId

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend fun execute(taskId: TaskId): Result {
        return taskRepository.deleteTask(taskId)
            .map { Result.Success }
            .getOrElse { throwable -> Result.Error(throwable) }
    }

    sealed interface Result {
        data object Success : Result
        data class Error(val error: Throwable) : Result
    }
}
