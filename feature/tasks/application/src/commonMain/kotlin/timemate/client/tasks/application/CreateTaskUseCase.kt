package timemate.client.tasks.application

import timemate.client.tasks.domain.Task

class CreateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    @Suppress("detekt.TooGenericExceptionCaught")
    suspend fun execute(task: Task): Result {
        return try {
            taskRepository.createTask(task)
            Result.Success(task)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data class Success(val task: Task) : Result
        data class Error(val error: Throwable) : Result
    }
}
