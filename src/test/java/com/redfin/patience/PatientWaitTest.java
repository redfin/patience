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

import com.redfin.patience.executions.SimpleExecutionHandler;
import com.redfin.patience.retries.FixedDelayPatientRetryHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

final class PatientWaitTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final Duration POSITIVE_DURATION;
    private static final Duration NEGATIVE_DURATION;
    private static final PatientExecutionHandler EXECUTION_HANDLER;
    private static final PatientRetryHandler RETRY_HANDLER;

    static {
        POSITIVE_DURATION = Duration.ofMillis(500);
        NEGATIVE_DURATION = Duration.ofMillis(-500);
        EXECUTION_HANDLER = PatientExecutionHandlers.simple();
        RETRY_HANDLER = PatientRetryHandlers.fixedDelay(Duration.ofMillis(500));
    }

    private PatientWait getInstance(Duration initialDelay,
                                    Duration defaultTimeout,
                                    PatientExecutionHandler executionHandler,
                                    PatientRetryHandler retryHandler) {
        return new PatientWait(initialDelay,
                               defaultTimeout,
                               executionHandler,
                               retryHandler);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiate() {
        Assertions.assertNotNull(getInstance(Duration.ZERO,
                                             Duration.ZERO,
                                             EXECUTION_HANDLER,
                                             RETRY_HANDLER),
                                 "Should be able to instantiate a PatientWait object.");
    }

    @Test
    void testThrowsForNullInitialDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(null,
                                                  POSITIVE_DURATION,
                                                  EXECUTION_HANDLER,
                                                  RETRY_HANDLER),
                                "Should throw an exception for a null initial delay");
    }

    @Test
    void testThrowsForNegativeInitialDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(NEGATIVE_DURATION,
                                                  POSITIVE_DURATION,
                                                  EXECUTION_HANDLER,
                                                  RETRY_HANDLER),
                                "Should throw an exception for a negative initial delay");
    }

    @Test
    void testThrowsForNullDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_DURATION,
                                                  null,
                                                  EXECUTION_HANDLER,
                                                  RETRY_HANDLER),
                                "Should throw an exception for a null default timeout");
    }

    @Test
    void testThrowsForNegativeDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_DURATION,
                                                  NEGATIVE_DURATION,
                                                  EXECUTION_HANDLER,
                                                  RETRY_HANDLER),
                                "Should throw an exception for a negative default timeout");
    }

    @Test
    void testThrowsForNullExecutionHandler() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_DURATION,
                                                  POSITIVE_DURATION,
                                                  null,
                                                  RETRY_HANDLER),
                                "Should throw an exception for a null execution handler");
    }

    @Test
    void testThrowsForNullRetryHandler() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_DURATION,
                                                  POSITIVE_DURATION,
                                                  EXECUTION_HANDLER,
                                                  null),
                                "Should throw an exception for a null retry handler");
    }

    @Test
    void testReturnsGivenInitialDelay() {
        Duration duration = Duration.ofSeconds(1);
        Assertions.assertEquals(duration,
                                getInstance(duration,
                                            POSITIVE_DURATION,
                                            EXECUTION_HANDLER,
                                            RETRY_HANDLER).getInitialDelay(),
                                "Should return the given initial delay");
    }

    @Test
    void testReturnsGivenDefaultTimeout() {
        Duration duration = Duration.ofSeconds(1);
        Assertions.assertEquals(duration,
                                getInstance(POSITIVE_DURATION,
                                            duration,
                                            EXECUTION_HANDLER,
                                            RETRY_HANDLER).getDefaultTimeout(),
                                "Should return the given default timeout");
    }

    @Test
    void testReturnsGivenExecutionHandler() {
        Assertions.assertEquals(EXECUTION_HANDLER,
                                getInstance(POSITIVE_DURATION,
                                            POSITIVE_DURATION,
                                            EXECUTION_HANDLER,
                                            RETRY_HANDLER).getExecutionHandler(),
                                "Should return the given execution handler");
    }

    @Test
    void testReturnsGivenRetryHandler() {
        Assertions.assertEquals(RETRY_HANDLER,
                                getInstance(POSITIVE_DURATION,
                                            POSITIVE_DURATION,
                                            EXECUTION_HANDLER,
                                            RETRY_HANDLER).getRetryHandler(),
                                "Should return the given retry handler");
    }

    @Test
    void testFromReturnsNonNull() {
        Assertions.assertNotNull(getInstance(POSITIVE_DURATION,
                                             POSITIVE_DURATION,
                                             EXECUTION_HANDLER,
                                             RETRY_HANDLER).from(() -> true),
                                 "Should return a non-null future with from(Executable) call.");
    }

    @Test
    void testFromThrowsForNullExecutable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(POSITIVE_DURATION,
                                                  POSITIVE_DURATION,
                                                  EXECUTION_HANDLER,
                                                  RETRY_HANDLER).from(null),
                                "Should throw for a null executable to from(Executable)");
    }

    @Test
    void testBuilderReturnsNonNull() {
        Assertions.assertNotNull(PatientWait.builder(),
                                 "PatientWait call to builder should return non-null Builder.");
    }

    @Test
    void testBuilderThrowsForNullInitialDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientWait.builder()
                                                 .withInitialDelay(null),
                                "PatientWait builder should throw for null initial delay.");
    }

    @Test
    void testBuilderThrowsForNegativeInitialDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientWait.builder()
                                                 .withInitialDelay(NEGATIVE_DURATION),
                                "PatientWait builder should throw for negative initial delay.");
    }

    @Test
    void testBuilderThrowsForNullDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientWait.builder()
                                                 .withDefaultTimeout(null),
                                "PatientWait builder should throw for null default timeout.");
    }

    @Test
    void testBuilderThrowsForNegativeDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientWait.builder()
                                                 .withDefaultTimeout(NEGATIVE_DURATION),
                                "PatientWait builder should throw for negative default timeout.");
    }

    @Test
    void testBuilderThrowsForExecutionHandler() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientWait.builder()
                                                 .withExecutionHandler(null),
                                "PatientWait builder should throw for null execution handler.");
    }

    @Test
    void testBuilderThrowsForRetryHandler() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientWait.builder()
                                                 .withRetryHandler(null),
                                "PatientWait builder should throw for null retry handler.");
    }

    @Test
    void testBuilderBuildReturnsNonNull() {
        Assertions.assertNotNull(PatientWait.builder().build(),
                                 "PatientWait builder call to build should return non-null PatientWait.");
    }

    @Test
    void testBuilderBuildReturnsWaitWithGivenInitialDuration() {
        Duration duration = Duration.ofSeconds(1);
        Assertions.assertEquals(duration,
                                PatientWait.builder()
                                           .withInitialDelay(duration)
                                           .build()
                                           .getInitialDelay(),
                                "Builder built PatientWait should return the given initial delay after building");
    }

    @Test
    void testBuilderBuildReturnsWaitWithGivenDefaultTimeout() {
        Duration duration = Duration.ofSeconds(1);
        Assertions.assertEquals(duration,
                                PatientWait.builder()
                                           .withDefaultTimeout(duration)
                                           .build()
                                           .getDefaultTimeout(),
                                "Builder built PatientWait should return the given default timeout after building");
    }

    @Test
    void testBuilderBuildReturnsWaitWithGivenExecutionHandler() {
        Assertions.assertEquals(EXECUTION_HANDLER,
                                PatientWait.builder()
                                           .withExecutionHandler(EXECUTION_HANDLER)
                                           .build()
                                           .getExecutionHandler(),
                                "Builder built PatientWait should return the given execution handler after building");
    }

    @Test
    void testBuilderBuildReturnsWaitWithGivenRetryHandler() {
        Assertions.assertEquals(RETRY_HANDLER,
                                PatientWait.builder()
                                           .withRetryHandler(RETRY_HANDLER)
                                           .build()
                                           .getRetryHandler(),
                                "Builder built PatientWait should return the given retry handler after building");
    }

    @Test
    void testBuilderCreatesPatientWaitWithExpectedDefaults() {
        PatientWait wait = PatientWait.builder().build();
        Assertions.assertAll(() -> Assertions.assertEquals(Duration.ZERO, wait.getInitialDelay()),
                             () -> Assertions.assertEquals(Duration.ZERO, wait.getDefaultTimeout()),
                             () -> Assertions.assertTrue(wait.getExecutionHandler() instanceof SimpleExecutionHandler),
                             () -> Assertions.assertTrue(wait.getRetryHandler() instanceof FixedDelayPatientRetryHandler));
    }

    @Test
    void testDefaultFilterReturnsTrueForTrue() {
        Assertions.assertTrue(PatientWait.getDefaultFilter().test(true),
                              "Default filter should return true for a true value.");
    }

    @Test
    void testDefaultFilterReturnsFalseForFalse() {
        Assertions.assertFalse(PatientWait.getDefaultFilter().test(false),
                               "Default filter should return false for a false value.");
    }

    @Test
    void testDefaultFilterReturnsTrueForNonNull() {
        Assertions.assertTrue(PatientWait.getDefaultFilter().test("hello"),
                              "Default filter should return true for a non-null, non-boolean value.");

    }

    @Test
    void testDefaultFilterReturnsFalseForNull() {
        Assertions.assertFalse(PatientWait.getDefaultFilter().test(null),
                               "Default filter should return false for a null value.");

    }
}
