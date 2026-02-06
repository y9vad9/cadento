package cadento.timers.database.integrationtest

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import cadento.timers.database.DbTimer
import cadento.timers.database.DbTimerSort
import cadento.timers.database.DbTimerState
import cadento.timers.database.DbTimerStateType
import cadento.timers.database.DbTimerType
import cadento.timers.database.TimersDatabaseSource
import cadento.timers.sqldelight.TimerDatabase
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class TimersDatabaseSourceTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: TimerDatabase
    private lateinit var source: TimersDatabaseSource
    private lateinit var tempFile: File

    @BeforeTest
    fun setup() = runTest {
        // GIVEN a temporary database file and initialized schema
        tempFile = File.createTempFile("timers-db-test", ".db")
        driver = JdbcSqliteDriver("jdbc:sqlite:${tempFile.absolutePath}")
        TimerDatabase.Schema.create(driver).await()
        database = TimerDatabase(driver)
        source = TimersDatabaseSource(database)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    @Test
    fun `observeTimer with existing timer returns timer`() = runTest {
        // GIVEN a database source and a valid timer
        val id = Uuid.random()
        val timer = createDbTimer(id)
        source.insertTimer(timer)

        // WHEN we observe the timer by ID
        val flow = source.observeTimer(id)

        // THEN it emits the inserted timer correctly
        flow.test {
            val observed = awaitItem()
            assertNotNull(observed)
            assertEquals(timer.id, observed.id)
            assertEquals(timer.name, observed.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateTimer with changed values changes stored timer`() = runTest {
        // GIVEN an existing timer in the database
        val id = Uuid.random()
        val timer = createDbTimer(id)
        source.insertTimer(timer)
        val updatedTimer = timer.copy(name = "Updated")

        // WHEN we update the timer
        source.updateTimer(updatedTimer)

        // THEN observeTimer reflects the updated values
        source.observeTimer(id).test {
            val observed = awaitItem()
            assertNotNull(observed)
            assertEquals("Updated", observed.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTimer with existing id returns true`() = runTest {
        // GIVEN a timer in the database
        val id = Uuid.random()
        source.insertTimer(createDbTimer(id))

        // WHEN we delete the timer
        val result = source.deleteTimer(id)

        // THEN it returns true and the timer is removed
        assertEquals(true, result)
        source.observeTimer(id).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTimers with name filter returns matching timers`() = runTest {
        // GIVEN multiple timers in the database
        val timer1 = createDbTimer(Uuid.random(), "Focus")
        val timer2 = createDbTimer(Uuid.random(), "Break")
        source.insertTimer(timer1)
        source.insertTimer(timer2)

        // WHEN we observe with a "Focus" name filter
        val flow = source.observeTimers("Focus", DbTimerSort.BY_CREATION_DESC.name)

        // THEN it returns exactly one matching timer
        flow.test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Focus", list[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTimers with null filter returns all timers`() = runTest {
        // GIVEN multiple timers in the database
        source.insertTimer(createDbTimer(Uuid.random(), "Focus"))
        source.insertTimer(createDbTimer(Uuid.random(), "Break"))

        // WHEN we observe without a filter
        val flow = source.observeTimers(null, DbTimerSort.BY_NAME_ASC.name)

        // THEN it returns all timers in the database
        flow.test {
            val list = awaitItem()
            assertEquals(2, list.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTimers with name ascending sorts correctly`() = runTest {
        // GIVEN timers with different names
        val timerA = createDbTimer(Uuid.random(), "Apple")
        val timerC = createDbTimer(Uuid.random(), "Cherry")
        val timerB = createDbTimer(Uuid.random(), "Banana")
        source.insertTimer(timerA)
        source.insertTimer(timerC)
        source.insertTimer(timerB)

        // WHEN we observe with BY_NAME_ASC sort
        val flow = source.observeTimers(null, DbTimerSort.BY_NAME_ASC.name)

        // THEN they are sorted alphabetically
        flow.test {
            val list = awaitItem()
            assertEquals(3, list.size)
            assertEquals("Apple", list[0].name)
            assertEquals("Banana", list[1].name)
            assertEquals("Cherry", list[2].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTimers with creation descending sorts correctly`() = runTest {
        // GIVEN timers created at different times
        val oldTimer = createDbTimer(Uuid.random(), "Old").copy(createdAt = "2024-01-01T00:00:00Z")
        val newTimer = createDbTimer(Uuid.random(), "New").copy(createdAt = "2024-01-02T00:00:00Z")
        val midTimer = createDbTimer(Uuid.random(), "Mid").copy(createdAt = "2024-01-01T12:00:00Z")
        
        source.insertTimer(oldTimer)
        source.insertTimer(newTimer)
        source.insertTimer(midTimer)

        // WHEN we observe with BY_CREATION_DESC sort
        val flow = source.observeTimers(null, DbTimerSort.BY_CREATION_DESC.name)

        // THEN they are sorted from newest to oldest
        flow.test {
            val list = awaitItem()
            assertEquals(3, list.size)
            assertEquals("New", list[0].name)
            assertEquals("Mid", list[1].name)
            assertEquals("Old", list[2].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createDbTimer(id: Uuid, name: String = "Test") = DbTimer(
        id = id,
        name = name,
        type = DbTimerType.REGULAR,
        createdAt = "2024-01-01T00:00:00Z",
        linkedTask = null,
        state = DbTimerState(DbTimerStateType.REGULAR_INACTIVE, "2024-01-01T00:00:00Z", null),
        pomodoroSettings = null,
        focusDividendSettings = null
    )
}
