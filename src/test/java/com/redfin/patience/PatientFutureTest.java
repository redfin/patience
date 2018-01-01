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
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class PatientFutureTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private PatientFuture<Boolean> getInstance() {
        return getInstance("Start message");
    }

    private PatientFuture<Boolean> getInstance(String message) {
        return getInstance(Duration.ZERO,
                           Duration.ZERO,
                           new FixedDelayPatientRetryHandler(Duration.ZERO),
                           new SimpleExecutionHandler(),
                           () -> true,
                           bool -> null != bool && bool,
                           message);
    }

    private PatientFuture<Boolean> getInstance(Duration initialDelay,
                                               Duration defaultTimeout,
                                               PatientRetryHandler retryHandler,
                                               PatientExecutionHandler executionHandler,
                                               Executable<Boolean> executable,
                                               Predicate<Boolean> filter,
                                               String failureMessage) {
        return new PatientFuture<>(initialDelay,
                                   defaultTimeout,
                                   retryHandler,
                                   executionHandler,
                                   executable,
                                   filter,
                                   failureMessage);
    }

    private PatientFuture<Boolean> getInstance(Supplier<String> messageSupplier) {
        return getInstance(Duration.ZERO,
                           Duration.ZERO,
                           new FixedDelayPatientRetryHandler(Duration.ZERO),
                           new SimpleExecutionHandler(),
                           () -> true,
                           bool -> null != bool && bool,
                           messageSupplier);
    }

    private PatientFuture<Boolean> getInstance(Duration initialDelay,
                                               Duration defaultTimeout,
                                               PatientRetryHandler retryHandler,
                                               PatientExecutionHandler executionHandler,
                                               Executable<Boolean> executable,
                                               Predicate<Boolean> filter,
                                               Supplier<String> failureMessageSupplier) {
        return new PatientFuture<>(initialDelay,
                                   defaultTimeout,
                                   retryHandler,
                                   executionHandler,
                                   executable,
                                   filter,
                                   failureMessageSupplier);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiate() {
        Assertions.assertNotNull(getInstance("Whoops"),
                                 "Should be able to instantiate a PatientFuture");
    }

    @Test
    void testCanInstantiateWithSupplier() {
        Assertions.assertNotNull(getInstance(() -> "hello"),
                                 "Should be able to instantiate a PatientFuture");
    }

    @Test
    void testCanInstantiateWithEmptyFailureMessage() {
        Assertions.assertNotNull(getInstance(""),
                                 "Should be able to instantiate with an empty failure message.");
    }

    @Test
    void testCanInstantiateWithNullFailureMessage() {
        Assertions.assertNotNull(getInstance((String) null),
                                 "Should be able to instantiate with a null failure message.");
    }

    @Test
    void testReturnsGivenValues() {
        Duration durations = Duration.ZERO;
        PatientRetryHandler retryHandler = new FixedDelayPatientRetryHandler(Duration.ZERO);
        PatientExecutionHandler executionHandler = new SimpleExecutionHandler();
        Executable<Boolean> executable = () -> true;
        Predicate<Boolean> filter = bool -> null != bool && bool;
        String failureMessage = "whoops";
        PatientFuture<Boolean> future = new PatientFuture<>(durations,
                                                            durations,
                                                            retryHandler,
                                                            executionHandler,
                                                            executable,
                                                            filter,
                                                            failureMessage);
        Assertions.assertAll(() -> Assertions.assertEquals(durations,
                                                           future.getInitialDelay(),
                                                           "Should return the given initial delay"),
                             () -> Assertions.assertEquals(durations,
                                                           future.getDefaultTimeout(),
                                                           "Should return the given default timeout"),
                             () -> Assertions.assertEquals(retryHandler,
                                                           future.getRetryHandler(),
                                                           "Should return the given retry handler"),
                             () -> Assertions.assertEquals(executionHandler,
                                                           future.getExecutionHandler(),
                                                           "Should return the given execution handler"),
                             () -> Assertions.assertEquals(executable,
                                                           future.getExecutable(),
                                                           "Should return the given executable"),
                             () -> Assertions.assertEquals(filter,
                                                           future.getFilter(),
                                                           "Should return the given filter"),
                             () -> Assertions.assertEquals(failureMessage,
                                                           future.getFailureMessageSupplier().get(),
                                                           "Should return the given failure message"));
    }

    @Test
    void testThrowsForNullInitialDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(null,
                                                  Duration.ZERO,
                                                  new FixedDelayPatientRetryHandler(Duration.ZERO),
                                                  new SimpleExecutionHandler(),
                                                  () -> true,
                                                  bool -> null != bool && bool,
                                                  "hello, world"),
                                "Should throw for a null initial delay");
    }

    @Test
    void testThrowsForNegativeInitialDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ofMillis(-100),
                                                  Duration.ZERO,
                                                  new FixedDelayPatientRetryHandler(Duration.ZERO),
                                                  new SimpleExecutionHandler(),
                                                  () -> true,
                                                  bool -> null != bool && bool,
                                                  "hello, world"),
                                "Should throw for a negative initial delay");
    }

    @Test
    void testThrowsForNullDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ZERO,
                                                  null,
                                                  new FixedDelayPatientRetryHandler(Duration.ZERO),
                                                  new SimpleExecutionHandler(),
                                                  () -> true,
                                                  bool -> null != bool && bool,
                                                  "hello, world"),
                                "Should throw for a null default timeout");
    }

    @Test
    void testThrowsForNegativeDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ZERO,
                                                  Duration.ofMillis(-100),
                                                  new FixedDelayPatientRetryHandler(Duration.ZERO),
                                                  new SimpleExecutionHandler(),
                                                  () -> true,
                                                  bool -> null != bool && bool,
                                                  "hello, world"),
                                "Should throw for a null initial delay");
    }

    @Test
    void testThrowsForNullRetryHandler() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ZERO,
                                                  Duration.ZERO,
                                                  null,
                                                  new SimpleExecutionHandler(),
                                                  () -> true,
                                                  bool -> null != bool && bool,
                                                  "hello, world"),
                                "Should throw for a null retry handler");
    }

    @Test
    void testThrowsForNullExecutionHandler() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ZERO,
                                                  Duration.ZERO,
                                                  new FixedDelayPatientRetryHandler(Duration.ZERO),
                                                  null,
                                                  () -> true,
                                                  bool -> null != bool && bool,
                                                  "hello, world"),
                                "Should throw for a null execution handler");
    }

    @Test
    void testThrowsForNullExecutable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ZERO,
                                                  Duration.ZERO,
                                                  new FixedDelayPatientRetryHandler(Duration.ZERO),
                                                  new SimpleExecutionHandler(),
                                                  null,
                                                  bool -> null != bool && bool,
                                                  "hello, world"),
                                "Should throw for a null executable");
    }

    @Test
    void testThrowsForNullFilter() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(Duration.ZERO,
                                                  Duration.ZERO,
                                                  new FixedDelayPatientRetryHandler(Duration.ZERO),
                                                  new SimpleExecutionHandler(),
                                                  () -> true,
                                                  null,
                                                  "hello, world"),
                                "Should throw for a null filter");
    }

    @Test
    void testThrowsWithNullFailureMessageSupplier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance((Supplier<String>) null),
                                "Should throw an exception for a null failure message supplier");
    }

    @Test
    void testWithFailureMessageReturnsNonNull() {
        String message = "ha";
        Assertions.assertEquals(message,
                                getInstance().withMessage(message)
                                             .getFailureMessageSupplier()
                                             .get(),
                                "Should return a future from withMessage(String) that has the given failure message");
    }

    @Test
    void testWithFailureMessageThrowsForNullMessage() {
        Assertions.assertNotNull(getInstance().withMessage((String) null),
                                 "Should be able to give a null message to the withMessage(String) method.");
    }

    @Test
    void testWithFailureMessageReturnsNonNullForEmptyMessage() {
        Assertions.assertNotNull(getInstance().withMessage(""),
                                 "Should be able to give an empty message to the withMessage(String) method.");
    }

    @Test
    void testWithFailureMessageThrowsForNullSupplier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().withMessage((Supplier<String>) null),
                                "Should throw for withMessage(Supplier<String>) with a null argument");
    }

    @Test
    void testWithFilterReturnsNonNull() {
        Predicate<Boolean> filter = Objects::nonNull;
        Assertions.assertEquals(filter,
                                getInstance().withFilter(filter)
                                             .getFilter(),
                                "Should return a future from withFilter(Predicate) that has the given filter");
    }

    @Test
    void testWithFilterThrowsForNullFilter() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().withFilter(null),
                                "Should throw for withFilter(Predicate) with a null argument");
    }

    @Test
    void testGetReturnsValueWhenSuccessful() {
        PatientFuture<Boolean> future = getInstance(Duration.ZERO,
                                                    Duration.ZERO,
                                                    new FixedDelayPatientRetryHandler(Duration.ofMillis(1)),
                                                    new SimpleExecutionHandler(),
                                                    () -> true,
                                                    bool -> null != bool && bool,
                                                    "whoops");
        Assertions.assertEquals(true,
                                future.get(),
                                "Should return the expected result when successful");
    }

    @Test
    void testGetThrowsWhenUnsuccessful() {
        PatientFuture<Boolean> future = getInstance(Duration.ZERO,
                                                    Duration.ZERO,
                                                    new FixedDelayPatientRetryHandler(Duration.ofMillis(1)),
                                                    new SimpleExecutionHandler(),
                                                    () -> false,
                                                    bool -> null != bool && bool,
                                                    "whoops");
        Assertions.assertThrows(PatientTimeoutException.class,
                                future::get,
                                "Should throw from get() when unsuccessful");
    }

    @Test
    void testGetThrowsForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().get(null),
                                "Should throw from get(Duration) for null duration");
    }

    @Test
    void testGetThrowsForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().get(Duration.ofMillis(-100)),
                                "Should throw from get(Duration) for negative duration");
    }

    @Test
    void testGetThrowsForNullResult() {
        PatientFuture<Boolean> future = getInstance(Duration.ZERO,
                                                    Duration.ZERO,
                                                    new PatientRetryHandler() {
                                                        @Override
                                                        public <T> PatientResult<T> execute(Supplier<PatientExecutionResult<T>> patientExecutionResultSupplier, Duration maxDuration) {
                                                            return null;
                                                        }
                                                    },
                                                    new SimpleExecutionHandler(),
                                                    () -> true,
                                                    bool -> null != bool && bool,
                                                    "Whoops");
        Assertions.assertThrows(PatientException.class,
                                future::get,
                                "Should throw from get if the retry handler returns a null result");
    }

    @Test
    void testCheckReturnsTrueWhenSuccessful() {
        PatientFuture<Boolean> future = getInstance(Duration.ZERO,
                                                    Duration.ZERO,
                                                    new FixedDelayPatientRetryHandler(Duration.ofMillis(1)),
                                                    new SimpleExecutionHandler(),
                                                    () -> true,
                                                    bool -> null != bool && bool,
                                                    "whoops");
        Assertions.assertTrue(future.check(),
                              "Should return true when successful.");
    }

    @Test
    void testCheckReturnsFalseWhenUnsuccessful() {
        PatientFuture<Boolean> future = getInstance(Duration.ZERO,
                                                    Duration.ZERO,
                                                    new FixedDelayPatientRetryHandler(Duration.ofMillis(1)),
                                                    new SimpleExecutionHandler(),
                                                    () -> false,
                                                    bool -> null != bool && bool,
                                                    "whoops");
        Assertions.assertFalse(future::check,
                                "Should return false when unsuccessful.");
    }
}
