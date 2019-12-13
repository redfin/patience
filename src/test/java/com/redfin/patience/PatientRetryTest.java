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
import java.util.stream.Stream;

@DisplayName("When a PatientRetry")
public class PatientRetryTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final PatientSleep SLEEP;
    private static final Duration POSITIVE_DURATION;
    private static final Duration NEGATIVE_DURATION;
    private static final int NUMBER_OF_RETRIES;
    private static final PatientExecutionHandler EXECUTION_HANDLER;
    private static final PatientDelaySupplierFactory DELAY_SUPPLIER_FACTORY;

    static {
        SLEEP = Thread::sleep;
        POSITIVE_DURATION = Duration.ofMillis(500);
        NEGATIVE_DURATION = Duration.ofMillis(-500);
        NUMBER_OF_RETRIES = 0;
        EXECUTION_HANDLER = PatientExecutionHandlers.simple();
        DELAY_SUPPLIER_FACTORY = new FixedPatientDelaySupplierFactory(Duration.ofMillis(500));
    }

    private PatientRetry getInstance() {
        return getInstance(SLEEP, POSITIVE_DURATION, NUMBER_OF_RETRIES, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY);
    }

    private PatientRetry getInstance(PatientSleep sleep,
                                     Duration initialDelay,
                                     int defaultNumberOfRetries,
                                     PatientExecutionHandler executionHandler,
                                     PatientDelaySupplierFactory delaySupplierFactory) {
        return new PatientRetry(sleep,
                                initialDelay,
                                defaultNumberOfRetries,
                                executionHandler,
                                delaySupplierFactory);
    }


    private static final class ValidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(SLEEP, Duration.ZERO, NUMBER_OF_RETRIES, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY),
                             Arguments.of(SLEEP, Duration.ofMillis(500), NUMBER_OF_RETRIES, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY),
                             Arguments.of(SLEEP, Duration.ZERO, 1, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY),
                             Arguments.of(SLEEP, Duration.ofMillis(500), 2, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY));
        }
    }

    private static final class InvalidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // Invalid sleep
            return Stream.of(Arguments.of(null, Duration.ofMillis(500), NUMBER_OF_RETRIES, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY),
                             // Invalid initial delays
                             Arguments.of(SLEEP, null, NUMBER_OF_RETRIES, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY),
                             Arguments.of(SLEEP, Duration.ofMillis(-500), NUMBER_OF_RETRIES, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY),
                             // Invalid number of retries
                             Arguments.of(SLEEP, Duration.ofMillis(500), -1, EXECUTION_HANDLER, DELAY_SUPPLIER_FACTORY),
                             // Invalid execution handler
                             Arguments.of(SLEEP, Duration.ofMillis(500), NUMBER_OF_RETRIES, null, DELAY_SUPPLIER_FACTORY),
                             // Invalid delay supplier factory
                             Arguments.of(SLEEP, Duration.ofMillis(500), NUMBER_OF_RETRIES, EXECUTION_HANDLER, null));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is constructed")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("it returns a non-null instance successfully for valid arguments")
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testCanInstantiateWithValidArguments(PatientSleep sleep,
                                                  Duration initialDelay,
                                                  int defaultNumberOfRetries,
                                                  PatientExecutionHandler handler,
                                                  PatientDelaySupplierFactory delaySupplierFactory) {
            try {
                Assertions.assertNotNull(getInstance(sleep, initialDelay, defaultNumberOfRetries, handler, delaySupplierFactory),
                                         "Should have received a non-null instance with valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Should have been able to construct a PatientRetry with valid arguments but caught throwable: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("it throws an exception for invalid arguments")
        @ArgumentsSource(PatientRetryTest.InvalidArgumentsProvider.class)
        void testThrowsExceptionWithInvalidArguments(PatientSleep sleep,
                                                     Duration initialDelay,
                                                     int defaultNumberOfRetries,
                                                     PatientExecutionHandler handler,
                                                     PatientDelaySupplierFactory delaySupplierFactory) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(sleep, initialDelay, defaultNumberOfRetries, handler, delaySupplierFactory),
                                    "Should have thrown an exception when the constructor is called with an invalid argument.");
        }
    }

    @Nested
    @DisplayName("has it's getter methods called")
    final class GetterTests {

        @ParameterizedTest
        @DisplayName("it returns the given arguments")
        @ArgumentsSource(PatientRetryTest.ValidArgumentsProvider.class)
        void testCanInstantiateWithValidArguments(PatientSleep sleep,
                                                  Duration initialDelay,
                                                  int defaultNumberOfRetries,
                                                  PatientExecutionHandler handler,
                                                  PatientDelaySupplierFactory delaySupplierFactory) {
            PatientRetry retry = getInstance(sleep, initialDelay, defaultNumberOfRetries, handler, delaySupplierFactory);
            Assertions.assertAll(() -> Assertions.assertEquals(sleep, retry.getSleep(), "Should return the given sleep"),
                                 () -> Assertions.assertEquals(initialDelay, retry.getInitialDelay(), "Should return the given initial delay"),
                                 () -> Assertions.assertEquals(defaultNumberOfRetries, retry.getDefaultNumberOfRetries(), "Should return the given default timeout"),
                                 () -> Assertions.assertEquals(handler, retry.getExecutionHandler(), "Should return the given execution handler"),
                                 () -> Assertions.assertEquals(delaySupplierFactory, retry.getDelaySupplierFactory(), "Should return the given delay supplier factory"));
        }
    }

    @Nested
    @DisplayName("has the from(PatientExecutable) method called")
    final class FromTests {

        @Test
        @DisplayName("it returns a non-null future for a non-null PatientExecutable")
        void testFromReturnsNonNull() {
            Assertions.assertNotNull(getInstance().from(() -> true),
                                     "Should return a non-null future with from(PatientExecutable) call.");
        }

        @Test
        @DisplayName("it throws an exception for a null PatientExecutable")
        void testFromThrowsForNullExecutable() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().from(null),
                                    "Should throw for a null executable to from(PatientExecutable)");
        }
    }

    @Nested
    @DisplayName("has the getDefaultFilter() method called")
    final class FilterTests {

        @Test
        @DisplayName("it returns a filter that returns true for true")
        void testDefaultFilterReturnsTrueForTrue() {
            Assertions.assertTrue(PatientRetry.getDefaultFilter().test(true),
                                  "Default filter should return true for a true value.");
        }

        @Test
        @DisplayName("it returns a filter that returns false for false")
        void testDefaultFilterReturnsFalseForFalse() {
            Assertions.assertFalse(PatientRetry.getDefaultFilter().test(false),
                                   "Default filter should return false for a false value.");
        }

        @Test
        @DisplayName("it returns a filter that returns true for non-null")
        void testDefaultFilterReturnsTrueForNonNull() {
            Assertions.assertTrue(PatientRetry.getDefaultFilter().test("hello"),
                                  "Default filter should return true for a non-null, non-boolean value.");
        }

        @Test
        @DisplayName("it returns a filter that returns false for null")
        void testDefaultFilterReturnsFalseForNull() {
            Assertions.assertFalse(PatientRetry.getDefaultFilter().test(null),
                                   "Default filter should return false for a null value.");
        }
    }

    @Nested
    @DisplayName("Builder has been created")
    final class BuilderTests {

        @Test
        @DisplayName("it returns a non-null builder instance")
        void testBuilderReturnsNonNull() {
            Assertions.assertNotNull(PatientRetry.builder(),
                                     "PatientRetry call to builder should return non-null Builder.");
        }

        @Test
        @DisplayName("it throws an exception for a null sleep")
        void testBuilderThrowsForNullSleep() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> PatientRetry.builder()
                                                      .withSleep(null),
                                    "PatientRetry builder should throw for null sleep.");
        }

        @Test
        @DisplayName("it throws an exception for a null initial delay duration")
        void testBuilderThrowsForNullInitialDelay() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> PatientRetry.builder()
                                                      .withInitialDelay(null),
                                    "PatientRetry builder should throw for null initial delay.");
        }

        @Test
        @DisplayName("it throws an exception for a negative initial delay duration")
        void testBuilderThrowsForNegativeInitialDelay() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> PatientRetry.builder()
                                                      .withInitialDelay(NEGATIVE_DURATION),
                                    "PatientRetry builder should throw for negative initial delay.");
        }

        @Test
        @DisplayName("it throws an exception for a negative default number of retries")
        void testBuilderThrowsForNegativeDefaultTimeout() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> PatientRetry.builder()
                                                      .withDefaultNumberOfRetries(-1),
                                    "PatientRetry builder should throw for negative default number of retries.");
        }

        @Test
        @DisplayName("it throws an exception for a null execution handler")
        void testBuilderThrowsForExecutionHandler() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> PatientRetry.builder()
                                                      .withExecutionHandler(null),
                                    "PatientRetry builder should throw for null execution handler.");
        }

        @Test
        @DisplayName("it throws an exception for a null initial delay duration")
        void testBuilderThrowsForDelaySupplierFactory() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> PatientRetry.builder()
                                                      .withDelaySupplier(null),
                                    "PatientRetry builder should throw for null delay supplier.");
        }

        @Test
        @DisplayName("should return a non-null PatientRetry when the build() method is called")
        void testBuilderBuildReturnsNonNull() {
            Assertions.assertNotNull(PatientRetry.builder().build(),
                                     "PatientRetry builder call to build should return non-null PatientRetry.");
        }

        @Test
        @DisplayName("should return a PatientRetry when the build() method is called that has the given arguments")
        void testBuilderBuildReturnsWaitWithGivenValues() {
            PatientRetry retry = PatientRetry.builder()
                                             .withSleep(SLEEP)
                                             .withInitialDelay(POSITIVE_DURATION)
                                             .withDefaultNumberOfRetries(10)
                                             .withExecutionHandler(EXECUTION_HANDLER)
                                             .withDelaySupplier(DELAY_SUPPLIER_FACTORY)
                                             .build();
            Assertions.assertAll(() -> Assertions.assertEquals(SLEEP, retry.getSleep(), "Should have the given Sleep"),
                                 () -> Assertions.assertEquals(POSITIVE_DURATION, retry.getInitialDelay(), "Should have the given initial delay"),
                                 () -> Assertions.assertEquals(10, retry.getDefaultNumberOfRetries(), "Should have the given default number of retries"),
                                 () -> Assertions.assertEquals(EXECUTION_HANDLER, retry.getExecutionHandler(), "Should have the given execution handler"),
                                 () -> Assertions.assertEquals(DELAY_SUPPLIER_FACTORY, retry.getDelaySupplierFactory(), "Should have the given delay supplier factory"));
        }

        @Test
        @DisplayName("should return a PatientRetry when the build() method is called without setting values that has the expected default arguments")
        void testBuilderCreatesPatientRetryWithExpectedDefaults() {
            PatientRetry wait = PatientRetry.builder().build();
            Assertions.assertAll(() -> Assertions.assertEquals(Duration.ZERO, wait.getInitialDelay(), "Should have the default initial delay"),
                                 () -> Assertions.assertEquals(0, wait.getDefaultNumberOfRetries(), "Should have the expected default number of retries"),
                                 () -> Assertions.assertTrue(wait.getExecutionHandler() instanceof SimplePatientExecutionHandler, "Should have the default execution handler"),
                                 () -> Assertions.assertTrue(wait.getDelaySupplierFactory() instanceof FixedPatientDelaySupplierFactory, "Should have the default type of delay supplier factory"),
                                 () -> Assertions.assertEquals(Duration.ZERO, wait.getDelaySupplierFactory().create().get(), "The delay supplier factory should return the default wait time"));
        }
    }
}
