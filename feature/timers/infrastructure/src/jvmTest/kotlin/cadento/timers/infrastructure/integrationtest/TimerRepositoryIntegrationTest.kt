package cadento.timers.infrastructure.integrationtest

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import cadento.timers.application.TimerFilter
import cadento.timers.application.TimerSort
import cadento.timers.database.TimersDatabaseSource
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.RegularTimerState
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.infrastructure.DbTimerMapper
import cadento.timers.infrastructure.TimerRepositoryImpl
import cadento.timers.sqldelight.TimerDatabase
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.uuid.Uuid

class TimerRepositoryIntegrationTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: TimerDatabase
    private lateinit var repository: TimerRepositoryImpl
    private lateinit var tempFile: File

    @BeforeTest
    fun setup() = runTest {
        // GIVEN a temporary database file and initialized repository
        tempFile = File.createTempFile("timers-repo-test", ".db")
        driver = JdbcSqliteDriver("jdbc:sqlite:${tempFile.absolutePath}")
        TimerDatabase.Schema.create(driver).await()
        database = TimerDatabase(driver)
        
        repository = TimerRepositoryImpl(
            databaseSource = TimersDatabaseSource(database),
            dbTimerMapper = DbTimerMapper(),
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    @Test
    fun `observeTimers with created timer returns list containing timer`() = runTest {
        // GIVEN a regular timer
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = RegularTimer(
            id = TimerId(Uuid.random()),
            name = TimerName.createOrThrow("Integrated Timer"),
            creationTime = now,
            state = RegularTimerState.Inactive(now, null),
            linkedTask = null,
        )

        // WHEN we save the timer
        val result = repository.createTimer(timer)
        assertTrue(result.isSuccess)

        // THEN we can observe it from the repository
        repository.observeTimers(TimerFilter(), TimerSort.ByNameAsc).test {
            val observed = awaitItem().getOrThrow()
            assertEquals(1, observed.size)
            assertEquals(timer.id, observed[0].id)
            assertEquals(timer.name, observed[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
