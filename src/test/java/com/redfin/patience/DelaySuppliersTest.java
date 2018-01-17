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
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@DisplayName("DelaySuppliers")
final class DelaySuppliersTest
 implements NonInstantiableContract<DelaySuppliers> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Class<DelaySuppliers> getClassObject_NonInstantiableContract() {
        return DelaySuppliers.class;
    }

    private static final class ValidFixedArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(Duration.ZERO, Arrays.asList(Duration.ZERO, Duration.ZERO, Duration.ZERO)),
                             Arguments.of(Duration.ofMillis(500), Arrays.asList(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(500))));
        }
    }

    private static final class InvalidFixedArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((Duration) null),
                             Arguments.of(Duration.ofMillis(-500)));
        }
    }

    private static final class ValidExponentialArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(1, Duration.ofMillis(500), Arrays.asList(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(500))),
                             Arguments.of(2, Duration.ofSeconds(1), Arrays.asList(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(4))),
                             Arguments.of(3, Duration.ofSeconds(1), Arrays.asList(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(9))));
        }
    }

    private static final class InvalidExponentialArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(-1, null),
                             Arguments.of(-1, Duration.ofMillis(-500)),
                             Arguments.of(-1, Duration.ZERO),
                             Arguments.of(-1, Duration.ofMillis(500)),
                             Arguments.of(0, null),
                             Arguments.of(0, Duration.ofMillis(-500)),
                             Arguments.of(0, Duration.ZERO),
                             Arguments.of(0, Duration.ofMillis(500)),
                             Arguments.of(1, null),
                             Arguments.of(1, Duration.ofMillis(-500)),
                             Arguments.of(1, Duration.ZERO));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("when fixed(Duration) is called")
    final class FixedTests {

        @ParameterizedTest
        @DisplayName("it throws an exception for an invalid argument")
        @ArgumentsSource(InvalidFixedArgumentsProvider.class)
        void testThrowsWithInvalidArguments(Duration duration) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> DelaySuppliers.fixed(duration),
                                    "Should throw an exception for an invalid argument.");
        }

        @ParameterizedTest
        @DisplayName("it returns a non-null delay supplier for a valid argument")
        @ArgumentsSource(ValidFixedArgumentsProvider.class)
        void testReturnsSuccessfullyWithValidArguments(Duration duration) {
            Assertions.assertNotNull(DelaySuppliers.fixed(duration),
                                     "Should return a non-null delay supplier for a valid argument.");
        }

        @ParameterizedTest
        @DisplayName("it returns a delay supplier that returns the expected duration supplier")
        @ArgumentsSource(ValidFixedArgumentsProvider.class)
        void testReturnsExpectedDelaySupplier(Duration duration,
                                              List<Duration> expectedSuppliedDurations) {
            Assumptions.assumeTrue(null != expectedSuppliedDurations && !expectedSuppliedDurations.isEmpty(),
                                   "Should have received a non-null and non-empty list of expected durations.");
            Supplier<Duration> supplier = DelaySuppliers.fixed(duration).create();
            Assertions.assertAll(expectedSuppliedDurations.stream().map(next -> () -> Assertions.assertEquals(next, supplier.get())));
        }
    }

    @Nested
    @DisplayName("when exponential(int, Duration) is called")
    final class ExponentialTests {

        @ParameterizedTest
        @DisplayName("it throws an exception for an invalid argument")
        @ArgumentsSource(InvalidExponentialArgumentsProvider.class)
        void testThrowsWithInvalidArguments(int base,
                                            Duration duration) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> DelaySuppliers.exponential(base, duration),
                                    "Should throw an exception for an invalid argument.");
        }

        @ParameterizedTest
        @DisplayName("it returns a non-null delay supplier for a valid argument")
        @ArgumentsSource(ValidExponentialArgumentsProvider.class)
        void testReturnsSuccessfullyWithValidArguments(int base,
                                                       Duration duration) {
            Assertions.assertNotNull(DelaySuppliers.exponential(base, duration),
                                     "Should return a non-null delay supplier for a valid argument.");
        }

        @ParameterizedTest
        @DisplayName("it returns a delay supplier that returns the expected duration supplier")
        @ArgumentsSource(ValidExponentialArgumentsProvider.class)
        void testReturnsExpectedDelaySupplier(int base,
                                              Duration duration,
                                              List<Duration> expectedSuppliedDurations) {
            Assumptions.assumeTrue(null != expectedSuppliedDurations && !expectedSuppliedDurations.isEmpty(),
                                   "Should have received a non-null and non-empty list of expected durations.");
            Supplier<Duration> supplier = DelaySuppliers.exponential(base, duration).create();
            Assertions.assertAll(expectedSuppliedDurations.stream().map(next -> () -> Assertions.assertEquals(next, supplier.get())));
        }
    }
}
