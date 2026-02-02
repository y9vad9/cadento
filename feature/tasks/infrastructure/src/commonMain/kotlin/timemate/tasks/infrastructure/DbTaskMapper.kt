
package timemate.tasks.infrastructure

import timemate.client.tasks.application.TaskFilter
import timemate.client.tasks.application.TaskSort
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import timemate.client.tasks.domain.TaskStatus
import timemate.client.tasks.domain.TaskStatusId
import timemate.client.tasks.domain.TaskStatusName
import timemate.client.tasks.domain.TaskTag
import timemate.tasks.database.DbTask
import timemate.tasks.database.DbTaskFilter
import timemate.tasks.database.DbTaskSort
import kotlin.time.Instant

class DbTaskMapper {

    /** Converts TaskFilter to DbTaskFilter */
    fun mapFilter(
        taskFilter: TaskFilter,
    ): DbTaskFilter {
        return DbTaskFilter(
            dueBefore = taskFilter.dueBefore?.toString(),
            tag = taskFilter.tag?.string,
            statusId = taskFilter.status?.id?.long,
        )
    }

    /** Converts TaskSort to DbTaskSort */
    fun mapSort(
        taskSort: TaskSort,
    ): DbTaskSort {
        return when (taskSort) {
            TaskSort.ByCreationTimeAsc -> DbTaskSort.ByCreationAsc
            TaskSort.ByCreationTimeDesc -> DbTaskSort.ByCreationDesc
            TaskSort.ByDueTimeAsc -> DbTaskSort.ByDueAsc
            TaskSort.ByDueTimeDesc -> DbTaskSort.ByDueDesc
        }
    }

    /** Converts DbTask to domain Task */
    fun mapToDomain(
        dbTask: DbTask,
    ): Task {
        return Task.createOrThrow(
            id = TaskId(dbTask.id),
            name = TaskName.createOrThrow(dbTask.name),
            description = TaskDescription.createOrThrow(dbTask.description),
            status = TaskStatus.from(
                TaskStatusId.createOrThrow(dbTask.statusId),
                TaskStatusName.createOrThrow(dbTask.status),
            ),
            tags = dbTask.tags.map { TaskTag.createOrThrow(it) },
            creationTime = Instant.parse(dbTask.createdAt),
            dueTime = Instant.parse(dbTask.due),
        )
    }
}
