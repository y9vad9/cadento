package timemate.tasks.infrastructure

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timemate.client.tasks.application.TaskFilter
import timemate.client.tasks.application.TaskRepository
import timemate.client.tasks.application.TaskSort
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskId
import timemate.coroutines.suspendRunCatching
import timemate.tasks.database.DbTask
import timemate.tasks.database.DbTaskPatch
import timemate.tasks.database.DbTaskSort
import timemate.tasks.database.TasksDatabaseSource
import kotlin.time.Instant

class TaskRepositoryImpl(
    private val databaseSource: TasksDatabaseSource,
    private val dbTaskMapper: DbTaskMapper,
) : TaskRepository {
    override suspend fun createTask(task: Task): Result<Unit> = suspendRunCatching {
        databaseSource.insertTask(
            task = DbTask(
                id = task.id.value,
                name = task.name.string,
                description = task.description.string,
                createdAt = task.creationTime.toString(),
                due = task.dueTime.toString(),
                statusId = task.status.id.long,
                status = task.status.name.string,
                tags = task.tags.map { it.string },
            ),
        )
    }

    override suspend fun updateTask(task: Task): Result<Task?> = suspendRunCatching {
        databaseSource.updateTask(
            id = task.id.value,
            patch = DbTaskPatch(
                name = task.name.string,
                description = task.description.string,
                due = task.dueTime.toString(),
                statusId = task.status.id.long,
                tags = task.tags.map { it.string },
            ),
        )
        task
    }

    override suspend fun deleteTask(taskId: TaskId): Result<Unit> = suspendRunCatching {
        databaseSource.deleteTasks(listOf(taskId.value))
    }

    override suspend fun deleteTasks(taskIds: List<TaskId>): Result<Unit> = suspendRunCatching {
        databaseSource.deleteTasks(taskIds.map { it.value })
    }

    override fun observeTasks(
        filter: TaskFilter,
        sort: TaskSort,
        now: Instant,
    ): Flow<List<Task>> {
        return databaseSource.observeTasks(
            filter = dbTaskMapper.mapFilter(filter),
            sort = dbTaskMapper.mapSort(sort),
            now = now.toString(),
        ).map { dbTasks ->
            dbTasks.map { dbTaskMapper.mapToDomain(it) }
        }
    }

    override fun observeTask(taskId: TaskId): Flow<Task?> {
        return databaseSource.observeTask(taskId.value)
            .map { dbTask ->
                dbTask?.let { dbTaskMapper.mapToDomain(it) }
            }
    }

    override suspend fun getTask(taskId: TaskId): Result<Task?> = suspendRunCatching {
        databaseSource.observeTask(taskId.value).firstOrNull()?.let { dbTaskMapper.mapToDomain(it) }
    }

    override fun observeDueTasks(now: Instant): Flow<List<Task>> {
        return databaseSource.observeDueTasks(now.toString(), DbTaskSort.ByDueDesc)
            .map { dbTasks -> dbTasks.map { dbTaskMapper.mapToDomain(it) } }
    }

    override fun observeTasksDueBetween(range: ClosedRange<Instant>): Flow<List<Task>> {
        return databaseSource.observeTasksDueInRange(range.start.toString(), range.endInclusive.toString())
            .map { dbTasks -> dbTasks.map { dbTaskMapper.mapToDomain(it) } }
    }
}
