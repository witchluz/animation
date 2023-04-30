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

import net.minecraft.util.MathHelper.sin // Alternatively: import kotlin.math.sin
import java.lang.Math.toRadians
import java.time.Duration
import java.time.Instant
import java.time.Instant.now

/**
 * An animator, providing basic functionality for [SpeedCurve]-driven animating.
 * @author Luz
 * @since 1.0.0
 */
open class Animator {
    private var startTime = Instant.MIN
    private var duration = Duration.ZERO

    /** Starts the animation for a given [durationMs] (in milliseconds). */
    fun start(durationMs: Long) {
        require(durationMs >= 0) { "Negative duration" }

        this.startTime = now()
        this.duration = Duration.ofMillis(durationMs)
    }

    /**
     * Returns the animation's current progress,
     * eased by a [speedCurve], as a floating point from 0.0 to 1.0.
     */
    fun getProgress(speedCurve: SpeedCurve = SpeedCurve.LINEAR): Float {
        if (isDone()) return 1.0f

        val linearProgress: Double =
            (Duration.between(this.startTime, now()).toMillis() / this.duration.toMillis().toDouble())

        return when (speedCurve) {
            SpeedCurve.EASE_IN -> 1.0f + sin(toRadians(-90.0 + linearProgress * 90.0).toFloat())
            SpeedCurve.EASE_OUT -> sin(toRadians(linearProgress * 90.0).toFloat())
            SpeedCurve.EASE_IN_OUT -> (1.0f + sin(toRadians(-90.0 + linearProgress * 180.0).toFloat())) * 0.5f
            else -> linearProgress.toFloat()
        }
    }

    /** Is the animation done? */
    fun isDone(): Boolean = now() >= this.startTime.plusMillis(this.duration.toMillis())
}
