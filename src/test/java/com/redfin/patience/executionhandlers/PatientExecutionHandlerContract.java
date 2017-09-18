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

package com.redfin.patience.executionhandlers;

import com.redfin.patience.PatientExecutionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * A test contract for any implementation of a {@link PatientExecutionHandler}.
 *
 * @param <T> the type of the concrete implementation being tested.
 */
interface PatientExecutionHandlerContract<T extends PatientExecutionHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return an instance of the {@link PatientExecutionHandler}
     * implementation that is being tested.
     */
    T getInstance();

    Callable<String> CALLABLE = () -> "hello";
    Predicate<String> PASSING_FILTER = str -> null != str && str.length() > 1;
    Predicate<String> FAILING_FILTER = str -> null != str && str.length() < 2;

    final class ContractUncheckedException extends RuntimeException {
        ContractUncheckedException() {
            super();
        }
    }

    final class ContractCheckedException extends Exception {
        ContractCheckedException() {
            super();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    default void testCanConstructInstance_PatientExecutionHandlerContract() {
        Assertions.assertNotNull(getInstance(),
                                 "Should be able to construct an instance of a PatientExecutionHandler");
    }

    @Test
    default void testHandlerThrowsExceptionForNullCallable_PatientExecutionHandlerContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(null, PASSING_FILTER));
    }

    @Test
    default void testHandlerThrowsExceptionForNullFilter_PatientExecutionHandlerContract() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().execute(CALLABLE, null));
    }

    @Test
    default void testHandlerReturnsNonNullResultForValidArguments_PatientExecutorHandlerContract() {
        Assertions.assertNotNull(getInstance().execute(CALLABLE, PASSING_FILTER),
                                 "A PatientExecutionHandler should return a non null result for a passing value");
        Assertions.assertNotNull(getInstance().execute(CALLABLE, FAILING_FILTER),
                                 "A PatientExecutionHandler should return a non null result for a failing value");
    }

    @Test
    default void testHandlerReturnsSuccessfulResultForPassingValues_PatientExecutionHandlerContract() {
        Assertions.assertTrue(getInstance().execute(CALLABLE, PASSING_FILTER).wasSuccessful(),
                              "A PatientExecutionHandler should return a valid result for passing arguments");
        Assertions.assertEquals(getInstance().execute(CALLABLE, PASSING_FILTER).getSuccessResult(),
                                "hello",
                                "A PatientExecutionHandler should return the expected result value");
    }

    @Test
    default void testHandlerReturnsUnsuccessfulResultForFailingValues_PatientExecutionHandlerContract() {
        Assertions.assertFalse(getInstance().execute(CALLABLE, FAILING_FILTER).wasSuccessful(),
                               "A PatientExecutionHandler should return an invalid result for failing arguments");
    }
}
