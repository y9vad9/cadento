package timemate.client.tasks.application

import kotlinx.coroutines.flow.firstOrNull
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import timemate.client.tasks.domain.TaskStatus
import timemate.client.tasks.domain.TaskTag
import kotlin.time.Instant

class EditTaskUseCase(
    private val taskRepository: TaskRepository
) {
    @Suppress("detekt.TooGenericExceptionCaught")
    suspend fun execute(
        taskId: TaskId,
        patch: TaskPatch,
    ): Result {
        return try {
            val task = taskRepository.getTask(taskId).firstOrNull()
                ?: return Result.TaskNotFound

            val updatedTask = task
                .let { patch.newName?.let(it::rename) ?: it }
                .let { patch.newDescription?.let(it::changeDescription) ?: it }
                .let { patch.newStatus?.let(it::markAs) ?: it }
                .let { patch.newTags?.let(it::updateTags) ?: it }
                .let { applyDueTime(it, patch.newDueTime) ?: return Result.InvalidDueTime }

            taskRepository.updateTask(updatedTask)
            Result.Success(updatedTask)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun applyDueTime(task: Task, newDueTime: Instant?): Task? {
        if (newDueTime == null) return task

        return when (val result = task.changeDueTime(newDueTime)) {
            is Task.ChangeDueTimeResult.Success -> result.task
            is Task.ChangeDueTimeResult.InvalidTimeRange -> null
        }
    }

    data class TaskPatch(
        val newName: TaskName? = null,
        val newDescription: TaskDescription? = null,
        val newDueTime: Instant? = null,
        val newStatus: TaskStatus? = null,
        val newTags: List<TaskTag>? = null,
    )

    sealed interface Result {
        data class Success(val task: Task) : Result
        data object TaskNotFound : Result
        data object InvalidDueTime : Result
        data class Error(val error: Throwable) : Result
    }
}
