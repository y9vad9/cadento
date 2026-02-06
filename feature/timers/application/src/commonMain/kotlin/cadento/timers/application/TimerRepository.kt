package cadento.timers.application

import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.RegularTimer
import kotlinx.coroutines.flow.Flow

interface PomodoroTimerRepository {
    fun observePomodoroTimer(id: TimerId): Flow<Result<PomodoroTimer?>>
    suspend fun getPomodoroTimer(id: TimerId): Result<PomodoroTimer?>
}

interface RegularTimerRepository {
    fun observeRegularTimer(id: TimerId): Flow<Result<RegularTimer?>>
    suspend fun getRegularTimer(id: TimerId): Result<RegularTimer?>
}

interface FocusDividendTimerRepository {
    fun observeFocusDividendTimer(id: TimerId): Flow<Result<FocusDividendTimer?>>
    suspend fun getFocusDividendTimer(id: TimerId): Result<FocusDividendTimer?>
}

interface TimerRepository : 
    PomodoroTimerRepository, 
    RegularTimerRepository, 
    FocusDividendTimerRepository {
    
    suspend fun createTimer(timer: Timer): Result<Unit>
    suspend fun updateTimer(timer: Timer): Result<Timer?>
    suspend fun deleteTimer(id: TimerId): Result<DeleteTimerResult>
    
    fun observeTimers(filter: TimerFilter, sort: TimerSort): Flow<Result<List<Timer>>>

    suspend fun getTimer(id: TimerId): Result<Timer?>
}

sealed interface DeleteTimerResult {
    data object Success : DeleteTimerResult
    data object TimerNotFound : DeleteTimerResult
}

data class TimerFilter(
    val nameContains: String? = null,
)

sealed interface TimerSort {
    data object ByCreationTimeDesc : TimerSort
    data object ByNameAsc : TimerSort
}

