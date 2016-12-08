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

package com.redfin.patience;

import com.redfin.patience.retrystrategies.ExponentialDelayPatientRetryStrategy;
import com.redfin.patience.retrystrategies.FixedDelayPatientRetryStrategy;
import com.redfin.validity.Validity;

import java.time.Duration;

/**
 * A non-instantiable class that wraps creation
 * of the {@link PatientRetryStrategy} implementations in
 * the Patience library.
 */
public final class PatientRetryStrategies {

    /**
     * @param delay the {@link Duration} fixed delay between each retry attempt.
     *              May not be null or negative.
     *
     * @return a new {@link FixedDelayPatientRetryStrategy} instance
     * with the given delay.
     *
     * @throws IllegalArgumentException if delay is null or negative.
     */
    public static PatientRetryStrategy withFixedDelay(Duration delay) {
        return new FixedDelayPatientRetryStrategy(Validity.require().that(delay).isGreaterThanOrEqualTo(Duration.ZERO));
    }

    /**
     * @param base         the base for the exponential calculation.
     *                     Must be greater than zero.
     * @param initialDelay the initial {@link Duration} between executions.
     *                     May not be null, zero, or negative.
     *
     * @return a new {@link ExponentialDelayPatientRetryStrategy} instance with the given
     * initial delay and base.
     *
     * @throws IllegalArgumentException if initialDuration is null or either are less than
     *                                  or equal to zero.
     */
    public static PatientRetryStrategy withExponentialDelay(int base, Duration initialDelay) {
        return new ExponentialDelayPatientRetryStrategy(Validity.require().that(base).isStrictlyPositive(),
                                                        Validity.require().that(initialDelay).isStrictlyPositive());
    }

    /*
     * Ensure this class is non-instantiable.
     */

    private PatientRetryStrategies() {
        throw new AssertionError("Cannot instantiate this class");
    }
}
