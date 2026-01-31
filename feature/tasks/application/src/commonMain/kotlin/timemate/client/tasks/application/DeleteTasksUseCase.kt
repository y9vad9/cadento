package timemate.client.tasks.application

import timemate.client.tasks.domain.TaskId

class DeleteTasksUseCase(
    private val taskRepository: TaskRepository
) {
    suspend fun execute(taskIds: List<TaskId>): Result {
        return taskRepository.deleteTasks(taskIds)
            .map { Result.Success }
            .getOrElse { throwable -> Result.Error(throwable) }
    }

    sealed interface Result {
        data object Success : Result
        data class Error(val error: Throwable) : Result
    }
}
