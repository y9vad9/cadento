package cadento.tasks.application

import cadento.tasks.domain.Task
import cadento.tasks.domain.TaskId
import cadento.tasks.domain.TaskStatus
import cadento.tasks.domain.TaskTag
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface TaskRepository {
    suspend fun createTask(task: Task): Result<Unit>
    suspend fun updateTask(task: Task): Result<Task?>
    suspend fun deleteTask(taskId: TaskId): Result<Unit>
    suspend fun deleteTasks(taskIds: List<TaskId>): Result<Unit>

    fun observeTasks(filter: TaskFilter, sort: TaskSort, now: Instant): Flow<List<Task>>
    fun observeTask(taskId: TaskId): Flow<Task?>
    suspend fun getTask(taskId: TaskId): Result<Task?>

    fun observeDueTasks(now: Instant): Flow<List<Task>>
    fun observeTasksDueBetween(range: ClosedRange<Instant>): Flow<List<Task>>
}

data class TaskFilter(
    val dueBefore: Instant? = null,
    val tag: TaskTag? = null,
    val status: TaskStatus? = null,
)

sealed interface TaskSort {
    data object ByDueTimeAsc : TaskSort
    data object ByDueTimeDesc : TaskSort
    data object ByCreationTimeAsc : TaskSort
    data object ByCreationTimeDesc : TaskSort
}
