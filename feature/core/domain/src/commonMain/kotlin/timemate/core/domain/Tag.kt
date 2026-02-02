package timemate.core.domain

import kotlin.time.Instant

data class Tag(
    val id: TagId,
    val name: TagName,
    val creationTime: Instant,
) {
    fun rename(name: TagName): Tag = copy(name = name)
}
