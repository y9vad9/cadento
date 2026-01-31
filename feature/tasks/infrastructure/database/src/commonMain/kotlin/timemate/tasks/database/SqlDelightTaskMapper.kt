package timemate.tasks.database

import kotlin.uuid.Uuid

/**
 * Intermediate representation of a task row from the database.
 * Used to decouple the database source from SQLDelight-generated types
 * and provide a stable interface for mapping.
 */
data class TaskRow(
    val id: ByteArray,
    val name: String,
    val description: String,
    val createdAt: String,
    val due: String,
    val statusId: Long,
    val status: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TaskRow
        if (!id.contentEquals(other.id)) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (createdAt != other.createdAt) return false
        if (due != other.due) return false
        if (statusId != other.statusId) return false
        if (status != other.status) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + due.hashCode()
        result = 31 * result + statusId.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}

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
