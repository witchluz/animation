/*
 * Copyright © 2023 luz.lgbt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lgbt.luz.animation

import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * A cleanly (even while running) toggleable animation,
 * providing the animated value as a given number type [T].
 * @author Luz
 * @since 1.0.0
 */
class Animation<T : Number> : Animator() {
    private var isTurned = false
    private var progressForth = 0.0
    private var progressBack = 0.0

    private fun toggle(triggered: Boolean, durationMs: Long) {
        if (triggered xor this.isTurned) {
            this.start(durationMs)
            this.isTurned = triggered
        }
    }

    /**
     * Toggles the animation and returns the animated value as [T].
     * @param start First value of the animation.
     * @param end Last value of the animation.
     * @param durationMs The animation's duration in milliseconds.
     * @param triggered Is the animation triggered?
     * @param speedCurve The curve the animation's speed is eased with.
     */
    fun getValue(
        start: T,
        end: T,
        durationMs: Long,
        triggered: Boolean = true,
        speedCurve: SpeedCurve = SpeedCurve.LINEAR,
    ): T {
        require(durationMs >= 0) { "Negative duration" }

        this.toggle(triggered, durationMs)

        val startDouble = start.toDouble()
        val endDouble = end.toDouble()
        val animatedValue = startDouble +
                if (triggered) {
                    (this.progressForth +
                            ((endDouble - startDouble - this.progressForth) * this.getProgress(speedCurve)))
                        .also { this.progressBack = it }
                } else {
                    (this.progressBack -
                            this.progressBack * this.getProgress(speedCurve))
                        .also { this.progressForth = it }
                }

        @Suppress("UNCHECKED_CAST")
        return when (start) {
            is Byte -> animatedValue.roundToInt().toByte() as T
            is Short -> animatedValue.roundToInt().toShort() as T
            is Int -> animatedValue.roundToInt() as T
            is Long -> animatedValue.roundToLong() as T
            is Float -> animatedValue.toFloat() as T
            is Double -> animatedValue as T
            else -> throw IllegalArgumentException("Unsupported number type")
        }
    }
}
