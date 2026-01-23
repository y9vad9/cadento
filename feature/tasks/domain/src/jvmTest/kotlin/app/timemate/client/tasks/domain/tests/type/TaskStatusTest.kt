package app.timemate.client.tasks.domain.tests.type

import app.timemate.client.tasks.domain.type.TaskStatus
import app.timemate.client.tasks.domain.type.isBuiltin
import app.timemate.client.tasks.domain.type.isCustom
import app.timemate.client.tasks.domain.type.isDone
import app.timemate.client.tasks.domain.type.isInProgress
import app.timemate.client.tasks.domain.type.isPaused
import app.timemate.client.tasks.domain.type.isPlanned
import app.timemate.client.tasks.domain.type.value.TaskStatusId
import app.timemate.client.tasks.domain.type.value.TaskStatusName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class TaskStatusTest {

    private val customId = TaskStatusId.createOrThrow(9999L)
    private val customName = TaskStatusName.createOrThrow("CustomStatus")

    @Test
    fun `from returns builtin instances for all builtin ids`() {
        // GIVEN all builtin ids and their corresponding names
        val builtinData = listOf(
            Triple(TaskStatusId.PLANNED, TaskStatusName.PLANNED, TaskStatus.Builtin.Planned::class),
            Triple(TaskStatusId.IN_PROGRESS, TaskStatusName.IN_PROGRESS, TaskStatus.Builtin.InProgress::class),
            Triple(TaskStatusId.PAUSED, TaskStatusName.PAUSED, TaskStatus.Builtin.Paused::class),
            Triple(TaskStatusId.DONE, TaskStatusName.DONE, TaskStatus.Builtin.Done::class),
        )

        // WHEN / THEN
        for ((id, name, expectedClass) in builtinData) {
            val status = TaskStatus.from(id, name)
            assertTrue(status.isBuiltin(), "Expected status with id=$id to be builtin")
            assertEquals(
                expected = expectedClass,
                actual = status::class,
                message = "Expected status class for id=$id to be $expectedClass",
            )

            // Also verify the per-variant checks
            when (status) {
                is TaskStatus.Builtin.Planned -> assertTrue(status.isPlanned())
                is TaskStatus.Builtin.InProgress -> assertTrue(status.isInProgress())
                is TaskStatus.Builtin.Paused -> assertTrue(status.isPaused())
                is TaskStatus.Builtin.Done -> assertTrue(status.isDone())
            }
        }
    }

    @Test
    fun `from returns custom instance for non builtin id`() {
        // GIVEN non builtin id and valid name
        val id = customId
        val name = customName

        // WHEN
        val status = TaskStatus.from(id, name)

        // THEN
        assertTrue(status is TaskStatus.Custom)
        assertTrue(status.isCustom())
        @Suppress("KotlinConstantConditions") // we check that contract is valid
        assertFalse(status.isBuiltin())
    }

    @Test
    fun `Custom constructor throws if id is builtin`() {
        // GIVEN a builtin id with custom name
        val id = TaskStatusId.PLANNED
        val name = customName

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TaskStatus.Custom(id, name)
        }
    }

    @Test
    fun `Custom constructor throws if name is builtin`() {
        // GIVEN a custom id with builtin name
        val id = customId
        val name = TaskStatusName.PLANNED

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TaskStatus.Custom(id, name)
        }
    }

    @Test
    fun `isBuiltin returns true for builtin statuses`() {
        // GIVEN builtin statuses
        val statuses = listOf(
            TaskStatus.Builtin.Planned,
            TaskStatus.Builtin.InProgress,
            TaskStatus.Builtin.Paused,
            TaskStatus.Builtin.Done,
        )

        // WHEN / THEN
        statuses.forEach { status ->
            assertTrue(status.isBuiltin(), "Expected $status to be builtin")
            @Suppress("KotlinConstantConditions") // we check that contract is valid
            assertFalse(status.isCustom(), "Expected $status NOT to be custom")
        }
    }

    @Test
    fun `isCustom returns true for custom status`() {
        // GIVEN a custom status
        val custom = TaskStatus.Custom(customId, customName)

        // WHEN / THEN
        assertTrue(custom.isCustom())
        @Suppress("KotlinConstantConditions") // we check that contract is valid
        assertFalse(custom.isBuiltin())
    }

    @Suppress("KotlinConstantConditions") // we check that contract is valid
    @Test
    fun `specific isX() functions correctly identify builtin statuses`() {
        // GIVEN each builtin status
        val planed = TaskStatus.Builtin.Planned
        val inProgress = TaskStatus.Builtin.InProgress
        val paused = TaskStatus.Builtin.Paused
        val done = TaskStatus.Builtin.Done

        // WHEN / THEN
        assertTrue(planed.isPlanned())
        assertFalse(planed.isDone())

        assertTrue(inProgress.isInProgress())
        assertFalse(inProgress.isPaused())

        assertTrue(paused.isPaused())
        assertFalse(paused.isPlanned())

        assertTrue(done.isDone())
        assertFalse(done.isInProgress())
    }
}
