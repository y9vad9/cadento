package timemate.client.tasks.application

import timemate.client.tasks.domain.TaskId

class DeleteTasksUseCase(
    private val taskRepository: TaskRepository
) {
    @Suppress("detekt.TooGenericExceptionCaught")
    suspend fun execute(taskIds: List<TaskId>): Result {
        return try {
            taskRepository.deleteTasks(taskIds)
            Result.Success(taskIds)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data class Success(val taskIds: List<TaskId>) : Result
        data class Error(val error: Throwable) : Result
    }
}
