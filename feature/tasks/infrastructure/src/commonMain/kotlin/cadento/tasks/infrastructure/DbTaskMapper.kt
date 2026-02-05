
package cadento.tasks.infrastructure

import cadento.tasks.application.TaskFilter
import cadento.tasks.application.TaskSort
import cadento.tasks.database.DbTask
import cadento.tasks.database.DbTaskFilter
import cadento.tasks.database.DbTaskSort
import cadento.tasks.domain.Task
import cadento.tasks.domain.TaskDescription
import cadento.tasks.domain.TaskId
import cadento.tasks.domain.TaskName
import cadento.tasks.domain.TaskStatus
import cadento.tasks.domain.TaskStatusId
import cadento.tasks.domain.TaskStatusName
import cadento.tasks.domain.TaskTag
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
