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

import com.redfin.patience.PatientExecutionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

final class AbstractPatientExecutionHandlerTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final Callable<String> CALLABLE = () -> "hello";
    private static final Predicate<String> PASSING_FILTER = t -> t.length() > 1;
    private static final Predicate<String> FAILING_FILTER = String::isEmpty;

    private AbstractPatientExecutionHandler getHandler() {
        return new AbstractPatientExecutionHandler() {
            @Override
            public <T> PatientExecutionResult<T> execute(Callable<T> callable, Predicate<T> filter) {
                // Not implemented since it is not needed for these tests
                return null;
            }
        };
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testExecuteHelperDoesNotCheckCallable() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> getHandler().executeHelper(null, PASSING_FILTER));
    }

    @Test
    void testExecuteHelperDoesNotCheckFilter() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> getHandler().executeHelper(CALLABLE, null));
    }

    @Test
    void testExecuteHelperDoesNotHandleUncheckedExceptionsFromCallable() {
        Assertions.assertThrows(PatientExecutionHandlerContract.ContractUncheckedException.class,
                                () -> getHandler().executeHelper(() -> {
                                    throw new PatientExecutionHandlerContract.ContractUncheckedException();
                                }, PASSING_FILTER));
    }

    @Test
    void testExecuteHelperDoesNotHandleCheckedExceptionsFromCallable() {
        Assertions.assertThrows(PatientExecutionHandlerContract.ContractCheckedException.class,
                                () -> getHandler().executeHelper(() -> {
                                    throw new PatientExecutionHandlerContract.ContractCheckedException();
                                }, PASSING_FILTER));
    }

    @Test
    void testExecuteHelperDoesNotHandleExceptionsFromPredicate() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> getHandler().executeHelper(() -> null, PASSING_FILTER));
    }

    @Test
    void testExecuteHelperReturnsSuccessfulResultForPassingValue() throws Exception {
        PatientExecutionResult<String> result = getHandler().executeHelper(CALLABLE, PASSING_FILTER);
        Assertions.assertNotNull(result,
                                 "AbstractPatientExecutionHandler executeHelper shouldn't return a null result");
        Assertions.assertTrue(result.isPresent(),
                              "AbstractPatientExecutionHandler executeHelper should return a successful result for a passing filter");
        Assertions.assertEquals("hello",
                                result.get(),
                                "AbstractPatientExecutionHandler executeHelper should return a result with the expected value for a passing filter");
    }

    @Test
    void testExecuteHelperReturnsUnsuccessfulForFailingValue() throws Exception {
        PatientExecutionResult<String> result = getHandler().executeHelper(CALLABLE, FAILING_FILTER);
        Assertions.assertNotNull(result,
                                 "AbstractPatientExecutionHandler executeHelper shouldn't return a null result");
        Assertions.assertFalse(result.isPresent(),
                               "AbstractPatientExecutionHandler executeHelper should return an unsuccessful result for a failing filter");
    }

    @Test
    void testPropagateThrowsExceptionForNullThrowable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getHandler().propagate(null));
    }

    @Test
    void testPropagateReturnsCastExceptionForUncheckedException() {
        Exception exception = new PatientExecutionHandlerContract.ContractUncheckedException();
        Assertions.assertTrue(exception == getHandler().propagate(exception),
                              "AbstractPatientExecutionHandler propagate should return the same instance for unchecked exceptions");
    }

    @Test
    void testPropagateReturnsWrappedExceptionForCheckedException() {
        Exception exception = new PatientExecutionHandlerContract.ContractCheckedException();
        RuntimeException returned = getHandler().propagate(exception);
        Assertions.assertTrue(exception != returned,
                              "AbstractPatientExecutionHandler propagate should return a wrapped instance for checked exceptions");
        Assertions.assertTrue(null != returned.getCause(),
                              "AbstractPatientExecutionHandler propagate should return a wrapped instance for checked exceptions with a set cause");
        Assertions.assertTrue(returned.getCause() == exception,
                              "AbstractPatientExecutionHandler propagate should return a wrapped instance for checked exceptions with the given exception set as the cause");
    }
}
