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

import com.redfin.patience.delays.FixedPatientDelaySupplierFactory;
import com.redfin.patience.exceptions.PatientException;
import com.redfin.patience.exceptions.PatientRetryException;
import com.redfin.patience.executions.SimplePatientExecutionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("When a PatientRetryFuture")
final class PatientRetryFutureTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private PatientRetryFuture<Boolean> getInstance() {
        return getInstance("Start message");
    }

    private PatientRetryFuture<Boolean> getInstance(String message) {
        return getInstance(Thread::sleep,
                           Duration.ZERO,
                           0,
                           new SimplePatientExecutionHandler(),
                           new FixedPatientDelaySupplierFactory(Duration.ZERO),
                           () -> true,
                           bool -> null != bool && bool,
                           message);
    }

    private PatientRetryFuture<Boolean> getInstance(PatientSleep sleep,
                                                    Duration initialDelay,
                                                    int defaultNumberOfRetries,
                                                    PatientExecutionHandler executionHandler,
                                                    PatientDelaySupplierFactory delaySupplierFactory,
                                                    PatientExecutable<Boolean> executable,
                                                    Predicate<Boolean> filter,
                                                    String failureMessage) {
        return new PatientRetryFuture<>(sleep,
                                        initialDelay,
                                        defaultNumberOfRetries,
                                        executionHandler,
                                        delaySupplierFactory,
                                        executable,
                                        filter,
                                        failureMessage);
    }

    private PatientRetryFuture<Boolean> getInstance(PatientSleep sleep,
                                                    Duration initialDelay,
                                                    int defaultNumberOfRetries,
                                                    PatientExecutionHandler executionHandler,
                                                    PatientDelaySupplierFactory delaySupplierFactory,
                                                    PatientExecutable<Boolean> executable,
                                                    Predicate<Boolean> filter,
                                                    Supplier<String> failureMessageSupplier) {
        return new PatientRetryFuture<>(sleep,
                                        initialDelay,
                                        defaultNumberOfRetries,
                                        executionHandler,
                                        delaySupplierFactory,
                                        executable,
                                        filter,
                                        failureMessageSupplier);
    }

    private static final class ValidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            PatientSleep sleep = Thread::sleep;
            PatientExecutionHandler executionHandler = new SimplePatientExecutionHandler();
            PatientDelaySupplierFactory delaySupplierFactory = new FixedPatientDelaySupplierFactory(Duration.ZERO);
            PatientExecutable<Boolean> executable = () -> true;
            Predicate<Boolean> filter = bool -> null != bool && bool;
            Supplier<String> messageSupplier = () -> "hello";
            return Stream.of(Arguments.of(sleep, Duration.ZERO, 0, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ofMillis(500), 0, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, 1, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ofMillis(500), 1, executionHandler, delaySupplierFactory, executable, filter, messageSupplier));
        }
    }

    private static final class InvalidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            PatientSleep sleep = Thread::sleep;
            PatientExecutionHandler executionHandler = new SimplePatientExecutionHandler();
            PatientDelaySupplierFactory delaySupplierFactory = new FixedPatientDelaySupplierFactory(Duration.ZERO);
            PatientExecutable<Boolean> executable = () -> true;
            Predicate<Boolean> filter = bool -> null != bool && bool;
            Supplier<String> messageSupplier = () -> "hello";
            return Stream.of(Arguments.of(null, Duration.ZERO, 0, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, null, 0, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ofMillis(-500), 0, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, -1, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, 0, null, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, 0, executionHandler, null, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, 0, executionHandler, delaySupplierFactory, null, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, 0, executionHandler, delaySupplierFactory, executable, null, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, 0, executionHandler, delaySupplierFactory, executable, filter, null));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is constructed")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("it returns successfully with valid arguments")
        @ArgumentsSource(PatientRetryFutureTest.ValidArgumentsProvider.class)
        void testValidArgumentsSucceed(PatientSleep sleep,
                                       Duration initialDelay,
                                       int defaultNumberOfRetries,
                                       PatientExecutionHandler executionHandler,
                                       PatientDelaySupplierFactory delaySupplierFactory,
                                       PatientExecutable<Boolean> executable,
                                       Predicate<Boolean> filter,
                                       Supplier<String> messageSupplier) {
            try {
                Assertions.assertNotNull(getInstance(sleep,
                                                     initialDelay,
                                                     defaultNumberOfRetries,
                                                     executionHandler,
                                                     delaySupplierFactory,
                                                     executable,
                                                     filter,
                                                     messageSupplier),
                                         "Should have returned a non-null instance for valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Expected to construct a PatientRetryFuture with valid arguments but throwable was caught: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("it returns successfully with valid arguments and a null message")
        @ArgumentsSource(PatientRetryFutureTest.ValidArgumentsProvider.class)
        void testSucceedsWithNullMessage(PatientSleep sleep,
                                         Duration initialDelay,
                                         int defaultNumberOfRetries,
                                         PatientExecutionHandler executionHandler,
                                         PatientDelaySupplierFactory delaySupplierFactory,
                                         PatientExecutable<Boolean> executable,
                                         Predicate<Boolean> filter) {
            try {
                Assertions.assertNotNull(getInstance(sleep,
                                                     initialDelay,
                                                     defaultNumberOfRetries,
                                                     executionHandler,
                                                     delaySupplierFactory,
                                                     executable,
                                                     filter,
                                                     (String) null),
                                         "Should have returned a non-null instance for valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Expected to construct a PatientRetryFuture with valid arguments but throwable was caught: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("it throws an exception for invalid arguments")
        @ArgumentsSource(PatientRetryFutureTest.InvalidArgumentsProvider.class)
        void testInvalidArgumentsThrowsException(PatientSleep sleep,
                                                 Duration initialDelay,
                                                 int defaultNumberOfRetries,
                                                 PatientExecutionHandler executionHandler,
                                                 PatientDelaySupplierFactory delaySupplierFactory,
                                                 PatientExecutable<Boolean> executable,
                                                 Predicate<Boolean> filter,
                                                 Supplier<String> messageSupplier) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(sleep,
                                                      initialDelay,
                                                      defaultNumberOfRetries,
                                                      executionHandler,
                                                      delaySupplierFactory,
                                                      executable,
                                                      filter,
                                                      messageSupplier),
                                    "Should have thrown an exception for an invalid argument");
        }
    }

    @Nested
    @DisplayName("has it's getter methods called")
    final class GetterTests {

        @Test
        @DisplayName("it returns the given values")
        void testReturnsGivenValues() {
            PatientSleep sleep = Thread::sleep;
            Duration duration = Duration.ZERO;
            int numberOfRetries = 1;
            PatientExecutionHandler executionHandler = new SimplePatientExecutionHandler();
            PatientDelaySupplierFactory delaySupplierFactory = new FixedPatientDelaySupplierFactory(duration);
            PatientExecutable<Boolean> executable = () -> true;
            Predicate<Boolean> filter = bool -> null != bool && bool;
            Supplier<String> failureMessageSupplier = () -> "hello";
            PatientRetryFuture<Boolean> future = getInstance(sleep,
                                                             duration,
                                                             numberOfRetries,
                                                             executionHandler,
                                                             delaySupplierFactory,
                                                             executable,
                                                             filter,
                                                             failureMessageSupplier);
            Assertions.assertAll(() -> Assertions.assertEquals(sleep, future.getSleep(), "Should return the given sleep"),
                                 () -> Assertions.assertEquals(duration, future.getInitialDelay(), "Should return the given initial delay"),
                                 () -> Assertions.assertEquals(numberOfRetries, future.getDefaultNumberOfRetries(), "Should return the given default timeout"),
                                 () -> Assertions.assertEquals(executionHandler, future.getExecutionHandler(), "Should return the given execution handler"),
                                 () -> Assertions.assertEquals(delaySupplierFactory, future.getDelaySupplierFactory(), "Should return the given delay supplier factory"),
                                 () -> Assertions.assertEquals(executable, future.getExecutable(), "Should return the given executable"),
                                 () -> Assertions.assertEquals(filter, future.getFilter(), "Should return the given filter"),
                                 () -> Assertions.assertEquals(failureMessageSupplier, future.getFailureMessageSupplier(), "Should return the given failure message supplier"));
        }

        @Test
        @DisplayName("it returns a failure message supplier that has the given message")
        void testReturnsGivenFailureMessage() {
            String message = "hello";
            Assertions.assertEquals(message,
                                    getInstance(message).getFailureMessageSupplier().get(),
                                    "Should return the given message");
        }
    }

    @Nested
    @DisplayName("has it's withMessage method called")
    final class WithMessageTests {

        @Test
        @DisplayName("it returns a non-null future for a null message")
        void testWithFailureMessageReturnsNonNullForNullMessage() {
            Assertions.assertNotNull(getInstance().withMessage((String) null),
                                     "Should be able to give a null message to the withMessage(String) method.");
        }

        @Test
        @DisplayName("it returns a non-null future for an empty message")
        void testWithFailureMessageReturnsNonNullForEmptyMessage() {
            Assertions.assertNotNull(getInstance().withMessage(""),
                                     "Should be able to give an empty message to the withMessage(String) method.");
        }

        @Test
        @DisplayName("it throws an exception for a null supplier")
        void testWithFailureMessageThrowsForNullSupplier() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withMessage((Supplier<String>) null),
                                    "Should throw for withMessage(Supplier<String>) with a null argument");
        }
    }

    @Nested
    @DisplayName("has it's withFilter method called")
    final class WithFilterTests {

        @Test
        @DisplayName("it returns a future with the given filter")
        void testWithFilterReturnsNonNull() {
            Predicate<Boolean> filter = Objects::nonNull;
            Assertions.assertEquals(filter,
                                    getInstance().withFilter(filter)
                                                 .getFilter(),
                                    "Should return a future from withFilter(Predicate) that has the given filter");
        }

        @Test
        @DisplayName("it throws an exception for a null filter")
        void testWithFilterThrowsForNullFilter() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withFilter(null),
                                    "Should throw for withFilter(Predicate) with a null argument");
        }
    }

    @Nested
    @DisplayName("has it's get methods called")
    final class GetTests {

        @Test
        @DisplayName("it returns value for a successful retry")
        void testGetReturnsValueWhenSuccessful() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> true,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertEquals(true,
                                    future.get(),
                                    "Should return the expected result when successful");
        }

        @Test
        @DisplayName("it throws an exception for an unsuccessful retry")
        void testGetThrowsWhenUnsuccessful() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientRetryException.class,
                                    future::get,
                                    "Should throw from get() when unsuccessful");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a null duration")
        void testGetThrowsAnExceptionIfDelaySupplierReturnsNull() {
            PatientDelaySupplierFactory supplierFactory = () -> () -> null;
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             supplierFactory,
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.get(10),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a negative duration")
        void testGetThrowsAnExceptionIfDelaySupplierReturnsNegative() {
            PatientDelaySupplierFactory supplierFactory = () -> () -> Duration.ofMillis(-500);
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             supplierFactory,
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.get(10),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it only executes once for a duration of zero")
        @SuppressWarnings("unchecked")
        void testGetWithZeroDurationOnlyExecutesOnce() throws Throwable {
            PatientExecutable<Boolean> executable = mock(PatientExecutable.class);
            when(executable.execute()).thenReturn(false);
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             executable,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientRetryException.class,
                                    future::get,
                                    "Should throw from get() when unsuccessful");
            verify(executable, times(1)).execute();
        }

        @Test
        @DisplayName("it executes more than once for a failure if the duration is greater than zero")
        void testGetExecutesMoreThanOnceWhenUnsuccessful() {
            AtomicInteger counter = new AtomicInteger(0);
            PatientExecutable<Boolean> executable = () -> {
                counter.incrementAndGet();
                return false;
            };
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             executable,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientRetryException.class,
                                    () -> future.get(10),
                                    "Unsuccessful attempts should keep executing until the timeout");
            Assertions.assertTrue(counter.get() > 1,
                                  "The executable should have been executed more than once");
        }

        @Test
        @DisplayName("it stops executing when a passing value is found")
        void testGetStopsExecutingWhenValidResultIsFound() {
            AtomicInteger counter = new AtomicInteger(0);
            PatientExecutable<Boolean> executable = () -> counter.incrementAndGet() == 2;
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             executable,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            future.get(10);
            Assertions.assertEquals(2,
                                    counter.get(),
                                    "The executable should have stop being executed once a passing value was found");
        }

        @Test
        @DisplayName("it throws an exception if a null supplier of delay durations is returned from the factory")
        void testCheckThrowsForNullDelayDurationSupplier() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             () -> null,
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::get,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception if a null execution result is returned")
        void testCheckThrowsForNullExecutionResult() {
            PatientExecutionHandler handler = new PatientExecutionHandler() {
                @Override
                public <T> PatientExecutionResult<T> execute(PatientExecutable<T> executable, Predicate<T> filter) {
                    return null;
                }
            };
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             handler,
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::get,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception if an unhandled throwable is thrown by the execution handler")
        void testCheckThrowsForUnexpectedThrowableFromExecutable() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> {
                                                                 throw new RuntimeException("whoops");
                                                             },
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::get,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception for a negative number of retries")
        void testGetThrowsForNegativeNumberOfRetries() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().get(-10),
                                    "Should throw from get(int) for negative number of retries");
        }
    }

    @Nested
    @DisplayName("has it's check methods called")
    final class CheckTests {

        @Test
        @DisplayName("it returns true for a successful retry")
        void testCheckReturnsTrueWhenSuccessful() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> true,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertTrue(future.check(),
                                  "Should return true when successful.");
        }

        @Test
        @DisplayName("it returns false for an unsuccessful retry")
        void testCheckReturnsFalseWhenUnsuccessful() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertFalse(future::check,
                                   "Should return false when unsuccessful.");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a null duration")
        void testCheckThrowsAnExceptionIfDelaySupplierReturnsNull() {
            PatientDelaySupplierFactory supplierFactory = () -> () -> null;
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             supplierFactory,
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.check(10),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a negative duration")
        void testCheckThrowsAnExceptionIfDelaySupplierReturnsNegative() {
            PatientDelaySupplierFactory supplierFactory = () -> () -> Duration.ofMillis(-500);
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             supplierFactory,
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.check(10),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it only executes once for a duration of zero")
        @SuppressWarnings("unchecked")
        void testCheckWithZeroDurationOnlyExecutesOnce() throws Throwable {
            PatientExecutable<Boolean> executable = mock(PatientExecutable.class);
            when(executable.execute()).thenReturn(false);
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             executable,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertFalse(future.check(),
                                   "Should return false for an unsuccessful check");
            verify(executable, times(1)).execute();
        }

        @Test
        @DisplayName("it executes more than once for a failure if the duration is greater than zero")
        void testCheckExecutesMoreThanOnceWhenUnsuccessful() {
            AtomicInteger counter = new AtomicInteger(0);
            PatientExecutable<Boolean> executable = () -> {
                counter.incrementAndGet();
                return false;
            };
            int numRetries = 10;
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             numRetries,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             executable,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertFalse(future.check(numRetries),
                                   "Should return false for unsuccessful check.");
            Assertions.assertEquals(numRetries + 1,
                                    counter.get(),
                                    "The executable should have been executed more than once");
        }

        @Test
        @DisplayName("it stops executing when a passing value is found")
        void testCheckStopsExecutingWhenValidResultIsFound() {
            AtomicInteger counter = new AtomicInteger(0);
            PatientExecutable<Boolean> executable = () -> counter.incrementAndGet() == 2;
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             executable,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            future.check(10);
            Assertions.assertEquals(2,
                                    counter.get(),
                                    "The executable should have stop being executed once a passing value was found");
        }

        @Test
        @DisplayName("it throws an exception if a null supplier of delay durations is returned from the factory")
        void testCheckThrowsForNullDelayDurationSupplier() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             () -> null,
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::check,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception if a null execution result is returned")
        void testCheckThrowsForNullExecutionResult() {
            PatientExecutionHandler handler = new PatientExecutionHandler() {
                @Override
                public <T> PatientExecutionResult<T> execute(PatientExecutable<T> executable, Predicate<T> filter) {
                    return null;
                }
            };
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             handler,
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> false,
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::check,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception if an unhandled throwable is thrown by the execution handler")
        void testCheckThrowsForUnexpectedThrowableFromExecutable() {
            PatientRetryFuture<Boolean> future = getInstance(Thread::sleep,
                                                             Duration.ZERO,
                                                             0,
                                                             new SimplePatientExecutionHandler(),
                                                             new FixedPatientDelaySupplierFactory(Duration.ZERO),
                                                             () -> {
                                                                 throw new RuntimeException("whoops");
                                                             },
                                                             bool -> null != bool && bool,
                                                             "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::check,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception for a negative number of retries")
        void testCheckThrowsForNegativeDuration() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().check(-1),
                                    "Should throw from check(int) for negative number of retries");
        }
    }
}
