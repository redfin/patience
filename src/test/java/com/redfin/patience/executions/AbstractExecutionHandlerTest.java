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

package com.redfin.patience.executions;

import com.redfin.patience.PatientExecutionHandler;
import com.redfin.patience.exceptions.PatientExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Predicate;

abstract class AbstractExecutionHandlerTest<T extends PatientExecutionHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    protected abstract T getInstance();

    final <E> Predicate<E> getNonNullPredicate() {
        return Objects::nonNull;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is executing")
    final class BehaviorTests {

        @Test
        @DisplayName("it throws an exception for a null executable")
        void testThrowsForNullExecutable() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().execute(null,
                                                                getNonNullPredicate()),
                                    "Should throw for a null executable");
        }

        @Test
        @DisplayName("it throws an exception for a null filter")
        void testThrowsForNullFilter() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().execute(() -> true,
                                                                null),
                                    "Should throw for a null filter");
        }

        @Test
        @DisplayName("it throws an exception for an unexpected throwable from the executable")
        void testThrowsForUnexpectedThrowableFromExecutable() {
            Assertions.assertThrows(PatientExecutionException.class,
                                    () -> getInstance().execute(() -> {
                                                                    throw new AssertionError("Whoops");
                                                                },
                                                                getNonNullPredicate()),
                                    "Should throw for an unexpected throwable from executable execution");
        }

        @Test
        @DisplayName("it throws an exception for an unexpected throwable from the filter")
        void testThrowsForUnexpectedThrowableFromFilter() {
            Assertions.assertThrows(PatientExecutionException.class,
                                    () -> getInstance().execute(() -> true,
                                                                bool -> {
                                                                    throw new AssertionError("whoops");
                                                                }),
                                    "Should throw for an unexpected throwable from executable execution");
        }

        @Test
        @DisplayName("it returns a success for a passing value")
        void testReturnsSuccessfulForPassingValue() {
            Assertions.assertTrue(getInstance().execute(() -> true,
                                                        bool -> bool)
                                               .isSuccess(),
                                  "A passing test should return a successful result");
        }

        @Test
        @DisplayName("it returns a failure for a non-passing value")
        void testReturnsFailureForNonPassingValue() {
            Assertions.assertFalse(getInstance().execute(() -> false,
                                                         bool -> bool)
                                                .isSuccess(),
                                   "A failing test should return an unsuccessful result");
        }
    }
}
