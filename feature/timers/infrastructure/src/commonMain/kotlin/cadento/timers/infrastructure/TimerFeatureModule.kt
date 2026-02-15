package cadento.timers.infrastructure

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
import cadento.timers.database.TimersDatabaseSource
import cadento.timers.sqldelight.TimerDatabase
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.time.Clock

@Module
@Suppress("detekt.TooManyFunctions")
class TimerFeatureModule {

    @Single
    fun createPomodoroTimerUseCase(repository: TimerRepository, clock: Clock, idProvider: TimerIdProvider) = 
        CreatePomodoroTimerUseCase(repository, clock, idProvider)

    @Single
    fun getPomodoroTimerUseCase(repository: TimerRepository) = 
        GetPomodoroTimerUseCase(repository)

    @Single
    fun startPomodoroTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StartPomodoroTimerUseCase(repository, clock)

    @Single
    fun stopPomodoroTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StopPomodoroTimerUseCase(repository, clock)

    @Single
    fun createRegularTimerUseCase(repository: TimerRepository, clock: Clock, idProvider: TimerIdProvider) = 
        CreateRegularTimerUseCase(repository, clock, idProvider)

    @Single
    fun getRegularTimerUseCase(repository: TimerRepository) = 
        GetRegularTimerUseCase(repository)

    @Single
    fun startRegularTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StartRegularTimerUseCase(repository, clock)

    @Single
    fun stopRegularTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StopRegularTimerUseCase(repository, clock)

    @Single
    fun createFocusDividendTimerUseCase(repository: TimerRepository, clock: Clock, idProvider: TimerIdProvider) = 
        CreateFocusDividendTimerUseCase(repository, clock, idProvider)

    @Single
    fun getFocusDividendTimerUseCase(repository: TimerRepository) = 
        GetFocusDividendTimerUseCase(repository)

    @Single
    fun startFocusDividendTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StartFocusDividendTimerUseCase(repository, clock)

    @Single
    fun stopFocusDividendTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StopFocusDividendTimerUseCase(repository, clock)

    @Single
    fun getTimersUseCase(repository: TimerRepository) = 
        GetTimersUseCase(repository)

    @Single
    fun deleteTimerUseCase(repository: TimerRepository) = 
        DeleteTimerUseCase(repository)

    @Single
    fun linkTaskToTimerUseCase(repository: TimerRepository) = 
        LinkTaskToTimerUseCase(repository)

    @Single
    fun unlinkTaskFromTimerUseCase(repository: TimerRepository) = 
        UnlinkTaskFromTimerUseCase(repository)

    @Single
    fun timerIdProvider(): TimerIdProvider = UuidTimerIdProvider()

    @Single
    fun timerRepository(sqlDriver: SqlDriver): TimerRepository {
        return TimerRepositoryImpl(
            databaseSource = TimersDatabaseSource(
                database = TimerDatabase(sqlDriver)
            ),
            dbTimerMapper = DbTimerMapper()
        )
    }
}
