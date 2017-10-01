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

import com.redfin.patience.PatientExecutionException;
import com.redfin.patience.PatientExecutionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Predicate;

interface PatientExecutionHandlerContract<T extends PatientExecutionHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    T getInstance();

    default <E> Predicate<E> getNonNullPredicate() {
        return Objects::nonNull;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    default void testCanInstantiate_PatientExecutionHandlerContract() {
        Assertions.assertNotNull(getInstance(),
                                 "Should be able to instantiate the execution handler");
    }

    @Test
    default void testThrowsForNullExecutable_PatientExecutionHandlerContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(null,
                                                            getNonNullPredicate()),
                                 "Should throw for a null executable");
    }

    @Test
    default void testThrowsForNullFilter_PatientExecutionHandlerContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(() -> true,
                                                            null),
                                "Should throw for a null filter");
    }

    @Test
    default void testThrowsForUnexpectedThrowableFromExecutable_PatientExecutionHandlerContract() {
        Assertions.assertThrows(PatientExecutionException.class,
                                () -> getInstance().execute(() -> { throw new AssertionError("Whoops"); },
                                                            getNonNullPredicate()),
                                "Should throw for an unexpected throwable from executable execution");
    }

    @Test
    default void testThrowsForUnexpectedThrowableFromFilter_PatientExecutionHandlerContract() {
        Assertions.assertThrows(PatientExecutionException.class,
                                () -> getInstance().execute(() -> true,
                                                            bool -> { throw new AssertionError("whoops"); }),
                                "Should throw for an unexpected throwable from executable execution");
    }

    @Test
    default void testReturnsSuccessfulForPassingValue_PatientExecutionHandlerContract() {
        Assertions.assertTrue(getInstance().execute(() -> true,
                                                    bool -> bool)
                                           .isSuccess(),
                              "A passing test should return a successful result");
    }

    @Test
    default void testReturnsFailureForNonPassingValue_PatientExecutionHandlerContract() {
        Assertions.assertFalse(getInstance().execute(() -> false,
                                                     bool -> bool)
                                            .isSuccess(),
                              "A failing test should return an unsuccessful result");
    }
}
