package timemate.tasks.database.integrationtest

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import timemate.tasks.database.DbTask
import timemate.tasks.database.DbTaskPatch
import timemate.tasks.database.DbTaskSort
import timemate.tasks.database.SqlDelightQueryMapper
import timemate.tasks.database.SqlDelightTaskMapper
import timemate.tasks.database.TasksDatabaseSource
import timemate.tasks.sqldelight.TaskDatabase
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class TasksDatabaseSourceTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: TaskDatabase
    private lateinit var source: TasksDatabaseSource
    private lateinit var tempFile: File

    @BeforeTest
    fun setup() = runTest {
        tempFile = File.createTempFile("tasks-test", ".db")
        driver = JdbcSqliteDriver("jdbc:sqlite:${tempFile.absolutePath}")
        TaskDatabase.Schema.create(driver).await()
        database = TaskDatabase(driver)
        source = TasksDatabaseSource(
            database = database,
            taskMapper = SqlDelightTaskMapper(),
            queryMapper = SqlDelightQueryMapper(),
        )

        // Populate required statuses due to foreign key constraints
        database.transaction {
            database.taskQueries.run {
                driver.execute(
                    identifier = null,
                    sql = "INSERT INTO PersistedTaskStatus (statusId, statusName) VALUES (1, 'Planned')",
                    parameters = 0,
                )
                driver.execute(
                    identifier = null,
                    sql = "INSERT INTO PersistedTaskStatus (statusId, statusName) VALUES (2, 'InProgress')",
                    parameters = 0,
                )
            }
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
        tempFile.delete()
    }

    @Test
    fun `insertTask and loadTasksByIds with single task returns same task`() = runTest {
        // GIVEN a task and an empty database
        val id = Uuid.random()
        val task = DbTask(
            id = id,
            name = "Test task",
            description = "Some description",
            createdAt = "2026-01-25T12:00:00Z",
            due = "2026-01-25T12:30:00Z",
            statusId = 1,
            status = "Planned",
            tags = listOf("tag1", "tag2"),
        )

        // WHEN we insert the task
        source.insertTask(task)

        // THEN loadTasksByIds returns exactly one task matching the input
        val loaded = source.loadTasksByIds(
            ids = listOf(id),
        )
        assertEquals(
            expected = 1,
            actual = loaded.size,
        )
        assertEquals(
            expected = task,
            actual = loaded.first(),
        )
    }

    @Test
    fun `observeTask after updateTask emits updated task`() = runTest {
        // GIVEN an existing task in database
        val id = Uuid.random()
        val originalTask = DbTask(
            id = id,
            name = "Original",
            description = "Desc",
            createdAt = "2026-01-25T12:00:00Z",
            due = "2026-01-25T12:30:00Z",
            statusId = 1,
            status = "Planned",
            tags = listOf("t1"),
        )

        source.insertTask(originalTask)

        // WHEN & THEN we observe the task and update it
        source.observeTask(id).test {
            // Initial emission
            val first = awaitItem()
            assertEquals(
                expected = originalTask,
                actual = first,
            )

            // Update operation
            source.updateTask(
                id = id,
                patch = DbTaskPatch(
                    name = "Updated",
                    statusId = 2L,
                    tags = listOf("t2", "t3"),
                ),
            )

            // Second emission reflects updates
            val updated = awaitItem()!!
            assertEquals(
                expected = "Updated",
                actual = updated.name,
            )
            assertEquals(
                expected = 2L,
                actual = updated.statusId,
            )
            assertEquals(
                expected = listOf("t2", "t3"),
                actual = updated.tags,
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTasks with existing ids removes tasks from database`() = runTest {
        // GIVEN a task in database
        val id = Uuid.random()
        source.insertTask(
            DbTask(
                id = id,
                name = "ToDelete",
                description = "Desc",
                createdAt = "2026-01-25T12:00:00Z",
                due = "2026-01-25T12:30:00Z",
                statusId = 1L,
                status = "Planned",
                tags = listOf("tag"),
            ),
        )

        // WHEN we delete the task
        source.deleteTasks(listOf(id))

        // THEN loadTasksByIds returns empty list
        val loaded = source.loadTasksByIds(
            ids = listOf(id),
        )
        assertTrue(loaded.isEmpty())
    }

    @Test
    fun `observeDueTasks with multiple tasks returns them in correct order`() = runTest {
        // GIVEN tasks with different due dates
        val now = "2026-01-25T12:00:00Z"
        val task1 = DbTask(
            id = Uuid.random(),
            name = "Later Task",
            description = "D1",
            createdAt = "2026-01-25T10:00:00Z",
            due = "2026-01-25T11:00:00Z",
            statusId = 1,
            status = "Planned",
            tags = emptyList(),
        )
        val task2 = DbTask(
            id = Uuid.random(),
            name = "Earlier Task",
            description = "D2",
            createdAt = "2026-01-25T10:30:00Z",
            due = "2026-01-25T10:30:00Z",
            statusId = 1,
            status = "Planned",
            tags = emptyList(),
        )

        listOf(task1, task2).forEach { t ->
            source.insertTask(t)
        }

        // WHEN we observe due tasks
        val list = source.observeDueTasks(now, DbTaskSort.ByDueAsc).first()

        // THEN result list is ordered by due date ascending
        assertEquals(
            expected = listOf(task2.id, task1.id),
            actual = list.map { it.id },
        )
    }

    @Test
    fun `observeTasksDueInRange with time range returns only tasks within range`() = runTest {
        // GIVEN tasks both inside and outside the time range
        val from = "2026-01-25T10:00:00Z"
        val to = "2026-01-25T11:00:00Z"

        val task1 = DbTask(
            id = Uuid.random(),
            name = "Inside Range 1",
            description = "D1",
            createdAt = "2026-01-25T09:00:00Z",
            due = "2026-01-25T10:30:00Z",
            statusId = 1,
            status = "Planned",
            tags = emptyList(),
        )
        val task2 = DbTask(
            id = Uuid.random(),
            name = "Inside Range 2",
            description = "D2",
            createdAt = "2026-01-25T09:30:00Z",
            due = "2026-01-25T11:00:00Z",
            statusId = 1,
            status = "Planned",
            tags = emptyList(),
        )
        val task3 = DbTask(
            id = Uuid.random(),
            name = "Outside Range",
            description = "D3",
            createdAt = "2026-01-25T09:45:00Z",
            due = "2026-01-25T11:30:00Z",
            statusId = 1,
            status = "Planned",
            tags = emptyList(),
        )

        listOf(task1, task2, task3).forEach { t ->
            source.insertTask(t)
        }

        // WHEN we observe tasks in range
        val result = source.observeTasksDueInRange(from, to).first()

        // THEN only tasks with due time within [from, to] are returned
        assertEquals(
            expected = listOf(task1.id, task2.id),
            actual = result.map { it.id },
        )
    }
}
