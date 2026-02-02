package timemate.tasks.database

data class DbTaskFilter(
    val dueBefore: String? = null,
    val tag: String? = null,
    val statusId: Long? = null,
)

