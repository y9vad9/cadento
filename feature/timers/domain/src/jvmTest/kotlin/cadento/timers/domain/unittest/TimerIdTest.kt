package cadento.timers.domain.unittest

import cadento.timers.domain.TimerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class TimerIdTest {

    @Test
    fun `value property should return value passed to constructor`() {
        // GIVEN
        val uuid = Uuid.random()
        val timerId = TimerId(uuid)

        // WHEN
        val result = timerId.value

        // THEN
        assertEquals(uuid, result)
    }
}
