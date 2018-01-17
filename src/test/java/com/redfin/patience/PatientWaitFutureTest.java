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

import com.redfin.patience.delays.FixedDelaySupplierFactory;
import com.redfin.patience.exceptions.PatientException;
import com.redfin.patience.exceptions.PatientTimeoutException;
import com.redfin.patience.executions.SimpleExecutionHandler;
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

@DisplayName("When a PatientWaitFuture")
final class PatientWaitFutureTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private PatientWaitFuture<Boolean> getInstance() {
        return getInstance("Start message");
    }

    private PatientWaitFuture<Boolean> getInstance(String message) {
        return getInstance(Thread::sleep,
                           Duration.ZERO,
                           Duration.ZERO,
                           new SimpleExecutionHandler(),
                           new FixedDelaySupplierFactory(Duration.ZERO),
                           () -> true,
                           bool -> null != bool && bool,
                           message);
    }

    private PatientWaitFuture<Boolean> getInstance(Sleep sleep,
                                                   Duration initialDelay,
                                                   Duration defaultTimeout,
                                                   PatientExecutionHandler executionHandler,
                                                   DelaySupplierFactory delaySupplierFactory,
                                                   PatientExecutable<Boolean> executable,
                                                   Predicate<Boolean> filter,
                                                   String failureMessage) {
        return new PatientWaitFuture<>(sleep,
                                       initialDelay,
                                       defaultTimeout,
                                       executionHandler,
                                       delaySupplierFactory,
                                       executable,
                                       filter,
                                       failureMessage);
    }

    private PatientWaitFuture<Boolean> getInstance(Sleep sleep,
                                                   Duration initialDelay,
                                                   Duration defaultTimeout,
                                                   PatientExecutionHandler executionHandler,
                                                   DelaySupplierFactory delaySupplierFactory,
                                                   PatientExecutable<Boolean> executable,
                                                   Predicate<Boolean> filter,
                                                   Supplier<String> failureMessageSupplier) {
        return new PatientWaitFuture<>(sleep,
                                       initialDelay,
                                       defaultTimeout,
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
            Sleep sleep = Thread::sleep;
            PatientExecutionHandler executionHandler = new SimpleExecutionHandler();
            DelaySupplierFactory delaySupplierFactory = new FixedDelaySupplierFactory(Duration.ZERO);
            PatientExecutable<Boolean> executable = () -> true;
            Predicate<Boolean> filter = bool -> null != bool && bool;
            Supplier<String> messageSupplier = () -> "hello";
            return Stream.of(Arguments.of(sleep, Duration.ZERO, Duration.ZERO, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ofMillis(500), Duration.ZERO, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, Duration.ofMillis(500), executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ofMillis(500), Duration.ofMillis(500), executionHandler, delaySupplierFactory, executable, filter, messageSupplier));
        }
    }

    private static final class InvalidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Sleep sleep = Thread::sleep;
            PatientExecutionHandler executionHandler = new SimpleExecutionHandler();
            DelaySupplierFactory delaySupplierFactory = new FixedDelaySupplierFactory(Duration.ZERO);
            PatientExecutable<Boolean> executable = () -> true;
            Predicate<Boolean> filter = bool -> null != bool && bool;
            Supplier<String> messageSupplier = () -> "hello";
            return Stream.of(Arguments.of(null, Duration.ZERO, Duration.ZERO, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, null, Duration.ZERO, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ofMillis(-500), Duration.ZERO, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, null, executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, Duration.ofMillis(-500), executionHandler, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, Duration.ZERO, null, delaySupplierFactory, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, Duration.ZERO, executionHandler, null, executable, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, Duration.ZERO, executionHandler, delaySupplierFactory, null, filter, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, Duration.ZERO, executionHandler, delaySupplierFactory, executable, null, messageSupplier),
                             Arguments.of(sleep, Duration.ZERO, Duration.ZERO, executionHandler, delaySupplierFactory, executable, filter, null));
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
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testValidArgumentsSucceed(Sleep sleep,
                                       Duration initialDelay,
                                       Duration defaultTimeout,
                                       PatientExecutionHandler executionHandler,
                                       DelaySupplierFactory delaySupplierFactory,
                                       PatientExecutable<Boolean> executable,
                                       Predicate<Boolean> filter,
                                       Supplier<String> messageSupplier) {
            try {
                Assertions.assertNotNull(getInstance(sleep,
                                                     initialDelay,
                                                     defaultTimeout,
                                                     executionHandler,
                                                     delaySupplierFactory,
                                                     executable,
                                                     filter,
                                                     messageSupplier),
                                         "Should have returned a non-null instance for valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Expected to construct a PatientWaitFuture with valid arguments but throwable was caught: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("it returns successfully with valid arguments and a null message")
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testSucceedsWithNullMessage(Sleep sleep,
                                         Duration initialDelay,
                                         Duration defaultTimeout,
                                         PatientExecutionHandler executionHandler,
                                         DelaySupplierFactory delaySupplierFactory,
                                         PatientExecutable<Boolean> executable,
                                         Predicate<Boolean> filter) {
            try {
                Assertions.assertNotNull(getInstance(sleep,
                                                     initialDelay,
                                                     defaultTimeout,
                                                     executionHandler,
                                                     delaySupplierFactory,
                                                     executable,
                                                     filter,
                                                     (String) null),
                                         "Should have returned a non-null instance for valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Expected to construct a PatientWaitFuture with valid arguments but throwable was caught: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("it throws an exception for invalid arguments")
        @ArgumentsSource(InvalidArgumentsProvider.class)
        void testInvalidArgumentsThrowsException(Sleep sleep,
                                                 Duration initialDelay,
                                                 Duration defaultTimeout,
                                                 PatientExecutionHandler executionHandler,
                                                 DelaySupplierFactory delaySupplierFactory,
                                                 PatientExecutable<Boolean> executable,
                                                 Predicate<Boolean> filter,
                                                 Supplier<String> messageSupplier) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(sleep,
                                                      initialDelay,
                                                      defaultTimeout,
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
            Sleep sleep = Thread::sleep;
            Duration duration = Duration.ZERO;
            PatientExecutionHandler executionHandler = new SimpleExecutionHandler();
            DelaySupplierFactory delaySupplierFactory = new FixedDelaySupplierFactory(duration);
            PatientExecutable<Boolean> executable = () -> true;
            Predicate<Boolean> filter = bool -> null != bool && bool;
            Supplier<String> failureMessageSupplier = () -> "hello";
            PatientWaitFuture<Boolean> future = getInstance(sleep,
                                                            duration,
                                                            duration,
                                                            executionHandler,
                                                            delaySupplierFactory,
                                                            executable,
                                                            filter,
                                                            failureMessageSupplier);
            Assertions.assertAll(() -> Assertions.assertEquals(sleep, future.getSleep(), "Should return the given sleep"),
                                 () -> Assertions.assertEquals(duration, future.getInitialDelay(), "Should return the given initial delay"),
                                 () -> Assertions.assertEquals(duration, future.getDefaultTimeout(), "Should return the given default timeout"),
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
        @DisplayName("it returns value for a successful wait")
        void testGetReturnsValueWhenSuccessful() {
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            () -> true,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertEquals(true,
                                    future.get(),
                                    "Should return the expected result when successful");
        }

        @Test
        @DisplayName("it throws an exception for an unsuccessful wait")
        void testGetThrowsWhenUnsuccessful() {
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            () -> false,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientTimeoutException.class,
                                    future::get,
                                    "Should throw from get() when unsuccessful");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a null duration")
        void testGetThrowsAnExceptionIfDelaySupplierReturnsNull() {
            DelaySupplierFactory supplierFactory = () -> () -> null;
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            supplierFactory,
                                                            () -> false,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.get(Duration.ofMinutes(1)),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a negative duration")
        void testGetThrowsAnExceptionIfDelaySupplierReturnsNegative() {
            DelaySupplierFactory supplierFactory = () -> () -> Duration.ofMillis(-500);
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            supplierFactory,
                                                            () -> false,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.get(Duration.ofMinutes(1)),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it only executes once for a duration of zero")
        @SuppressWarnings("unchecked")
        void testGetWithZeroDurationOnlyExecutesOnce() throws Throwable {
            PatientExecutable<Boolean> executable = mock(PatientExecutable.class);
            when(executable.execute()).thenReturn(false);
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            executable,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientTimeoutException.class,
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
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            executable,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientTimeoutException.class,
                                    () -> future.get(Duration.ofMillis(100)),
                                    "Unsuccessful attempts should keep executing until the timeout");
            Assertions.assertTrue(counter.get() > 1,
                                  "The executable should have been executed more than once");
        }

        @Test
        @DisplayName("it stops executing when a passing value is found")
        void testGetStopsExecutingWhenValidResultIsFound() {
            AtomicInteger counter = new AtomicInteger(0);
            PatientExecutable<Boolean> executable = () -> counter.incrementAndGet() == 2;
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            executable,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            future.get(Duration.ofMillis(100));
            Assertions.assertEquals(2,
                                    counter.get(),
                                  "The executable should have stop being executed once a passing value was found");
        }

        @Test
        @DisplayName("it throws an exception if a null supplier of delay durations is returned from the factory")
        void testCheckThrowsForNullDelayDurationSupplier() {
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
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
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            handler,
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
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
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            () -> { throw new RuntimeException("whoops"); },
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::get,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception for a null duration")
        void testGetThrowsForNullDuration() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().get(null),
                                    "Should throw from get(Duration) for null duration");
        }

        @Test
        @DisplayName("it throws an exception for a negative duration")
        void testGetThrowsForNegativeDuration() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().get(Duration.ofMillis(-100)),
                                    "Should throw from get(Duration) for negative duration");
        }
    }

    @Nested
    @DisplayName("has it's check methods called")
    final class CheckTests {

        @Test
        @DisplayName("it returns true for a successful wait")
        void testCheckReturnsTrueWhenSuccessful() {
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            () -> true,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertTrue(future.check(),
                                  "Should return true when successful.");
        }

        @Test
        @DisplayName("it returns false for an unsuccessful wait")
        void testCheckReturnsFalseWhenUnsuccessful() {
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            () -> false,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertFalse(future::check,
                                   "Should return false when unsuccessful.");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a null duration")
        void testCheckThrowsAnExceptionIfDelaySupplierReturnsNull() {
            DelaySupplierFactory supplierFactory = () -> () -> null;
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            supplierFactory,
                                                            () -> false,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.check(Duration.ofMillis(100)),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it throws an exception if the delay supplier returns a negative duration")
        void testCheckThrowsAnExceptionIfDelaySupplierReturnsNegative() {
            DelaySupplierFactory supplierFactory = () -> () -> Duration.ofMillis(-500);
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            supplierFactory,
                                                            () -> false,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientException.class,
                                    () -> future.check(Duration.ofMillis(100)),
                                    "Should throw an exception if the delay supplier returns a negative duration");
        }

        @Test
        @DisplayName("it only executes once for a duration of zero")
        @SuppressWarnings("unchecked")
        void testCheckWithZeroDurationOnlyExecutesOnce() throws Throwable {
            PatientExecutable<Boolean> executable = mock(PatientExecutable.class);
            when(executable.execute()).thenReturn(false);
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
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
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            executable,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertFalse(future.check(Duration.ofMillis(100)),
                                   "Should return false for unsuccessful check.");
            Assertions.assertTrue(counter.get() > 1,
                                  "The executable should have been executed more than once");
        }

        @Test
        @DisplayName("it stops executing when a passing value is found")
        void testCheckStopsExecutingWhenValidResultIsFound() {
            AtomicInteger counter = new AtomicInteger(0);
            PatientExecutable<Boolean> executable = () -> counter.incrementAndGet() == 2;
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            executable,
                                                            bool -> null != bool && bool,
                                                            "whoops");
            future.check(Duration.ofMillis(100));
            Assertions.assertEquals(2,
                                    counter.get(),
                                    "The executable should have stop being executed once a passing value was found");
        }

        @Test
        @DisplayName("it throws an exception if a null supplier of delay durations is returned from the factory")
        void testCheckThrowsForNullDelayDurationSupplier() {
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
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
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            handler,
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
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
            PatientWaitFuture<Boolean> future = getInstance(Thread::sleep,
                                                            Duration.ZERO,
                                                            Duration.ZERO,
                                                            new SimpleExecutionHandler(),
                                                            new FixedDelaySupplierFactory(Duration.ZERO),
                                                            () -> { throw new RuntimeException("whoops"); },
                                                            bool -> null != bool && bool,
                                                            "whoops");
            Assertions.assertThrows(PatientException.class,
                                    future::check,
                                    "Should throw an exception for a delay factory that returns a null supplier");
        }

        @Test
        @DisplayName("it throws an exception for a null duration")
        void testCheckThrowsForNullDuration() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().check(null),
                                    "Should throw from get(Duration) for null duration");
        }

        @Test
        @DisplayName("it throws an exception for a negative duration")
        void testCheckThrowsForNegativeDuration() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().check(Duration.ofMillis(-100)),
                                    "Should throw from get(Duration) for negative duration");
        }
    }
}
