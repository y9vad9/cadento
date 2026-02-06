package cadento.timers.infrastructure.unittest

import cadento.timers.application.TimerFilter
import cadento.timers.application.TimerSort
import cadento.timers.database.DbFocusDividendSettings
import cadento.timers.database.DbLinkedTask
import cadento.timers.database.DbPomodoroSettings
import cadento.timers.database.DbTimer
import cadento.timers.database.DbTimerSort
import cadento.timers.database.DbTimerState
import cadento.timers.database.DbTimerStateType
import cadento.timers.database.DbTimerType
import cadento.timers.domain.EarningCoefficient
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.FocusDividendTimerSettings
import cadento.timers.domain.FocusDividendTimerState
import cadento.timers.domain.LinkedTaskId
import cadento.timers.domain.LinkedTaskName
import cadento.timers.domain.LinkedTimerTask
import cadento.timers.domain.PomodoroConfirmationTimeoutTime
import cadento.timers.domain.PomodoroFocusTime
import cadento.timers.domain.PomodoroLongBreakPerShortBreaksCount
import cadento.timers.domain.PomodoroLongBreakTime
import cadento.timers.domain.PomodoroPreparationTime
import cadento.timers.domain.PomodoroShortBreakTime
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.PomodoroTimerSettings
import cadento.timers.domain.PomodoroTimerState
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.RegularTimerState
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.infrastructure.DbTimerMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

class DbTimerMapperTest {

    private val mapper = DbTimerMapper()
    private val now = Instant.parse("2024-01-01T00:00:00Z")
    private val later = Instant.parse("2024-01-01T00:30:00Z")

    @Test
    fun `mapToDb with PomodoroTimer returns DbTimer`() {
        // GIVEN a Pomodoro domain timer
        val timer = createPomodoroTimer()

        // WHEN we map it to its database representation
        val dbTimer = mapper.mapToDb(timer)

        // THEN all properties are correctly translated
        assertEquals(timer.id.value, dbTimer.id)
        assertEquals(timer.name.string, dbTimer.name)
        assertEquals(DbTimerType.POMODORO, dbTimer.type)
        assertEquals(timer.creationTime.toString(), dbTimer.createdAt)
        assertNotNull(dbTimer.pomodoroSettings)
        assertEquals(
            timer.settings.pomodoroFocusTime.duration.inWholeMilliseconds, 
            dbTimer.pomodoroSettings?.focusDuration
        )
    }

    @Test
    fun `mapToDomain with PomodoroDbTimer returns PomodoroTimer`() {
        // GIVEN a database Pomodoro timer
        val dbTimer = createDbPomodoroTimer()

        // WHEN we map it back to the domain
        val timer = mapper.mapToDomain(dbTimer)

        // THEN the resulting domain model has the correct type and state
        assertIs<PomodoroTimer>(timer)
        assertEquals(dbTimer.id, timer.id.value)
        assertEquals(dbTimer.name, timer.name.string)
        assertIs<PomodoroTimerState.Focus>(timer.state)
        assertEquals(dbTimer.state.startTime, timer.state.startTime.toString())
    }

    @Test
    fun `mapToDb with RegularTimer returns DbTimer`() {
        // GIVEN a Regular domain timer in an active state
        val timer = RegularTimer(
            id = TimerId(Uuid.random()),
            name = TimerName.createOrThrow("Regular"),
            creationTime = now,
            state = RegularTimerState.Active(now, null),
            linkedTask = null
        )

        // WHEN we map it to the database model
        val dbTimer = mapper.mapToDb(timer)

        // THEN the type and state are correctly mapped
        assertEquals(DbTimerType.REGULAR, dbTimer.type)
        assertEquals(DbTimerStateType.REGULAR_ACTIVE, dbTimer.state.type)
    }

    @Test
    fun `mapToDomain with RegularDbTimer returns RegularTimer`() {
        // GIVEN a database model for a Regular timer
        val dbTimer = DbTimer(
            id = Uuid.random(),
            name = "Regular",
            type = DbTimerType.REGULAR,
            createdAt = now.toString(),
            linkedTask = null,
            state = DbTimerState(DbTimerStateType.REGULAR_ACTIVE, now.toString(), null),
            pomodoroSettings = null,
            focusDividendSettings = null
        )

        // WHEN we map it to the domain
        val timer = mapper.mapToDomain(dbTimer)

        // THEN the type and state are correctly reconstructed
        assertIs<RegularTimer>(timer)
        assertIs<RegularTimerState.Active>(timer.state)
    }

    @Test
    fun `mapToDomain with inactive RegularDbTimer returns RegularTimer`() {
        // GIVEN an inactive database Regular timer
        val dbTimer = DbTimer(
            id = Uuid.random(),
            name = "Regular",
            type = DbTimerType.REGULAR,
            createdAt = now.toString(),
            linkedTask = null,
            state = DbTimerState(DbTimerStateType.REGULAR_INACTIVE, now.toString(), later.toString()),
            pomodoroSettings = null,
            focusDividendSettings = null
        )

        // WHEN we map it to the domain
        val timer = mapper.mapToDomain(dbTimer)

        // THEN the inactive state is correctly preserved
        assertIs<RegularTimer>(timer)
        assertIs<RegularTimerState.Inactive>(timer.state)
        assertEquals(later, (timer.state as RegularTimerState.Inactive).endTime)
    }

    @Test
    fun `mapToDb with FocusDividendTimer returns DbTimer`() {
        // GIVEN a Focus Dividend domain timer
        val timer = FocusDividendTimer(
            id = TimerId(Uuid.random()),
            name = TimerName.createOrThrow("Focus"),
            creationTime = now,
            state = FocusDividendTimerState.Earning(now, null),
            linkedTask = null,
            settings = FocusDividendTimerSettings(EarningCoefficient.createOrThrow(2.0)),
            balance = 10.minutes
        )

        // WHEN we map it to the database model
        val dbTimer = mapper.mapToDb(timer)

        // THEN all Focus Dividend specific settings are preserved
        assertEquals(DbTimerType.FOCUS_DIVIDEND, dbTimer.type)
        assertEquals(2.0, dbTimer.focusDividendSettings?.coefficient)
        assertEquals(10.minutes.inWholeMilliseconds, dbTimer.focusDividendSettings?.balance)
    }

    @Test
    fun `mapToDomain with FocusDividendDbTimer returns FocusDividendTimer`() {
        // GIVEN a database representation of a Focus Dividend timer
        val dbTimer = DbTimer(
            id = Uuid.random(),
            name = "Focus",
            type = DbTimerType.FOCUS_DIVIDEND,
            createdAt = now.toString(),
            linkedTask = null,
            state = DbTimerState(DbTimerStateType.FOCUS_DIVIDEND_EARNING, now.toString(), null),
            pomodoroSettings = null,
            focusDividendSettings = DbFocusDividendSettings(2.0, 10.minutes.inWholeMilliseconds)
        )

        // WHEN we map it to the domain model
        val timer = mapper.mapToDomain(dbTimer)

        // THEN the domain model is correctly reconstructed
        assertIs<FocusDividendTimer>(timer)
        assertEquals(2.0, timer.settings.earningCoefficient.value)
        assertEquals(10.minutes, timer.balance)
    }

    @Test
    fun `mapToDomain with terminated FocusDividendDbTimer returns FocusDividendTimer`() {
        // GIVEN a terminated database Focus Dividend timer
        val dbTimer = DbTimer(
            id = Uuid.random(),
            name = "Focus",
            type = DbTimerType.FOCUS_DIVIDEND,
            createdAt = now.toString(),
            linkedTask = null,
            state = DbTimerState(DbTimerStateType.FOCUS_DIVIDEND_TERMINATED, now.toString(), later.toString()),
            pomodoroSettings = null,
            focusDividendSettings = DbFocusDividendSettings(1.0, 0)
        )

        // WHEN we map it to the domain
        val timer = mapper.mapToDomain(dbTimer)

        // THEN the terminated state is correctly mapped
        assertIs<FocusDividendTimer>(timer)
        assertIs<FocusDividendTimerState.Terminated>(timer.state)
    }

    @Test
    fun `mapToDomain with LinkedTask returns Timer`() {
        // GIVEN a database timer with an associated linked task
        val taskId = Uuid.random()
        val dbTimer = createDbPomodoroTimer().copy(
            linkedTask = DbLinkedTask(taskId, "Task", now.toString(), later.toString())
        )

        // WHEN we map it to the domain
        val timer = mapper.mapToDomain(dbTimer)

        // THEN the linked task details are correctly mapped
        assertNotNull(timer.linkedTask)
        assertEquals(taskId, timer.linkedTask?.id?.value)
        assertEquals("Task", timer.linkedTask?.name?.string)
        assertEquals(now, timer.linkedTask?.creationTime)
        assertEquals(later, timer.linkedTask?.dueTime)
    }

    @Test
    fun `mapStateToDb with all states returns DbTimerStates`() {
        // GIVEN a list of all possible timer states
        val states = listOf(
            PomodoroTimerState.Inactive(now, later),
            PomodoroTimerState.Focus(now, later),
            PomodoroTimerState.Paused(now, later),
            PomodoroTimerState.ShortBreak(now, later),
            PomodoroTimerState.LongBreak(now, later),
            PomodoroTimerState.Preparation(now, later),
            PomodoroTimerState.AwaitsConfirmation(now, later),
            RegularTimerState.Inactive(now, later),
            RegularTimerState.Active(now, later),
            FocusDividendTimerState.Earning(now, later),
            FocusDividendTimerState.Spending(now, later),
            FocusDividendTimerState.Terminated(now, later)
        )

        // WHEN we map each state to its database type
        val dbTimers = states.map { state ->
            val timer = createTimerWithState(state)
            mapper.mapToDb(timer)
        }

        // THEN every state is successfully mapped to a valid database state type
        dbTimers.forEach { dbTimer ->
            assertNotNull(dbTimer.state.type)
        }
    }

    @Test
    fun `mapToDomain with unexpected pomodoro state throws Exception`() {
        // GIVEN a database Pomodoro timer with an invalid state type
        val dbTimer = createDbPomodoroTimer().copy(
            state = DbTimerState(DbTimerStateType.REGULAR_ACTIVE, now.toString(), later.toString())
        )

        // WHEN we attempt to map it to the domain, THEN it throws an exception
        assertFailsWith<IllegalArgumentException> {
            mapper.mapToDomain(dbTimer)
        }
    }

    @Test
    fun `mapToDomain with unexpected regular state throws Exception`() {
        // GIVEN a database Regular timer with an invalid state type
        val dbTimer = DbTimer(
            id = Uuid.random(),
            name = "Regular",
            type = DbTimerType.REGULAR,
            createdAt = now.toString(),
            linkedTask = null,
            state = DbTimerState(DbTimerStateType.POMODORO_FOCUS, now.toString(), later.toString()),
            pomodoroSettings = null,
            focusDividendSettings = null
        )

        // WHEN we attempt to map it to the domain, THEN it throws an exception
        assertFailsWith<IllegalArgumentException> {
            mapper.mapToDomain(dbTimer)
        }
    }

    @Test
    fun `mapToDomain with unexpected focus dividend state throws Exception`() {
        // GIVEN a database Focus Dividend timer with an invalid state type
        val dbTimer = DbTimer(
            id = Uuid.random(),
            name = "Focus",
            type = DbTimerType.FOCUS_DIVIDEND,
            createdAt = now.toString(),
            linkedTask = null,
            state = DbTimerState(DbTimerStateType.POMODORO_FOCUS, now.toString(), later.toString()),
            pomodoroSettings = null,
            focusDividendSettings = DbFocusDividendSettings(1.0, 0)
        )

        // WHEN we attempt to map it to the domain, THEN it throws an exception
        assertFailsWith<IllegalArgumentException> {
            mapper.mapToDomain(dbTimer)
        }
    }

    @Test
    fun `mapSort with all types returns correct DbTimerSorts`() {
        // WHEN we map all domain sort types
        val dbCreationSort = mapper.mapSort(TimerSort.ByCreationTimeDesc)
        val dbNameSort = mapper.mapSort(TimerSort.ByNameAsc)

        // THEN they are correctly translated to database-level sort types
        assertEquals(DbTimerSort.BY_CREATION_DESC, dbCreationSort)
        assertEquals(DbTimerSort.BY_NAME_ASC, dbNameSort)
    }

    // Helpers

    private fun createPomodoroTimer(
        state: PomodoroTimerState = PomodoroTimerState.Focus(now, later)
    ) = PomodoroTimer(
        id = TimerId(Uuid.random()),
        name = TimerName.createOrThrow("Pomodoro"),
        creationTime = now,
        state = state,
        linkedTask = null,
        settings = PomodoroTimerSettings()
    )

    private fun createDbPomodoroTimer() = DbTimer(
        id = Uuid.random(),
        name = "Pomodoro",
        type = DbTimerType.POMODORO,
        createdAt = now.toString(),
        linkedTask = null,
        state = DbTimerState(DbTimerStateType.POMODORO_FOCUS, now.toString(), later.toString()),
        pomodoroSettings = DbPomodoroSettings(
            focusDuration = 25.minutes.inWholeMilliseconds,
            shortBreakDuration = 5.minutes.inWholeMilliseconds,
            longBreakDuration = 15.minutes.inWholeMilliseconds,
            longBreakPer = 4,
            isLongBreakEnabled = true,
            isPreparationEnabled = false,
            preparationDuration = 10.seconds.inWholeMilliseconds,
            requiresConfirmation = false,
            confirmationTimeout = 30.seconds.inWholeMilliseconds
        ),
        focusDividendSettings = null
    )

    private fun createTimerWithState(state: cadento.timers.domain.TimerState): Timer {
        val id = TimerId(Uuid.random())
        val name = TimerName.createOrThrow("Test")
        return when (state) {
            is PomodoroTimerState -> PomodoroTimer(id, name, now, state, null, PomodoroTimerSettings())
            is RegularTimerState -> RegularTimer(id, name, now, state, null)
            is FocusDividendTimerState -> FocusDividendTimer(
                id, name, now, state, null, FocusDividendTimerSettings(), Duration.ZERO
            )
        }
    }
}
