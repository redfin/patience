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

final class PatientRetryStrategiesTest implements NonInstantiableContract<PatientRetryStrategies> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Class<PatientRetryStrategies> getClassObject_NonInstantiableContract() {
        return PatientRetryStrategies.class;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testWithFixedDelayReturnsNonNullInstance() {
        Assertions.assertNotNull(PatientRetryStrategies.withFixedDelay(Duration.ofMillis(400)),
                                 "Should be able to get a fixed delay strategy with a valid duration");
    }

    @Test
    void testWithFixedDelayThrowsExceptionForNullDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryStrategies.withFixedDelay(null));
    }

    @Test
    void testWithFixedDelayThrowsExceptionForNegativeDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryStrategies.withFixedDelay(Duration.ofMinutes(-1)));
    }

    @Test
    void testWithFixedDelayDoesNotThrowForZeroDelay() {
        Assertions.assertNotNull(PatientRetryStrategies.withFixedDelay(Duration.ZERO),
                                 "Should be able to get a fixed delay strategy with a zero duration");
    }

    @Test
    void testWithExponentialDelayReturnsNonNullInstance() {
        Assertions.assertNotNull(PatientRetryStrategies.withFixedDelay(Duration.ofMillis(400)),
                                 "Should be able to get a fixed delay strategy with a valid duration");
    }

    @Test
    void testWithExponentialDelayThrowsExceptionForNullDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryStrategies.withExponentialDelay(2, null));
    }

    @Test
    void testWithExponentialDelayThrowsExceptionForNegativeDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryStrategies.withExponentialDelay(2, Duration.ofMinutes(-1)));
    }

    @Test
    void testWithExponentialDelayThrowsExceptionForZeroDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryStrategies.withExponentialDelay(2, Duration.ZERO));
    }

    @Test
    void testWithExponentialDelayThrowsExceptionForZeroBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryStrategies.withExponentialDelay(0, Duration.ofMillis(200)));
    }

    @Test
    void testWithExponentialDelayThrowsExceptionForNegativeBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientRetryStrategies.withExponentialDelay(-1, Duration.ofMillis(200)));
    }
}
