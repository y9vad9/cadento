package timemate.client.tasks.application

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskStatus
import timemate.client.tasks.domain.TaskTag
import kotlin.time.Clock

class GetTasksByTagAndStatusUseCase(
    private val taskRepository: TaskRepository,
    private val clock: Clock,
) {
    fun execute(tag: TaskTag? = null, status: TaskStatus? = null): Flow<Result> {
        return taskRepository.observeTasks(
            filter = TaskFilter(tag = tag, status = status),
            sort = TaskSort.ByDueTimeAsc,
            now = clock.now()
        ).map<_, Result> { tasks ->
            Result.Success(tasks)
        }.catch { e ->
            emit(Result.Error(e))
        }
    }

    sealed interface Result {
        data class Success(val tasks: List<Task>) : Result
        data class Error(val error: Throwable) : Result
    }
}
