package cadento.timers.infrastructure

import cadento.timers.application.TimerIdProvider
import cadento.timers.domain.TimerId
import kotlin.uuid.Uuid

class UuidTimerIdProvider : TimerIdProvider {
    override fun nextId(): TimerId = TimerId(Uuid.random())
}
