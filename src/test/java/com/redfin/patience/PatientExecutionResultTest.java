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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

@DisplayName("When a PatientExecutionResult")
final class PatientExecutionResultTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final class ValidSuccessArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((String) null),
                             Arguments.of(true),
                             Arguments.of(false),
                             Arguments.of(1),
                             Arguments.of("hello"));
        }
    }

    private static final class ValidFailureArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // First argument is the failed attempt description to be given
            // and the second argument is the expected description to be returned
            String defaultDescription = "Failed execution attempt";
            return Stream.of(Arguments.of(null, defaultDescription),
                             Arguments.of("", defaultDescription),
                             Arguments.of("hello", "hello"));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is created")
    final class FactoryTests {

        @ParameterizedTest
        @DisplayName("as a passing result, it returns successfully")
        @ArgumentsSource(ValidSuccessArguments.class)
        void testPassResultReturnsSuccessfully(Object result) {
            Assertions.assertNotNull(PatientExecutionResult.pass(result),
                                     "Should successfully return a non-null result for any successful value.");
        }

        @ParameterizedTest
        @DisplayName("as a failure result, it returns successfully")
        @ArgumentsSource(ValidFailureArguments.class)
        void testFailResultReturnsSuccessfully(String failureReason) {
            Assertions.assertNotNull(PatientExecutionResult.fail(failureReason),
                                     "Should successfully return a non-null result for any failure description.");
        }
    }

    @Nested
    @DisplayName("is a successful result")
    final class SuccessfulResultTests {

        @ParameterizedTest
        @DisplayName("it returns true for isSuccess()")
        @ArgumentsSource(ValidSuccessArguments.class)
        void testPassingResultReturnsTrueForIsSuccess(Object result) {
            Assertions.assertTrue(PatientExecutionResult.pass(result)
                                                        .isSuccess(),
                                  "A passing execution result should return true for isSuccess()");
        }

        @ParameterizedTest
        @DisplayName("it returns the given result")
        @ArgumentsSource(ValidSuccessArguments.class)
        void testPassingResultReturnsGivenResult(Object result) {
            Assertions.assertEquals(result,
                                    PatientExecutionResult.pass(result).getResult(),
                                    "A passing execution result should return it's given result.");
        }

        @Test
        @DisplayName("it throws an exception from getFailedAttemptDescription()")
        void testPassingResultThrowsForGetFailureDescription() {
            Assertions.assertThrows(UnsupportedOperationException.class,
                                    () -> PatientExecutionResult.pass("hello")
                                                                .getFailedAttemptDescription(),
                                    "A passing execution result should throw for getFailedAttemptDescription()");
        }
    }

    @Nested
    @DisplayName("is a successful result")
    final class FailureResultTests {

        @ParameterizedTest
        @DisplayName("it returns the expected String from getFailedAttemptDescription()")
        @ArgumentsSource(ValidFailureArguments.class)
        void testFailingResultReturnsExpectedDescription(String description,
                                                         String expectedResult) {
            Assertions.assertEquals(expectedResult,
                                    PatientExecutionResult.fail(description).getFailedAttemptDescription(),
                                    "A PatientExecutionResult should return the expected description for getFailedAttemptDescription().");
        }

        @Test
        @DisplayName("it returns false from isSuccess()")
        void testFailingResultReturnsFalseForIsSuccess() {
            Assertions.assertFalse(PatientExecutionResult.fail("failure")
                                                         .isSuccess(),
                                   "A failing execution result should return false for isSuccess()");
        }

        @Test
        @DisplayName("it throws an exception from getResult()")
        void testFailingResultThrowsForGetResult() {
            Assertions.assertThrows(UnsupportedOperationException.class,
                                    () -> PatientExecutionResult.fail("hello")
                                                                .getResult(),
                                    "A failing execution result should throw for getResult()");
        }
    }
}
