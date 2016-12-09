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

interface PatientFutureContract<T extends PatientFuture<String>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param timeout the given default timeout.
     *
     * @return an instance of the class under test that has
     * the given timeout set as the default.
     */
    T getInstanceWithDefaultTimeout(Duration timeout);

    /**
     * @return an instance of the class under test that will return
     * a non-null value when it's get method is called.
     */
    T getSuccessfulInstance();

    /**
     * @return an instance of the class under test that will not
     * return a valid value successfully.
     */
    T getUnsuccessfulInstance();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    default void testGetReturnsValueForSuccessful() {
        Assertions.assertNotNull(getSuccessfulInstance().get(),
                                 "A successful PatientFuture should return a value");
    }

    @Test
    default void testGetThrowsExceptionForUnsuccessful() {
        Assertions.assertThrows(PatientTimeoutException.class,
                                () -> getUnsuccessfulInstance().get());
    }

    @Test
    default void testGetThrowsExceptionForDefaultTimeoutOverflow() {
        Duration timeout = Duration.ofSeconds(Long.MAX_VALUE);
        Assertions.assertThrows(ArithmeticException.class,
                                () -> getInstanceWithDefaultTimeout(timeout).get());
    }

    @Test
    default void testGetWithTimeoutThrowsExceptionForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getSuccessfulInstance().get(null));
    }

    @Test
    default void testGetWithTimeoutThrowsExceptionForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getSuccessfulInstance().get(Duration.ofMinutes(-1)));
    }

    @Test
    default void testGetWithTimeoutSucceedsWithZeroDuration() {
        Assertions.assertNotNull(getSuccessfulInstance().get(Duration.ZERO),
                                 "Should be able to give a zero timeout duration to a patient future");
    }

    @Test
    default void testGetWithTimeoutReturnsValueForSuccessful() {
        Assertions.assertNotNull(getSuccessfulInstance().get(Duration.ofMillis(100)),
                                 "Should be able to give a zero timeout duration to a patient future");
    }

    @Test
    default void testGetWithTimeoutThrowsExceptionForUnsuccessful() {
        Assertions.assertThrows(PatientTimeoutException.class,
                                () -> getUnsuccessfulInstance().get(Duration.ofMillis(100)));
    }

    @Test
    default void testGetWithTimeoutThrowsExceptionForTimeoutOverflow() {
        Duration timeout = Duration.ofSeconds(Long.MAX_VALUE);
        Assertions.assertThrows(ArithmeticException.class,
                                () -> getSuccessfulInstance().get(timeout));
    }
}
