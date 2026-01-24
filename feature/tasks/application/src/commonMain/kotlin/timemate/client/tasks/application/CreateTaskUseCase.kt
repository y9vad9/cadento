package timemate.client.tasks.application

import timemate.client.tasks.domain.Task

class CreateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    sealed interface Result {
        data class Success(val task: Task) : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(task: Task): Result {
        @Suppress("detekt.TooGenericExceptionCaught")
        return try {
            taskRepository.createTask(task)
            Result.Success(task)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
