package timemate.client.tasks.domain.unittest

import timemate.client.tasks.domain.TaskId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.uuid.Uuid

class TaskIdTest {

    @Test
    fun `random generates unique TaskIds`() {
        // WHEN
        val id1 = TaskId.random()
        val id2 = TaskId.random()

        // THEN
        assertNotEquals(id1, id2)
    }

    @Test
    fun `TaskId can be constructed from a Uuid`() {
        // GIVEN
        val uuid = Uuid.random()

        // WHEN
        val taskId = TaskId(uuid)

        // THEN
        assertEquals(uuid, taskId.value)
    }
}
