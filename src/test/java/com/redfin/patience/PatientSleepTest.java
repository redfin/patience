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
import java.time.Instant;

final class PatientSleepTest implements NonInstantiableContract<PatientSleep> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Class<PatientSleep> getClassObject_NonInstantiableContract() {
        return PatientSleep.class;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testSleepForThrowsExceptionForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientSleep.sleepFor(null));
    }

    @Test
    void testSleepForThrowsExceptionForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientSleep.sleepFor(Duration.ofMinutes(-1)));
    }

    @Test
    void testSleepForCanBeCalledWithZeroDuration() {
        PatientSleep.sleepFor(Duration.ZERO);
    }

    @Test
    void testSleepForSleepsForTheGivenDuration() {
        Duration duration = Duration.ofMillis(200);
        Instant start = Instant.now();
        PatientSleep.sleepFor(duration);
        Instant end = Instant.now();
        Assertions.assertTrue(Duration.between(start, end).compareTo(duration) > 0,
                              "SleepFor should sleep for the given duration");
    }

    @Test
    void testSleepForThrowsExceptionOnOverflow() {
        Duration duration = Duration.ofSeconds(Long.MAX_VALUE);
        Assertions.assertThrows(ArithmeticException.class,
                                () -> PatientSleep.sleepFor(duration));
    }
}
