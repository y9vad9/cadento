package cadento.timers.domain

import kotlin.jvm.JvmInline

/**
 * Represents the coefficient used to calculate the earned time in Focus Dividend timers.
 *
 * @property value The multiplier for focus time.
 */
@JvmInline
value class EarningCoefficient private constructor(val value: Double) {
    companion object {
        const val MIN_VALUE = 0.01
        const val MAX_VALUE = 10.0

        val DEFAULT = EarningCoefficient(0.5)

        /**
         * Creates an [EarningCoefficient] from a double value.
         *
         * @param value The double value to create the coefficient from.
         * @return A [CreationResult] indicating success or the reason for failure.
         */
        fun create(value: Double): CreationResult {
            return when {
                value < MIN_VALUE -> CreationResult.TooSmall
                value > MAX_VALUE -> CreationResult.TooLarge
                else -> CreationResult.Success(EarningCoefficient(value))
            }
        }

        /**
         * Creates an [EarningCoefficient] or throws if the value is invalid.
         */
        fun createOrThrow(value: Double): EarningCoefficient {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.coefficient
                is CreationResult.TooSmall -> throw IllegalArgumentException("Coefficient must be at least $MIN_VALUE")
                is CreationResult.TooLarge -> throw IllegalArgumentException("Coefficient cannot exceed $MAX_VALUE")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val coefficient: EarningCoefficient) : CreationResult
        data object TooSmall : CreationResult
        data object TooLarge : CreationResult
    }
}
