package cadento.timers.infrastructure.integrationtest

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import cadento.timers.application.DeleteTimerUseCase
import cadento.timers.application.TimerRepository
import cadento.timers.application.focusdividend.CreateFocusDividendTimerUseCase
import cadento.timers.application.focusdividend.StartFocusDividendTimerUseCase
import cadento.timers.application.focusdividend.StopFocusDividendTimerUseCase
import cadento.timers.application.pomodoro.CreatePomodoroTimerUseCase
import cadento.timers.application.pomodoro.StartPomodoroTimerUseCase
import cadento.timers.application.pomodoro.StopPomodoroTimerUseCase
import cadento.timers.application.regular.CreateRegularTimerUseCase
import cadento.timers.application.regular.StartRegularTimerUseCase
import cadento.timers.application.regular.StopRegularTimerUseCase
import cadento.timers.database.TimersDatabaseSource
import cadento.timers.domain.FocusDividendTimerState
import cadento.timers.domain.PomodoroTimerState
import cadento.timers.domain.RegularTimerState
import cadento.timers.domain.TimerName
import cadento.timers.infrastructure.DbTimerMapper
import cadento.timers.infrastructure.TimerRepositoryImpl
import cadento.timers.infrastructure.UuidTimerIdProvider
import cadento.timers.sqldelight.TimerDatabase
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock

class TimerUseCaseIntegrationTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: TimerDatabase
    private lateinit var repository: TimerRepository
    private lateinit var tempFile: File
    private val clock = Clock.System
    private val idProvider = UuidTimerIdProvider()

    // Use cases
    private lateinit var createPomodoro: CreatePomodoroTimerUseCase
    private lateinit var startPomodoro: StartPomodoroTimerUseCase
    private lateinit var stopPomodoro: StopPomodoroTimerUseCase
    private lateinit var createRegular: CreateRegularTimerUseCase
    private lateinit var startRegular: StartRegularTimerUseCase
    private lateinit var stopRegular: StopRegularTimerUseCase
    private lateinit var createFocus: CreateFocusDividendTimerUseCase
    private lateinit var startFocus: StartFocusDividendTimerUseCase
    private lateinit var stopFocus: StopFocusDividendTimerUseCase
    private lateinit var deleteTimer: DeleteTimerUseCase

    @BeforeTest
    fun setup() = runTest {
        // GIVEN a temporary database file and initialized repository
        tempFile = File.createTempFile("timers-integration-test", ".db")
        driver = JdbcSqliteDriver("jdbc:sqlite:${tempFile.absolutePath}")
        TimerDatabase.Schema.create(driver).await()
        database = TimerDatabase(driver)
        
        repository = TimerRepositoryImpl(
            databaseSource = TimersDatabaseSource(database),
            dbTimerMapper = DbTimerMapper(),
        )

        createPomodoro = CreatePomodoroTimerUseCase(repository, clock, idProvider)
        startPomodoro = StartPomodoroTimerUseCase(repository, clock)
        stopPomodoro = StopPomodoroTimerUseCase(repository, clock)
        
        createRegular = CreateRegularTimerUseCase(repository, clock, idProvider)
        startRegular = StartRegularTimerUseCase(repository, clock)
        stopRegular = StopRegularTimerUseCase(repository, clock)
        
        createFocus = CreateFocusDividendTimerUseCase(repository, clock, idProvider)
        startFocus = StartFocusDividendTimerUseCase(repository, clock)
        stopFocus = StopFocusDividendTimerUseCase(repository, clock)
        
        deleteTimer = DeleteTimerUseCase(repository)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    @Test
    fun `execute pomodoro lifecycle with valid actions returns expected states`() = runTest {
        // GIVEN a new pomodoro timer
        val name = TimerName.createOrThrow("Focus Session")
        val createResult = createPomodoro.execute(name)
        val timerId = (createResult as CreatePomodoroTimerUseCase.Result.Success).timer.id

        // WHEN we start the timer
        val startResult = startPomodoro.execute(timerId)
        assertIs<StartPomodoroTimerUseCase.Result.Success>(startResult)

        // THEN it should be in Focus state in the database
        val startedTimer = repository.getPomodoroTimer(timerId).getOrThrow()
        assertIs<PomodoroTimerState.Focus>(startedTimer?.state)

        // WHEN we stop the timer
        val stopResult = stopPomodoro.execute(timerId)
        assertIs<StopPomodoroTimerUseCase.Result.Success>(stopResult)

        // THEN it should be in Paused state
        val stoppedTimer = repository.getPomodoroTimer(timerId).getOrThrow()
        assertIs<PomodoroTimerState.Paused>(stoppedTimer?.state)
    }

    @Test
    fun `execute regular lifecycle with valid actions returns expected states`() = runTest {
        // GIVEN a new regular timer
        val name = TimerName.createOrThrow("Simple Timer")
        val createResult = createRegular.execute(name)
        val timerId = (createResult as CreateRegularTimerUseCase.Result.Success).timer.id

        // WHEN we start the timer
        val startResult = startRegular.execute(timerId)
        assertIs<StartRegularTimerUseCase.Result.Success>(startResult)

        // THEN it is active in the repository
        val startedTimer = repository.getRegularTimer(timerId).getOrThrow()
        assertIs<RegularTimerState.Active>(startedTimer?.state)

        // WHEN we stop the timer
        val stopResult = stopRegular.execute(timerId)
        assertIs<StopRegularTimerUseCase.Result.Success>(stopResult)

        // THEN it is inactive
        val stoppedTimer = repository.getRegularTimer(timerId).getOrThrow()
        assertIs<RegularTimerState.Inactive>(stoppedTimer?.state)
    }

    @Test
    fun `execute focus lifecycle with valid actions returns expected states`() = runTest {
        // GIVEN a new focus dividend timer
        val name = TimerName.createOrThrow("Deep Work")
        val createResult = createFocus.execute(name)
        val timerId = (createResult as CreateFocusDividendTimerUseCase.Result.Success).timer.id

        // WHEN we start earning focus credits
        val startResult = startFocus.execute(timerId)
        assertIs<StartFocusDividendTimerUseCase.Result.Success>(startResult)

        // THEN it is in Earning state in the database
        val startedTimer = repository.getFocusDividendTimer(timerId).getOrThrow()
        assertIs<FocusDividendTimerState.Earning>(startedTimer?.state)

        // WHEN we stop focus session
        val stopResult = stopFocus.execute(timerId)
        assertIs<StopFocusDividendTimerUseCase.Result.Success>(stopResult)

        // THEN it transitions to Terminated state
        val stoppedTimer = repository.getFocusDividendTimer(timerId).getOrThrow()
        assertIs<FocusDividendTimerState.Terminated>(stoppedTimer?.state)
    }

    @Test
    fun `execute delete with existing timer returns Success`() = runTest {
        // GIVEN an existing timer in the database
        val name = TimerName.createOrThrow("To Delete")
        val createResult = createRegular.execute(name)
        val timerId = (createResult as CreateRegularTimerUseCase.Result.Success).timer.id

        // WHEN we delete the timer via use case
        val result = deleteTimer.execute(timerId)

        // THEN deletion is successful and timer is gone from the repository
        assertIs<DeleteTimerUseCase.Result.Success>(result)
        val timer = repository.getTimer(timerId).getOrThrow()
        assertEquals(null, timer)
    }
}
