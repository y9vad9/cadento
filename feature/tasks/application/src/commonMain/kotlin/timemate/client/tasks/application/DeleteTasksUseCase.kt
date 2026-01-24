package timemate.client.tasks.application

import timemate.client.tasks.domain.TaskId

class DeleteTasksUseCase(
    private val taskRepository: TaskRepository
) {
    sealed interface Result {
        data class Success(val taskIds: List<TaskId>) : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(taskIds: List<TaskId>): Result {
        @Suppress("detekt.TooGenericExceptionCaught")
        return try {
            taskRepository.deleteTasks(taskIds)
            Result.Success(taskIds)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
