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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

final class ExponentialDelayPatientRetryStrategyTest implements PatientRetryStrategyContract<ExponentialDelayPatientRetryStrategy> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public ExponentialDelayPatientRetryStrategy getInstance() {
        return new ExponentialDelayPatientRetryStrategy(2, Duration.ofMillis(100));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testExponentialDelayThrowsForNegativeBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new ExponentialDelayPatientRetryStrategy(-1, Duration.ofMillis(100)));
    }

    @Test
    void testExponentialDelayThrowsForZeroBase() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new ExponentialDelayPatientRetryStrategy(0, Duration.ofMillis(100)));
    }

    @Test
    void testExponentialDelayThrowsForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new ExponentialDelayPatientRetryStrategy(2, null));
    }

    @Test
    void testExponentialDelayThrowsForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new ExponentialDelayPatientRetryStrategy(2, Duration.ofMinutes(-1)));
    }

    @Test
    void testExponentialDelayThrowsForZeroDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new ExponentialDelayPatientRetryStrategy(2, Duration.ZERO));
    }

    @Test
    void testExponentialDelayReturnsExpectedDurations() {
        Duration initialDelay = Duration.ofMillis(100);
        int base = 2;
        Supplier<Duration> supplier = new ExponentialDelayPatientRetryStrategy(base, initialDelay).getDelayDurations();
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(initialDelay.multipliedBy((long) Math.pow(base, i)),
                                    supplier.get(),
                                    "An exponential retry should follow the expected pattern for supplied durations");
        }
    }
}
