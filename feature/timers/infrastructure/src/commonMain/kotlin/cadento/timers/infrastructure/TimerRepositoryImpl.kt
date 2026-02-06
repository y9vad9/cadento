package cadento.timers.infrastructure

import cadento.coroutines.suspendRunCatching
import cadento.timers.application.DeleteTimerResult
import cadento.timers.application.TimerFilter
import cadento.timers.application.TimerRepository
import cadento.timers.application.TimerSort
import cadento.timers.database.TimersDatabaseSource
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

@Suppress("detekt.TooManyFunctions")
class TimerRepositoryImpl(
    private val databaseSource: TimersDatabaseSource,
    private val dbTimerMapper: DbTimerMapper,
) : TimerRepository {

    override suspend fun createTimer(timer: Timer): Result<Unit> = suspendRunCatching {
        databaseSource.insertTimer(dbTimerMapper.mapToDb(timer))
    }

    override suspend fun updateTimer(timer: Timer): Result<Timer?> = suspendRunCatching {
        databaseSource.updateTimer(dbTimerMapper.mapToDb(timer))?.let { dbTimerMapper.mapToDomain(it) }
    }

    override suspend fun deleteTimer(id: TimerId): Result<DeleteTimerResult> = suspendRunCatching {
        val wasDeleted = databaseSource.deleteTimer(id.value)
        if (wasDeleted) DeleteTimerResult.Success else DeleteTimerResult.TimerNotFound
    }

    override fun observeTimers(filter: TimerFilter, sort: TimerSort): Flow<Result<List<Timer>>> {
        return databaseSource.observeTimers(
            nameContains = filter.nameContains,
            sort = dbTimerMapper.mapSort(sort).name
        ).map { dbTimers ->
            runCatching {
                dbTimers.map { dbTimerMapper.mapToDomain(it) }
            }
        }
    }

    override fun observePomodoroTimer(id: TimerId): Flow<Result<PomodoroTimer?>> {
        return databaseSource.observeTimer(id.value).map { dbTimer ->
            runCatching {
                dbTimer?.let { dbTimerMapper.mapToDomain(it) as? PomodoroTimer }
            }
        }
    }

    override fun observeRegularTimer(id: TimerId): Flow<Result<RegularTimer?>> {
        return databaseSource.observeTimer(id.value).map { dbTimer ->
            runCatching {
                dbTimer?.let { dbTimerMapper.mapToDomain(it) as? RegularTimer }
            }
        }
    }

    override fun observeFocusDividendTimer(id: TimerId): Flow<Result<FocusDividendTimer?>> {
        return databaseSource.observeTimer(id.value).map { dbTimer ->
            runCatching {
                dbTimer?.let { dbTimerMapper.mapToDomain(it) as? FocusDividendTimer }
            }
        }
    }

    override suspend fun getPomodoroTimer(id: TimerId): Result<PomodoroTimer?> = suspendRunCatching {
        databaseSource.observeTimer(id.value).firstOrNull()
            ?.let { dbTimerMapper.mapToDomain(it) as? PomodoroTimer }
    }

    override suspend fun getRegularTimer(id: TimerId): Result<RegularTimer?> = suspendRunCatching {
        databaseSource.observeTimer(id.value).firstOrNull()
            ?.let { dbTimerMapper.mapToDomain(it) as? RegularTimer }
    }

    override suspend fun getFocusDividendTimer(id: TimerId): Result<FocusDividendTimer?> = suspendRunCatching {
        databaseSource.observeTimer(id.value).firstOrNull()
            ?.let { dbTimerMapper.mapToDomain(it) as? FocusDividendTimer }
    }

    override suspend fun getTimer(id: TimerId): Result<Timer?> = suspendRunCatching {
        databaseSource.observeTimer(id.value).firstOrNull()?.let { dbTimerMapper.mapToDomain(it) }
    }
}
