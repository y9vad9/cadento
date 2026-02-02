
package timemate.tasks.infrastructure

import timemate.tasks.application.TaskFilter
import timemate.tasks.application.TaskSort
import timemate.tasks.domain.Task
import timemate.tasks.domain.TaskDescription
import timemate.tasks.domain.TaskId
import timemate.tasks.domain.TaskName
import timemate.tasks.domain.TaskStatus
import timemate.tasks.domain.TaskStatusId
import timemate.tasks.domain.TaskStatusName
import timemate.tasks.domain.TaskTag
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
