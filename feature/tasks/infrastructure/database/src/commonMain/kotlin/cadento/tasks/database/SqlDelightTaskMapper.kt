package cadento.tasks.database

import kotlin.uuid.Uuid

class SqlDelightTaskMapper {

    /**
     * Maps a [TaskRow] and its associated tags to a [DbTask].
     */
    fun map(row: TaskRow, tags: List<String>): DbTask {
        return DbTask(
            id = Uuid.fromByteArray(row.id),
            name = row.name,
            description = row.description,
            createdAt = row.createdAt,
            due = row.due,
            statusId = row.statusId,
            status = row.status,
            tags = tags
        )
    }

    /**
     * Maps a list of [TaskRow]s and a map of tags to a list of [DbTask]s.
     */
    fun mapAll(rows: List<TaskRow>, tagsByTaskId: Map<Uuid, List<String>>): List<DbTask> {
        return rows.map { row ->
            val uuid = Uuid.fromByteArray(row.id)
            map(row, tagsByTaskId[uuid].orEmpty())
        }
    }
}
