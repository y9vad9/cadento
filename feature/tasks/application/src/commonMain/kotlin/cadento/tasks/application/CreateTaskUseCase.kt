package cadento.tasks.application

import cadento.tasks.domain.Task

class CreateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend fun execute(task: Task): Result {
        return taskRepository.createTask(task)
            .map { Result.Success(task) }
            .getOrElse { Result.Error(it) }
    }

    sealed interface Result {
        data class Success(val task: Task) : Result
        data class Error(val error: Throwable) : Result
    }
}
