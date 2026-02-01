package timemate.tasks.infrastructure

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.time.Instant
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import timemate.client.tasks.domain.TaskStatus
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
class TaskRepositoryIntegrationTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: TaskDatabase
    private lateinit var repository: TaskRepositoryImpl
    private lateinit var tempFile: File

    @BeforeTest
    fun setup() = runTest {
        tempFile = File.createTempFile("tasks-repo-test", ".db")
        driver = JdbcSqliteDriver("jdbc:sqlite:${tempFile.absolutePath}")
        TaskDatabase.Schema.create(driver).await()
        database = TaskDatabase(driver)
        
        val databaseSource = TasksDatabaseSource(
            database = database,
            taskMapper = SqlDelightTaskMapper(),
            queryMapper = SqlDelightQueryMapper(),
        )

        repository = TaskRepositoryImpl(
            databaseSource = databaseSource,
            dbTaskMapper = DbTaskMapper(),
        )

        // Populate required statuses due to foreign key constraints
        // Using correct IDs from TaskStatusId.kt
        database.transaction {
            driver.execute(
                identifier = null,
                sql = "INSERT INTO PersistedTaskStatus (statusId, statusName) VALUES (-4, 'Planned')",
                parameters = 0,
            ).await()
            driver.execute(
                identifier = null,
                sql = "INSERT INTO PersistedTaskStatus (statusId, statusName) VALUES (-3, 'InProgress')",
                parameters = 0,
            ).await()
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    @Test
    fun `saveTask and observeTask correctly integrates with database`() = runTest {
        // GIVEN a task
        val id = TaskId(Uuid.random())
        val task = Task.createOrThrow(
            id = id,
            name = TaskName.createOrThrow("Integrated task"),
            description = TaskDescription.createOrThrow("Desc"),
            creationTime = Instant.parse("2026-01-25T12:00:00Z"),
            dueTime = Instant.parse("2026-01-25T13:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = emptyList(),
        )

        // WHEN we save the task
        val createResult = repository.createTask(task)
        assertTrue(createResult.isSuccess, "createTask failed: ${createResult.exceptionOrNull()}")

        // THEN we receive the saved task from observation
        repository.observeTask(id).test {
            val observed = awaitItem()
            assertTaskEquals(task, observed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun assertTaskEquals(expected: Task, actual: Task?) {
        assertEquals(
            expected = expected.id,
            actual = actual?.id,
            message = "Observed task ID should match saved task ID",
        )
        assertEquals(
            expected = expected.name,
            actual = actual?.name,
            message = "Observed task name should match saved task name",
        )
    }
}