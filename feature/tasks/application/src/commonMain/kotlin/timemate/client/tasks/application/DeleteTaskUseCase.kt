package timemate.client.tasks.application

import timemate.client.tasks.domain.TaskId

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository
) {
    sealed interface Result {
        data class Success(val taskId: TaskId) : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(taskId: TaskId): Result {
        @Suppress("detekt.TooGenericExceptionCaught")
        return try {
            taskRepository.deleteTask(taskId)
            Result.Success(taskId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
