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

import com.redfin.patience.retries.ExponentialDelayPatientRetryHandler;
import com.redfin.patience.retries.FixedDelayPatientRetryHandler;

import java.time.Duration;

import static com.redfin.validity.Validity.*;

/**
 * A static, non-instantiable, class for obtaining instances of different
 * implementations of the {@link PatientRetryHandler} interface.
 */
public final class PatientRetryHandlers {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * Make sure that the static class cannot be instantiated
     */

    private PatientRetryHandlers() {
        throw new AssertionError("Cannot instantiate PatientRetryHandlers.");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Static Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param delayDuration the {@link Duration} to wait between execution attempts.
     *                      May not be null or negative.
     *
     * @return a new {@link FixedDelayPatientRetryHandler} instance with the given delay duration.
     *
     * @throws IllegalArgumentException if delayDuration is null or negative.
     */
    public static PatientRetryHandler fixedDelay(Duration delayDuration) {
        validate().that(delayDuration).isAtLeast(Duration.ZERO);
        return new FixedDelayPatientRetryHandler(delayDuration);
    }

    /**
     * @param base            the int base for the {@link ExponentialDelayPatientRetryHandler}.
     *                        A value of 1 is the same as a {@link FixedDelayPatientRetryHandler} with
     *                        the given initialDuration.
     *                        May not be less than 1.
     * @param initialDuration the {@link Duration} that will be used and increase exponentially
     *                        between retries.
     *                        May not be null, zero, or negative.
     *
     * @return a new {@link ExponentialDelayPatientRetryHandler} instance with the given
     * base and initial duration values.
     *
     * @throws IllegalArgumentException if initialDuration is null or if either
     *                                  argument is zero or negative.
     */
    public static PatientRetryHandler exponentialDelay(int base,
                                                       Duration initialDuration) {
        validate().that(base).isStrictlyPositive();
        validate().that(initialDuration).isStrictlyPositive();
        return new ExponentialDelayPatientRetryHandler(base, initialDuration);
    }
}
