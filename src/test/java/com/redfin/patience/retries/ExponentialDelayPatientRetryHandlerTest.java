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

package com.redfin.patience.retries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Supplier;

final class ExponentialDelayPatientRetryHandlerTest
 implements PatientRetryHandlerContract<ExponentialDelayPatientRetryHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final int POSITIVE_BASE = 1;
    private static final int NEGATIVE_BASE = -1;
    private static final Duration POSITIVE_DURATION = Duration.ofMillis(100);
    private static final Duration NEGATIVE_DURATION = Duration.ofMillis(-100);
    private static final Duration LONGER_DURATION = Duration.ofSeconds(1);

    private ExponentialDelayPatientRetryHandler getInstance(int base,
                                                            Duration initialDelay) {
        return new ExponentialDelayPatientRetryHandler(base, initialDelay);
    }

    @Override
    public ExponentialDelayPatientRetryHandler getInstance() {
        return getInstance(POSITIVE_BASE, POSITIVE_DURATION);
    }

    @Override
    public Duration getMultipleExecutionDuration() {
        return LONGER_DURATION;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiate() {
        Assertions.assertNotNull(getInstance(POSITIVE_BASE, POSITIVE_DURATION),
                                 "Should be able to instantiate an ExponentialDelayPatientRetryHandler.");
    }

    @Test
    void testThrowsForZeroBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(0, POSITIVE_DURATION),
                                "Should throw an exception for a base of 0.");
    }

    @Test
    void testThrowsForNegativeBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(NEGATIVE_BASE, POSITIVE_DURATION),
                                "Should throw an exception for a negative base.");
    }

    @Test
    void testThrowsForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_BASE, null),
                                "Should throw an exception for a null duration.");
    }

    @Test
    void testThrowsForZeroDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_BASE, Duration.ZERO),
                                "Should throw an exception for a zero duration.");
    }

    @Test
    void testThrowsForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_BASE, NEGATIVE_DURATION),
                                "Should throw an exception for a negative duration.");
    }

    @Test
    void testReturnsExpectedDurationsFromSupplier_base_1() {
        Duration initialDelay = POSITIVE_DURATION;
        // Base of 1 is the same as fixed delay
        Duration[] durations = {initialDelay,  // x1 - first
                                initialDelay,  // x1 - second
                                initialDelay}; // x1 - third
        Supplier<Duration> supplier = getInstance(1, initialDelay).getRetryHandlerDurationSupplier();
        Assertions.assertAll(Arrays.stream(durations)
                                   .map(duration ->
                                                () -> Assertions.assertEquals(duration,
                                                                              supplier.get(),
                                                                              "Expected the duration supplier to return the expected duration.")
                                       ));
    }

    @Test
    void testReturnsExpectedDurationsFromSupplier_base_2() {
        Duration initialDelay = POSITIVE_DURATION;
        Duration[] durations = {initialDelay,                     // x1 - first
                                initialDelay.plus(initialDelay),  // x2 - second
                                initialDelay.plus(initialDelay)   // x4 - third
                                            .plus(initialDelay)
                                            .plus(initialDelay)};
        Supplier<Duration> supplier = getInstance(2, initialDelay).getRetryHandlerDurationSupplier();
        Assertions.assertAll(Arrays.stream(durations)
                                   .map(duration ->
                                                () -> Assertions.assertEquals(duration,
                                                                              supplier.get(),
                                                                              "Expected the duration supplier to return the expected duration.")
                                       ));
    }
}
