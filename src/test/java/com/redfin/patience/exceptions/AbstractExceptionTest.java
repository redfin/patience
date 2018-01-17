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

import java.util.function.Supplier;
import java.util.stream.Stream;

abstract class AbstractExceptionTest<X extends Throwable> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    protected abstract X getInstance();

    protected abstract X getInstance(String message);

    protected abstract X getInstance(Throwable cause);

    protected abstract X getInstance(String message,
                                     Throwable cause);

    protected abstract Class<X> getClassUnderTest();

    private static final class ValidMessageArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((String) null),
                             Arguments.of(""),
                             Arguments.of("hello"));
        }
    }

    private static final class ValidCauseArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((Throwable) null),
                             Arguments.of(new RuntimeException()),
                             Arguments.of(new AssertionError("whoops")));
        }
    }

    private static final class ValidMessageAndCauseArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, null),
                             Arguments.of("", null),
                             Arguments.of("hello", null),
                             Arguments.of(null, new RuntimeException()),
                             Arguments.of("", new RuntimeException()),
                             Arguments.of("hello", new RuntimeException()));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is constructed")
    final class ConstructorTests {

        @Test
        @DisplayName("it returns successfully with no arguments")
        void testCanBeConstructedWithNoArgs() {
            testInstantiation(AbstractExceptionTest.this::getInstance);
        }

        @ParameterizedTest(name = "with message: {0}")
        @DisplayName("it returns successfully for a message argument")
        @ArgumentsSource(ValidMessageArguments.class)
        void testCanBeConstructedWithMessageArg(String message) {
            testInstantiation(() -> getInstance(message));
        }

        @ParameterizedTest(name = "with cause: {0}")
        @DisplayName("it returns successfully for a cause argument")
        @ArgumentsSource(ValidCauseArguments.class)
        void testCanBeConstructedWithCauseArg(Throwable cause) {
            testInstantiation(() -> getInstance(cause));
        }

        @ParameterizedTest(name = "with message: {0}, and cause: {1}")
        @DisplayName("it returns successfully for a message and cause argument")
        @ArgumentsSource(ValidMessageAndCauseArguments.class)
        void testCanBeConstructedWithMessageAndCauseArgs(String message,
                                                         Throwable cause) {
            testInstantiation(() -> getInstance(message, cause));
        }

        private void testInstantiation(Supplier<X> supplier) {
            try {
                Assertions.assertNotNull(supplier.get(),
                                         "Should be able to instantiate an exception of type: " + getClassUnderTest().getName());
            } catch (Throwable thrown) {
                Assertions.fail(String.format("Expected to successfully create the exception of type %s but caught throwable %s",
                                              getClassUnderTest().getName(),
                                              thrown));
            }
        }
    }

    @Nested
    @DisplayName("has been created")
    final class BehaviorTests {

        @ParameterizedTest(name = "with message: {0}, and cause: {1}")
        @DisplayName("it returns the given message")
        @ArgumentsSource(ValidMessageAndCauseArguments.class)
        void testReturnsGivenMessage(String message, Throwable cause) {
            Assertions.assertEquals(message,
                                    getInstance(message, cause).getMessage(),
                                    "An exception should return it's given message.");
        }

        @ParameterizedTest(name = "with message: {0}, and cause: {1}")
        @DisplayName("it returns the given cause")
        @ArgumentsSource(ValidMessageAndCauseArguments.class)
        void testReturnsGivenCause(String message, Throwable cause) {
            Assertions.assertEquals(cause,
                                    getInstance(message, cause).getCause(),
                                    "An exception should return it's given cause.");
        }
    }
}
