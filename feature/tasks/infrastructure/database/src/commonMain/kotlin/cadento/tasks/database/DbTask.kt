package cadento.tasks.database

import kotlin.uuid.Uuid

data class DbTask(
    val id: Uuid,
    val name: String,
    val description: String,
    val createdAt: String,
    val due: String,
    val statusId: Long,
    val status: String,
    val tags: List<String>,
)
