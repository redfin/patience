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

final class FixedDelayPatientRetryStrategyTest implements PatientRetryStrategyContract<FixedDelayPatientRetryStrategy> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public FixedDelayPatientRetryStrategy getInstance() {
        return new FixedDelayPatientRetryStrategy(Duration.ofMillis(100));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testFixedDelayThrowsForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new FixedDelayPatientRetryStrategy(null));
    }

    @Test
    void testFixedDelayThrowsForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new FixedDelayPatientRetryStrategy(Duration.ofMinutes(-1)));
    }

    @Test
    void testFixedDelayDoesNotThrowForZeroDuration() {
        new FixedDelayPatientRetryStrategy(Duration.ZERO);
    }

    @Test
    void testFixedDelayRetryAlwaysReturnsGivenDuration() {
        Duration delay = Duration.ofMinutes(1);
        Supplier<Duration> supplier = new FixedDelayPatientRetryStrategy(delay).getDelayDurationsSupplier();
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(delay,
                                    supplier.get(),
                                    "A fixed delay retry strategy should return the same duration for each call to getDelayDuration");
        }
    }
}
