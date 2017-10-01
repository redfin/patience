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
import java.util.function.Supplier;

final class FixedDelayPatientRetryHandlerTest
 implements PatientRetryHandlerContract<FixedDelayPatientRetryHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private FixedDelayPatientRetryHandler getInstance(Duration duration) {
        return new FixedDelayPatientRetryHandler(duration);
    }

    @Override
    public FixedDelayPatientRetryHandler getInstance() {
        return getInstance(Duration.ZERO);
    }

    @Override
    public Duration getMultipleExecutionDuration() {
        return Duration.ofMillis(200);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testReturnsNonNullForZeroDuration() {
        Assertions.assertNotNull(getInstance(Duration.ZERO),
                                 "Should be able to instantiate with a zero duration.");
    }

    @Test
    void testReturnsNonNullForPositiveDuration() {
        Assertions.assertNotNull(getInstance(Duration.ofMillis(500)),
                                 "Should be able to instantiate with a positive duration.");
    }

    @Test
    void testThrowsForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(null),
                                 "Should throw for a null duration.");
    }

    @Test
    void testThrowsForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ofMillis(-500)),
                                "Should throw for a negative duration.");

    }

    @Test
    void testRepeatedGetCallsToDurationSupplierReturnSameDuration() {
        Duration duration = Duration.ofMinutes(1);
        Supplier<Duration> supplier = getInstance(duration).getRetryHandlerDurationSupplier();
        String message = "Should return the same duration from the supplier each time";
        Assertions.assertAll(() -> Assertions.assertEquals(duration,
                                                           supplier.get(),
                                                           message),
                             () -> Assertions.assertEquals(duration,
                                                           supplier.get(),
                                                           message),
                             () -> Assertions.assertEquals(duration,
                                                           supplier.get(),
                                                           message));
    }
}
