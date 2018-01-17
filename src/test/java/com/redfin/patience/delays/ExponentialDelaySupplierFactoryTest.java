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

package com.redfin.patience.delays;

import com.redfin.patience.DelaySupplierFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@DisplayName("When an ExponentialDelaySupplierFactory")
final class ExponentialDelaySupplierFactoryTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private ExponentialDelaySupplierFactory getInstance(int base,
                                                        Duration initialDelay) {
        return new ExponentialDelaySupplierFactory(base, initialDelay);
    }

    static final class ValidArguments
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(1, Duration.ofMillis(500)),
                             Arguments.of(2, Duration.ofMinutes(1)));
        }
    }

    static final class InvalidArguments
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(0, Duration.ofMillis(500)),
                             Arguments.of(1, Duration.ofMillis(-500)),
                             Arguments.of(1, Duration.ZERO),
                             Arguments.of(1, null));
        }
    }

    static final class ExpectedDurations
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(1, Duration.ofMillis(500), Arrays.asList(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(500))),
                             Arguments.of(2, Duration.ofSeconds(1), Arrays.asList(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(4))),
                             Arguments.of(3, Duration.ofSeconds(1), Arrays.asList(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(9))));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is constructed")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("it returns successfully for valid arguments")
        @ArgumentsSource(ValidArguments.class)
        void testCanBeCreatedWithValidArgument(int base,
                                               Duration duration) {
            try {
                Assertions.assertNotNull(getInstance(base, duration),
                                         "Should be able to create a non-null instance.");
            } catch (Throwable thrown) {
                Assertions.fail("Should be able to instantiate the object but caught the exception: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("it throws an exception for invalid arguments")
        @ArgumentsSource(InvalidArguments.class)
        void testThrowsExceptionForInvalidArgument(int base,
                                                   Duration duration) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(base, duration),
                                    "Should throw an exception when given invalid arguments.");
        }
    }

    @Nested
    @DisplayName("has the get() method called")
    final class BehaviorTests {

        @Test
        @DisplayName("it returns separate Supplier instances for each invocation")
        void testReturnsDifferentSupplierForEachCallToGet() {
            DelaySupplierFactory delaySupplier = getInstance(2, Duration.ofMillis(500));
            Assertions.assertTrue(delaySupplier.create() != delaySupplier.create(),
                                  "Separate calls to get should return different Supplier instances.");
        }

        @ParameterizedTest
        @DisplayName("it returns the expected type of Supplier")
        @ArgumentsSource(ExpectedDurations.class)
        void testReturnsExpectedSupplier(int base,
                                         Duration duration,
                                         List<Duration> expectedDurations) {
            Assumptions.assumeTrue(null != expectedDurations && !expectedDurations.isEmpty(),
                                   "Should be given at least 1 expected result duration.");
            Supplier<Duration> supplier = getInstance(base, duration).create();
            Assertions.assertAll(expectedDurations.stream()
                                                  .map(d -> (Executable) () -> Assertions.assertEquals(d,
                                                                                                       supplier.get(),
                                                                                                       "Duration supplier should return the expected durations."))
                                                  .toArray(Executable[]::new));
        }
    }
}
