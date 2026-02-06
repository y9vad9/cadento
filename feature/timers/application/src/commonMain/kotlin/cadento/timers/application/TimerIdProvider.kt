package cadento.timers.application

import cadento.timers.domain.TimerId

interface TimerIdProvider {
    fun nextId(): TimerId
}
