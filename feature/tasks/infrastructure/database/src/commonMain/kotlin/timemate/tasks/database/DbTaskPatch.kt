package timemate.tasks.database

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
