package timemate.tasks.database

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timemate.tasks.sqldelight.TaskDatabase
import kotlin.uuid.Uuid

/**
 * Represent a set of changes to apply to a task.
 * Fields are optional; only non-null fields will be updated.
 */
data class DbTaskPatch(
    val name: String? = null,
    val description: String? = null,
    val due: String? = null,
    val statusId: Long? = null,
    val tags: List<String>? = null,
)

class TasksDatabaseSource(
    private val database: TaskDatabase,
    private val taskMapper: SqlDelightTaskMapper,
    private val queryMapper: SqlDelightQueryMapper,
) {
    private val queries get() = database.taskQueries

    /* ---------- writes ---------- */

    suspend fun insertTask(task: DbTask) = database.transaction {
        val taskIdBytes = task.id.toByteArray()

        queries.insertTask(
            id = taskIdBytes,
            name = task.name,
            description = task.description,
            createdAt = task.createdAt,
            due = task.due,
            statusId = task.statusId,
        )

        task.tags.forEach { tag ->
            queries.insertTaskTag(
                taskId = taskIdBytes,
                tag = tag,
            )
        }
    }

    suspend fun updateTask(id: Uuid, patch: DbTaskPatch) = database.transaction {
        val taskIdBytes = id.toByteArray()

        queries.updateTask(
            id = taskIdBytes,
            name = patch.name,
            description = patch.description,
            due = patch.due,
            statusId = patch.statusId,
        )

        if (patch.tags != null) {
            queries.deleteTagsForTask(taskId = taskIdBytes)
            patch.tags.forEach { tag ->
                queries.insertTaskTag(
                    taskId = taskIdBytes,
                    tag = tag,
                )
            }
        }
    }

    suspend fun deleteTasks(ids: List<Uuid>) {
        queries.deleteTasksByIds(ids = ids.map { it.toByteArray() })
    }

    /* ---------- reads ---------- */

    fun observeTask(id: Uuid): Flow<DbTask?> =
        queries
            .selectTaskById(id = id.toByteArray())
            .asFlow()
            .map { query ->
                query.awaitAsOneOrNull()
                    ?.let { queryMapper.fromSelectTaskById(it) }
                    ?.let { attachTags(listOf(it)).first() }
            }

    fun observeTasks(
        filter: DbTaskFilter,
        sort: DbTaskSort,
        now: String,
    ): Flow<List<DbTask>> {
        val query = database.taskQueries.selectTasksFiltered(
            dueBefore = filter.dueBefore ?: now,
            statusId = filter.statusId,
            tag = filter.tag,
            sort = sort.name,
        )

        return query
            .asFlow()
            .map { cursor ->
                val rows = cursor.executeAsList().map { queryMapper.fromSelectTasksFiltered(it) }
                attachTags(rows)
            }
    }

    fun observeDueTasks(now: String, sort: DbTaskSort): Flow<List<DbTask>> {
        val query = if (sort == DbTaskSort.ByDueAsc) {
            queries.selectDueTasksAsc(now = now)
        } else {
            queries.selectDueTasksDesc(now = now)
        }

        return query
            .asFlow()
            .map { cursor ->
                val rows = cursor.awaitAsList().map {
                    if (sort == DbTaskSort.ByDueAsc) {
                        queryMapper.fromSelectDueTasksAsc(it as timemate.tasks.sqldelight.SelectDueTasksAsc)
                    } else {
                        queryMapper.fromSelectDueTasksDesc(it as timemate.tasks.sqldelight.SelectDueTasksDesc)
                    }
                }
                attachTags(rows)
            }
    }

    fun observeTasksDueInRange(from: String, to: String): Flow<List<DbTask>> =
        queries
            .selectTasksDueInRangeAsc(from = from, to = to)
            .asFlow()
            .map { cursor ->
                val rows = cursor.awaitAsList().map { queryMapper.fromSelectTasksDueInRangeAsc(it) }
                attachTags(rows)
            }

    suspend fun loadTasksByIds(ids: List<Uuid>): List<DbTask> =
        attachTags(
            queries.selectTasksByIds(ids = ids.map { it.toByteArray() })
                .awaitAsList()
                .map { queryMapper.fromSelectTasksByIds(it) }
        )

    /* ---------- helpers ---------- */

    private suspend fun attachTags(tasks: List<TaskRow>): List<DbTask> {
        if (tasks.isEmpty()) return emptyList()

        val taskIds = tasks.map { it.id }
        val allTags = queries.selectTagsForTasks(taskIds).awaitAsList()

        // Group by UUID to avoid ByteArray key issues and O(N^2) lookups
        val tagsMap = allTags
            .groupBy { Uuid.fromByteArray(it.taskId) }
            .mapValues { it.value.map { row -> row.tag } }

        return taskMapper.mapAll(tasks, tagsMap)
    }
}
