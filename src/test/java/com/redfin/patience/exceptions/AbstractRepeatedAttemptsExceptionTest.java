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

package com.redfin.patience.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

abstract class AbstractRepeatedAttemptsExceptionTest<X extends AbstractRepeatedAttemptsException> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    protected abstract X getInstance(String message,
                                     List<String> failedAttemptsDescription);

    private static final class ValidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, Collections.singletonList("hello")),
                             Arguments.of("", Arrays.asList("hello", "world")),
                             Arguments.of("hello", Collections.singletonList("world")));
        }
    }

    private static final class InValidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello", null),
                             Arguments.of("hello", Collections.emptyList()));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is constructed")
    final class ConstructorTest {

        @ParameterizedTest(name = "with message: <{0}>, and list: {1}")
        @ArgumentsSource(ValidArgumentsProvider.class)
        @DisplayName("it returns successfully with valid arguments")
        void testCanBeInstantiatedWithValidArguments(String message,
                                                     List<String> failedAttemptsDescriptions) {
            try {
                Assertions.assertNotNull(getInstance(message,
                                                     failedAttemptsDescriptions),
                                         "Sending valid arguments to the constructor should have resulted in a non-null exception being returned.");
            } catch (Throwable thrown) {
                Assertions.fail("Should have been able to instantiate the exception but caught the throwable: " + thrown);
            }
        }

        @ParameterizedTest(name = "with message: <{0}>, and list: {1}")
        @ArgumentsSource(InValidArgumentsProvider.class)
        @DisplayName("it throws an exception for invalid arguments")
        void testThrowsForInvalidArguments(String message,
                                           List<String> failedAttemptsDescriptions) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(message, failedAttemptsDescriptions),
                                    "Should thrown an IllegalArgumentException for invalid arguments.");
        }
    }

    @Nested
    @DisplayName("has been created")
    final class BehaviorTests {

        @Test
        @DisplayName("it returns the given message")
        void testReturnsGivenMessage() {
            String message = "hello";
            Assertions.assertEquals(message,
                                    getInstance(message, Collections.singletonList("world")).getMessage(),
                                    "Should return the given message.");
        }

        @Test
        @DisplayName("it eturns the given failed attempts count")
        void testReturnsGivenAttemptsCount() {
            Assertions.assertEquals(2,
                                    getInstance("hello", Arrays.asList("world", "unit")).getFailedAttemptsCount(),
                                    "Should return the expected number of failed attempts.");
        }

        @Test
        @DisplayName("it returns the expected failed attempts list")
        void testReturnsExpectedAttemptsList() {
            List<String> failedAttempts = Arrays.asList("hello", "world", "how");
            Assertions.assertEquals(failedAttempts,
                                    getInstance("hello", failedAttempts).getFailedAttemptsDescriptions(),
                                    "Should return the expected list of failed attempts descriptions.");
        }

        @Test
        @DisplayName("it returns an unmodifiable failed attempts list")
        void testReturnsUnmodifiableAttemptsList() {
            Assertions.assertThrows(UnsupportedOperationException.class,
                                    () -> getInstance("hello", Collections.singletonList("world")).getFailedAttemptsDescriptions().add("how are you?"),
                                    "Should return an unmodifiable list of failed attempts descriptions.");
        }
    }
}
