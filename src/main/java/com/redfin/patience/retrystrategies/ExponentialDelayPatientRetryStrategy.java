/*
 * Copyright: (c) 2016 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.patience.retrystrategies;

import com.redfin.validity.Validity;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * A concrete implementation of the {@link AbstractDelayPatientRetryStrategy} that calculates
 * an exponential back off based on the given initial duration and base so that each attempt
 * will result in a longer wait until the next attempt.
 */
public class ExponentialDelayPatientRetryStrategy extends AbstractDelayPatientRetryStrategy {

    private final int base;
    private final Duration initialDelay;

    /**
     * Create a new {@link ExponentialDelayPatientRetryStrategy} with the given
     * base and initial duration.
     *
     * @param base         the base for the exponential calculation.
     *                     Must be greater than zero.
     * @param initialDelay the initial {@link Duration} between executions.
     *                     May not be null, zero, or negative.
     *
     * @throws IllegalArgumentException if initialDuration is null or either are less than
     *                                  or equal to zero.
     */
    public ExponentialDelayPatientRetryStrategy(int base, Duration initialDelay) {
        this.base = Validity.require().that(base).isStrictlyPositive();
        this.initialDelay = Validity.require().that(initialDelay).isStrictlyPositive();
    }

    @Override
    protected Supplier<Duration> getDelayDurations() {
        return new Supplier<Duration>() {

            private int currentCount = 0;

            @Override
            public Duration get() {
                Duration nextDuration = initialDelay.multipliedBy((int) Math.pow(base, currentCount));
                currentCount++;
                return nextDuration;
            }
        };
    }
}
