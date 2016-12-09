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

import com.redfin.patience.PatientException;
import com.redfin.patience.PatientExecutionResult;
import com.redfin.patience.PatientRetryStrategy;
import com.redfin.patience.PatientTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

/**
 * A test contract for any implementation of a {@link PatientRetryStrategy}.
 *
 * @param <T> the type of the concrete implementation being tested.
 */
interface PatientRetryStrategyContract<T extends PatientRetryStrategy> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return an instance of the class type under test.
     */
    T getInstance();

    Supplier<PatientExecutionResult<String>> SUCCESS_RESULT_SUPPLIER = () -> PatientExecutionResult.of("hello");
    Supplier<PatientExecutionResult<String>> FAILURE_RESULT_SUPPLIER = PatientExecutionResult::empty;

    final class SuccessfulTrackingSupplier implements Supplier<PatientExecutionResult<String>> {

        private int counter = 0;

        private int getCounter() {
            return counter;
        }

        @Override
        public PatientExecutionResult<String> get() {
            counter++;
            return PatientExecutionResult.of("hello");
        }
    }

    final class UnsuccessfulTrackingSupplier implements Supplier<PatientExecutionResult<String>> {

        private int counter = 0;

        private int getCounter() {
            return counter;
        }

        @Override
        public PatientExecutionResult<String> get() {
            counter++;
            return PatientExecutionResult.empty();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    default void testCanInstantiate_PatientRetryStrategyContract() {
        Assertions.assertNotNull(getInstance(),
                                 "Should be able to instantiate this PatientRetryStrategy");
    }

    @Test
    default void testExecuteThrowsForNullTimeout_PatientRetryStrategyContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(null, SUCCESS_RESULT_SUPPLIER));
    }

    @Test
    default void testExecuteThrowsForNegativeTimeout_PatientRetryStrategyContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(Duration.ofMinutes(-1), SUCCESS_RESULT_SUPPLIER));
    }

    @Test
    default void testExecuteThrowsForNullResultSupplier_PatientRetryStrategyContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(Duration.ofMinutes(1), null));
    }

    @Test
    default void testExecuteWithZeroTimeoutMeansOneAttempt_PatientRetryStrategyContract() {
        UnsuccessfulTrackingSupplier supplier = new UnsuccessfulTrackingSupplier();
        try {
            getInstance().execute(Duration.ZERO, supplier);
        } catch (PatientTimeoutException ignore) { }
        Assertions.assertEquals(1,
                                supplier.getCounter(),
                                "An unsuccessful PatientRetryStrategy attempt with a duration of zero should execute only once");
    }

    @Test
    default void testExecuteSuccessDoesNotRetry_PatientRetryStrategyContract() {
        SuccessfulTrackingSupplier supplier = new SuccessfulTrackingSupplier();
        getInstance().execute(Duration.ofMinutes(1), supplier);
        Assertions.assertEquals(1,
                                supplier.getCounter(),
                                "An unsuccessful PatientRetryStrategy attempt with a duration of zero should execute only once");
    }

    @Test
    default void testExecuteSuccessReturnsSuppliedValue_PatientRetryStrategyContract() {
        Assertions.assertEquals("hello",
                                getInstance().execute(Duration.ZERO, SUCCESS_RESULT_SUPPLIER),
                                "A successful PatientRetryStrategy attempt should return the supplied value");
    }

    @Test
    default void testExecuteSuccessDoesNotWaitForTimeout_PatientRetryStrategyContract() {
        Duration timeout = Duration.ofMinutes(2);
        SuccessfulTrackingSupplier supplier = new SuccessfulTrackingSupplier();
        Instant start = Instant.now();
        getInstance().execute(timeout, supplier);
        Instant end = Instant.now();
        Assertions.assertTrue(Duration.between(start, end).compareTo(timeout) < 0,
                              "A successful PatientRetryStrategy attempt should not wait the full timeout");
    }

    @Test
    default void testExecuteThrowsTimeoutExceptionIfTimeoutIsReachedWithoutValidResult_PatientRetryStrategyContract() {
        Assertions.assertThrows(PatientTimeoutException.class,
                                () -> getInstance().execute(Duration.ofMillis(300), FAILURE_RESULT_SUPPLIER));
    }

    @Test
    default void testExecuteThrowsIfResultSupplierReturnsNull_PatientRetryStrategyContract() {
        Assertions.assertThrows(PatientException.class,
                                () -> getInstance().execute(Duration.ofMillis(300), () -> null));
    }

    @Test
    default void testExecuteRetriesForUnsuccessfulAttemptsBeforeTimeout_PatientRetryStrategyContract() {
        UnsuccessfulTrackingSupplier supplier = new UnsuccessfulTrackingSupplier();
        try {
            getInstance().execute(Duration.ofMillis(300), supplier);
        } catch (PatientTimeoutException ignore) { }
        Assertions.assertTrue(supplier.getCounter() > 1,
                              "An unsuccessful PatientRetryStrategy attempt should keep retrying getting a value when it isn't successful");
    }
}
