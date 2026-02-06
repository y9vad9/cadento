package cadento.timers.domain.unittest

import cadento.timers.domain.LinkedTaskId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class LinkedTaskIdTest {

    @Test
    fun `value property should return value passed to constructor`() {
        // GIVEN
        val uuid = Uuid.random()
        val taskId = LinkedTaskId(uuid)

        // WHEN
        val result = taskId.value

        // THEN
        assertEquals(uuid, result)
    }
}
