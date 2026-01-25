package timemate.client.tasks.application

import kotlinx.coroutines.flow.Flow
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskStatus
import timemate.client.tasks.domain.TaskStatusId
import timemate.client.tasks.domain.TaskTag
import kotlin.time.Instant

interface TaskRepository {
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: TaskId)
    suspend fun deleteTasks(taskIds: List<TaskId>)

    fun getTasks(filter: TaskFilter, sort: TaskSort): Flow<List<Task>>
    fun getTask(taskId: TaskId): Flow<Task?>

    fun getDueTasks(now: Instant): Flow<List<Task>>
    fun getTasksWithDueBetween(range: ClosedRange<Instant>): Flow<List<Task>>
}

data class TaskFilter(
    val dueBefore: Instant? = null,
    val tag: TaskTag? = null,
    val status: TaskStatus? = null,
    val statusId: TaskStatusId? = null,
)

sealed interface TaskSort {
    data object ByDueTimeAsc : TaskSort
    data object ByDueTimeDesc : TaskSort
    data object ByCreationTimeAsc : TaskSort
    data object ByCreationTimeDesc : TaskSort
}
