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
import org.koin.core.annotation.Singleton
import kotlin.time.Clock

@Module
@Suppress("detekt.TooManyFunctions")
class TimerFeatureModule {

    @Singleton
    fun createPomodoroTimerUseCase(repository: TimerRepository, clock: Clock, idProvider: TimerIdProvider) = 
        CreatePomodoroTimerUseCase(repository, clock, idProvider)

    @Singleton
    fun getPomodoroTimerUseCase(repository: TimerRepository) = 
        GetPomodoroTimerUseCase(repository)

    @Singleton
    fun startPomodoroTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StartPomodoroTimerUseCase(repository, clock)

    @Singleton
    fun stopPomodoroTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StopPomodoroTimerUseCase(repository, clock)

    @Singleton
    fun createRegularTimerUseCase(repository: TimerRepository, clock: Clock, idProvider: TimerIdProvider) = 
        CreateRegularTimerUseCase(repository, clock, idProvider)

    @Singleton
    fun getRegularTimerUseCase(repository: TimerRepository) = 
        GetRegularTimerUseCase(repository)

    @Singleton
    fun startRegularTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StartRegularTimerUseCase(repository, clock)

    @Singleton
    fun stopRegularTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StopRegularTimerUseCase(repository, clock)

    @Singleton
    fun createFocusDividendTimerUseCase(repository: TimerRepository, clock: Clock, idProvider: TimerIdProvider) = 
        CreateFocusDividendTimerUseCase(repository, clock, idProvider)

    @Singleton
    fun getFocusDividendTimerUseCase(repository: TimerRepository) = 
        GetFocusDividendTimerUseCase(repository)

    @Singleton
    fun startFocusDividendTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StartFocusDividendTimerUseCase(repository, clock)

    @Singleton
    fun stopFocusDividendTimerUseCase(repository: TimerRepository, clock: Clock) = 
        StopFocusDividendTimerUseCase(repository, clock)

    @Singleton
    fun getTimersUseCase(repository: TimerRepository) = 
        GetTimersUseCase(repository)

    @Singleton
    fun deleteTimerUseCase(repository: TimerRepository) = 
        DeleteTimerUseCase(repository)

    @Singleton
    fun linkTaskToTimerUseCase(repository: TimerRepository) = 
        LinkTaskToTimerUseCase(repository)

    @Singleton
    fun unlinkTaskFromTimerUseCase(repository: TimerRepository) = 
        UnlinkTaskFromTimerUseCase(repository)

    @Singleton
    fun timerIdProvider(): TimerIdProvider = UuidTimerIdProvider()

    @Singleton
    fun timerRepository(sqlDriver: SqlDriver): TimerRepository {
        return TimerRepositoryImpl(
            databaseSource = TimersDatabaseSource(
                database = TimerDatabase(sqlDriver)
            ),
            dbTimerMapper = DbTimerMapper()
        )
    }
}
