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

import com.redfin.patience.PatientException;
import com.redfin.patience.PatientExecutionResult;
import com.redfin.patience.PatientRetryHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

interface PatientRetryHandlerContract<T extends PatientRetryHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    T getInstance();
    Duration getMultipleExecutionDuration();

    PatientExecutionResult<Boolean> SUCCESSFUL_EXECUTION_RESULT = PatientExecutionResult.pass(true);
    PatientExecutionResult<Boolean> UNSUCCESSFUL_EXECUTION_RESULT = PatientExecutionResult.fail("whoops");

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    default void testExecuteThrowsForNullExecutionResultSupplier_PatientRetryHandlerContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(null, Duration.ZERO).getResult(),
                                "Should throw for execute with a null patient execution result supplier.");
    }

    @Test
    default void testExecuteThrowsForNullMaxDuration_PatientRetryHandlerContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(() -> null, null),
                                "Should throw for execute with a null max duration.");
    }

    @Test
    default void testExecuteThrowsForNegativeMaxDuration_PatientRetryHandlerContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(() -> null, Duration.ofMillis(-500)),
                                "Should throw for a negative max duration.");
    }

    @Test
    default void testExecuteReturnsSuccessfulResultForSuccessfulExecutionResult_PatientRetryHandlerContract() {
        Assertions.assertTrue(getInstance().execute(() -> SUCCESSFUL_EXECUTION_RESULT, Duration.ZERO).isSuccess(),
                              "Should return a successful result for a successful execution.");
    }

    @Test
    default void testExecuteThrowsForNullPatientExecutionResultFromSupplier_PatientRetryHandlerContract() {
        Assertions.assertThrows(PatientException.class,
                                () -> getInstance().execute(() -> null, Duration.ZERO),
                                "Should throw for a null result from the supplier.");
    }

    @Test
    default void testExecuteReturnsUnsuccessfulResultForUnsuccessfulExecutionResult_PatientRetryHandlerContract() {
        Assertions.assertFalse(getInstance().execute(() -> UNSUCCESSFUL_EXECUTION_RESULT, Duration.ZERO).isSuccess(),
                              "Should return an unsuccessful result for an unsuccessful execution.");
    }

    @Test
    default void testExecuteOnlyOccursOnceForZeroTimeoutDuration_PatientRetryHandlerContract() {
        AtomicInteger count = new AtomicInteger(0);
        Supplier<PatientExecutionResult<Boolean>> supplier = () -> {
            count.incrementAndGet();
            return PatientExecutionResult.fail("whoops");
        };
        getInstance().execute(supplier, Duration.ZERO);
        Assertions.assertEquals(1,
                                count.get(),
                                "Should only try once with a zero duration.");
    }

    @Test
    default void testExecuteTriesUntilSuccessful_PatientRetryHandlerContract() {
        AtomicInteger count = new AtomicInteger(0);
        Supplier<PatientExecutionResult<Boolean>> supplier = () -> {
            int current = count.incrementAndGet();
            if (current == 2) {
                return PatientExecutionResult.pass(true);
            } else {
                return PatientExecutionResult.fail("count: " + count);
            }
        };
        getInstance().execute(supplier, getMultipleExecutionDuration());
        Assertions.assertEquals(2,
                                count.get(),
                                "Should retry until successful.");
    }

    @Test
    default void testShouldReturnSuccessForPassingResult_PatientRetryHandlerContract() {
        Assertions.assertTrue(getInstance().execute(() -> PatientExecutionResult.pass(true), Duration.ZERO)
                                           .isSuccess(),
                              "Should return a successful result for a passing test");
    }

    @Test
    default void testShouldReturnUnsuccessfulForNonPassingResult_PatientRetryHandlerContract() {
        Assertions.assertFalse(getInstance().execute(() -> PatientExecutionResult.fail("failure"), Duration.ZERO)
                                            .isSuccess(),
                               "Should return an unsuccessful result for a failing test");
    }

    @Test
    default void testShouldThrowForThrowableFromResultSupplier() {
        Assertions.assertThrows(PatientException.class,
                                () -> getInstance().execute(() -> { throw new AssertionError("Whoops"); },
                                                            Duration.ZERO),
                                "Should throw for a execution result supplier throwable");
    }
}
