package cadento.timers.domain

/**
 * Settings for a Focus Dividend timer.
 *
 * @property earningCoefficient The multiplier applied to focused time to calculate earned time.
 */
data class FocusDividendTimerSettings(
    val earningCoefficient: EarningCoefficient = EarningCoefficient.DEFAULT,
)
