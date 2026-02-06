package cadento.timers.infrastructure.unittest

import cadento.timers.infrastructure.UuidTimerIdProvider
import kotlin.test.Test
import kotlin.test.assertNotNull

class UuidTimerIdProviderTest {

    @Test
    fun `nextId with call returns valid TimerId`() {
        // GIVEN a provider instance
        val provider = UuidTimerIdProvider()

        // WHEN we request a new ID
        val id = provider.nextId()

        // THEN we receive a non-null UUID-based ID
        assertNotNull(id.value)
    }
}
