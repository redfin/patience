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
import com.redfin.patience.PatientExecutionResult;
import com.redfin.validity.ValidityUtils;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * A class intended to be the super class of all implementations of the
 * {@link PatientExecutionHandler} interface. It takes care of the basic calling
 * the given callable and predicate testing as well as returning the correct
 * {@link PatientExecutionResult} for the result. Validation of the arguments
 * and any custom implementations need to be taken care of by concrete sub classes.
 */
public abstract class AbstractPatientExecutionHandler implements PatientExecutionHandler {

    /**
     * Each call to this method will get a result from the given callable with {@link Callable#call()}
     * and then tests the resulting value with the given filter via {@link Predicate#test(Object)}.
     * If the value from the callable passes the test, a successful {@link PatientExecutionResult}
     * will be returned with that value. If it does not pass the test, then an unsuccessful, empty,
     * {@link PatientExecutionResult} will be returned.
     * <p>
     * Any {@link Exception} thrown during the {@link Callable#call()} or {@link Predicate#test(Object)}
     * methods will be unhandled.
     * <p>
     * Note that this method doesn't check the arguments for null, the sub class caller should verify
     * the arguments themselves.
     *
     * @param callable the {@link Callable} to retrieve values from.
     * @param filter   the {@link Predicate} to use to test values from the callable.
     * @param <T>      the type of object retrieved from the callable.
     *
     * @return the {@link PatientExecutionResult} for this attempt.
     *
     * @throws Exception if calling {@link Callable#call()} on the given callable or
     *                   testing the subsequent result with the given filter
     *                   {@link Predicate#test(Object)} throws an exception.
     */
    protected final <T> PatientExecutionResult<T> executeHelper(Callable<T> callable, Predicate<T> filter) throws Exception {
        // Extract and test the value from the callable, don't catch anything
        T result = callable.call();
        if (filter.test(result)) {
            // A valid value was found, return a success result
            return PatientExecutionResult.success(result);
        } else {
            // No valid value was found, return a failure result
            return PatientExecutionResult.failure(ValidityUtils.describe(result));
        }
    }

    /**
     * @param thrown the {@link Exception} to wrap, if necessary.
     *               May not be null.
     *
     * @return the given exception cast as a {@link RuntimeException}
     * if it is already one or a new {@link RuntimeException} with
     * the given exception as it's cause if it is not a
     * {@link RuntimeException}.
     *
     * @throws IllegalArgumentException if thrown is null.
     */
    protected final RuntimeException propagate(Exception thrown) {
        validate().that(thrown).isNotNull();
        if (thrown instanceof RuntimeException) {
            return (RuntimeException) thrown;
        } else {
            return new RuntimeException(thrown);
        }
    }
}
