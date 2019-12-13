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

package com.redfin.patience.delays;

import com.redfin.patience.PatientDelaySupplierFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

/**
 * An implementation of {@link PatientDelaySupplierFactory} that creates a
 * {@link Supplier} of {@link Duration}s that increase exponentially
 * for each call to {@link Supplier#get()}.
 */
public final class ExponentialPatientDelaySupplierFactory
        implements PatientDelaySupplierFactory {

    private final int base;
    private final Duration initialDelay;

    /**
     * Create a new {@link ExponentialPatientDelaySupplierFactory} instance with the
     * given base and initial delay. If the base is set to one that is the same as
     * using a fixed delay retry handler with the given initial delay.
     *
     * @param base         the int base of the power function.
     *                     Must be greater than 0.
     * @param initialDelay the {@link Duration} initial delay duration.
     *                     May not be null, negative, or zero.
     *
     * @throws IllegalArgumentException if delayDuration is null or negative.
     * @throws IllegalArgumentException if initialDelay is null or if either argument is less than
     *                                  or equal to zero.
     */
    public ExponentialPatientDelaySupplierFactory(int base,
                                                  Duration initialDelay) {
        this.base = validate().withMessage("Cannot use a base less than 1.")
                              .that(base)
                              .isAtLeast(1);
        this.initialDelay = validate().withMessage("Cannot use a null, negative, or zero initial delay Duration.")
                                      .that(initialDelay)
                                      .isStrictlyPositive();
    }

    @Override
    public Supplier<Duration> create() {
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
