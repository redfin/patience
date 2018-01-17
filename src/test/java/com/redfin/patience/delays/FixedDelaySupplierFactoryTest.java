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
import java.util.function.Supplier;
import java.util.stream.Stream;

@DisplayName("When a FixedDelaySupplierFactory")
final class FixedDelaySupplierFactoryTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private FixedDelaySupplierFactory getInstance(Duration duration) {
        return new FixedDelaySupplierFactory(duration);
    }

    static final class ValidArguments
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(Duration.ZERO),
                             Arguments.of(Duration.ofDays(1)));
        }
    }

    static final class InvalidArguments
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((Duration) null),
                             Arguments.of(Duration.ofMillis(-500)));
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
        void testCanBeCreatedWithValidArgument(Duration duration) {
            try {
                Assertions.assertNotNull(getInstance(duration),
                                         "Should be able to create a non-null instance.");
            } catch (Throwable thrown) {
                Assertions.fail("Should be able to instantiate the object but caught the exception: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("it throws an exception for invalid arguments")
        @ArgumentsSource(InvalidArguments.class)
        void testThrowsExceptionForInvalidArgument(Duration duration) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(duration),
                                    "Should throw an exception when given an invalid duration.");
        }
    }

    @Nested
    @DisplayName("has the get() method called")
    final class BehaviorTests {

        @Test
        @DisplayName("it returns separate Supplier instances for each invocation")
        void testReturnsDifferentSupplierForEachCallToGet() {
            DelaySupplierFactory delaySupplier = getInstance(Duration.ofMillis(500));
            Assertions.assertTrue(delaySupplier.create() != delaySupplier.create(),
                                  "Separate calls to get should return different Supplier instances.");
        }

        @Test
        @DisplayName("it returns the expected type of Supplier")
        void testReturnsExpectedSupplier() {
            Duration duration = Duration.ofMinutes(3);
            Supplier<Duration> durationSupplier = getInstance(duration).create();
            Executable executable = () -> Assertions.assertEquals(duration,
                                                                  durationSupplier.get(),
                                                                  "Expected to receive the given duration");
            Assertions.assertAll(executable,
                                 executable,
                                 executable);
        }
    }
}
