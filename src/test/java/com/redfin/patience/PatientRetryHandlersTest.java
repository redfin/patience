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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

final class PatientRetryHandlersTest
 implements NonInstantiableContract<PatientRetryHandlers> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Class<PatientRetryHandlers> getClassObject_NonInstantiableContract() {
        return PatientRetryHandlers.class;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testReturnsNonNullForFixedDelayWithZeroDuration() {
        Assertions.assertNotNull(PatientRetryHandlers.fixedDelay(Duration.ZERO),
                                 "Should return a non-null fixed delay retry handler for zero duration.");
    }

    @Test
    void testReturnsNonNullForFixedDelayWithPositiveDuration() {
        Assertions.assertNotNull(PatientRetryHandlers.fixedDelay(Duration.ofMillis(500)),
                                 "Should return a non-null fixed delay retry handler for positive duration.");
    }

    @Test
    void testThrowsForFixedDelayWithNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryHandlers.fixedDelay(Duration.ofMillis(-500)),
                                 "Should throw for fixed delay retry handler with negative duration.");
    }

    @Test
    void testThrowsForFixedDelayWithNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryHandlers.fixedDelay(null),
                                "Should throw for fixed delay retry handler with null duration.");
    }

    @Test
    void testReturnsNonNullForExponentialDelayWithPositiveArguments() {
        Assertions.assertNotNull(PatientRetryHandlers.exponentialDelay(1, Duration.ofMillis(500)),
                                 "Should return a non-null exponential delay handler for positive arguments.");
    }

    @Test
    void testThrowsForExponentialDelayWithZeroBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryHandlers.exponentialDelay(0, Duration.ofMillis(500)),
                                "Should throw for exponential delay with a zero base.");
    }

    @Test
    void testThrowsForExponentialDelayWithZeroInitialDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryHandlers.exponentialDelay(1, Duration.ZERO),
                                "Should throw for exponential delay with a zero initial duration.");
    }

    @Test
    void testThrowsForExponentialDelayWithNegativeBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryHandlers.exponentialDelay(-1, Duration.ofMillis(500)),
                                "Should throw for exponential delay with a negative base.");
    }

    @Test
    void testThrowsForExponentialDelayWithNegativeInitialDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryHandlers.exponentialDelay(1, Duration.ofMillis(-500)),
                                "Should throw for exponential delay with a negative initial duration.");
    }

    @Test
    void testThrowsForExponentialDelayWithNullInitialDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryHandlers.exponentialDelay(1, null),
                                "Should throw for exponential delay with a null initial duration.");
    }
}
