package cadento.timers.domain.unittest

import cadento.timers.domain.TimerTag
import cadento.timers.domain.TimerTagId
import cadento.timers.domain.TimerTagName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock

class TimerTagTest {
    @Test
    fun `TimerTag constructor should assign properties correctly`() {
        // GIVEN
        val id = 1L
        val name = "Test Tag"
        val creationTime = Clock.System.now()

        // WHEN
        val timerTag = TimerTag(
            id = TimerTagId.createOrThrow(id),
            name = TimerTagName.createOrThrow(name),
            creationTime = creationTime
        )

        // THEN
        assertEquals(id, timerTag.id.long)
        assertEquals(name, timerTag.name.string)
        assertEquals(creationTime, timerTag.creationTime)
    }
}
