package cadento.timers.infrastructure.unittest

import app.cash.sqldelight.db.SqlDriver
import cadento.timers.application.DeleteTimerUseCase
import cadento.timers.application.GetTimersUseCase
import cadento.timers.application.LinkTaskToTimerUseCase
import cadento.timers.application.TimerIdProvider
import cadento.timers.application.TimerRepository
import cadento.timers.application.UnlinkTaskFromTimerUseCase
import cadento.timers.application.focusdividend.CreateFocusDividendTimerUseCase
import cadento.timers.application.focusdividend.GetFocusDividendTimerUseCase
import cadento.timers.application.focusdividend.StartFocusDividendTimerUseCase
import cadento.timers.application.focusdividend.StopFocusDividendTimerUseCase
import cadento.timers.application.pomodoro.CreatePomodoroTimerUseCase
import cadento.timers.application.pomodoro.GetPomodoroTimerUseCase
import cadento.timers.application.pomodoro.StartPomodoroTimerUseCase
import cadento.timers.application.pomodoro.StopPomodoroTimerUseCase
import cadento.timers.application.regular.CreateRegularTimerUseCase
import cadento.timers.application.regular.GetRegularTimerUseCase
import cadento.timers.application.regular.StartRegularTimerUseCase
import cadento.timers.application.regular.StopRegularTimerUseCase
import cadento.timers.infrastructure.TimerFeatureModule
import io.mockk.mockk
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.time.Clock

class TimerFeatureModuleTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `module provides all required singletons`() {
        // GIVEN a Koin context with required external dependencies mocked
        val sqlDriver: SqlDriver = mockk()
        val clock: Clock = mockk()
        
        val koin = startKoin {
            modules(
                module {
                    single { sqlDriver }
                    single { clock }
                },
                TimerFeatureModule().module
            )
        }.koin

        // WHEN we request all provided singletons
        // THEN they are correctly initialized and non-null
        assertNotNull(koin.get<TimerRepository>())
        assertNotNull(koin.get<TimerIdProvider>())
        assertNotNull(koin.get<CreatePomodoroTimerUseCase>())
        assertNotNull(koin.get<GetPomodoroTimerUseCase>())
        assertNotNull(koin.get<StartPomodoroTimerUseCase>())
        assertNotNull(koin.get<StopPomodoroTimerUseCase>())
        assertNotNull(koin.get<CreateRegularTimerUseCase>())
        assertNotNull(koin.get<GetRegularTimerUseCase>())
        assertNotNull(koin.get<StartRegularTimerUseCase>())
        assertNotNull(koin.get<StopRegularTimerUseCase>())
        assertNotNull(koin.get<CreateFocusDividendTimerUseCase>())
        assertNotNull(koin.get<GetFocusDividendTimerUseCase>())
        assertNotNull(koin.get<StartFocusDividendTimerUseCase>())
        assertNotNull(koin.get<StopFocusDividendTimerUseCase>())
        assertNotNull(koin.get<GetTimersUseCase>())
        assertNotNull(koin.get<DeleteTimerUseCase>())
        assertNotNull(koin.get<LinkTaskToTimerUseCase>())
        assertNotNull(koin.get<UnlinkTaskFromTimerUseCase>())
    }
}
