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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * An immutable sub class of the {@link AbstractPatientExecutionHandler}. It catches
 * any exception thrown during the execution of the given callable or predicate and
 * checks them against the set of ignored exception types. If it is an ignored type,
 * then a simple unsuccessful result will be returned. If it is not an ignored
 * type then it will be thrown as a {@link RuntimeException} (either via a cast if
 * it is already a runtime exception or set as the cause of a new instance if not).
 */
public class IgnoringPatientExecutionHandler extends AbstractPatientExecutionHandler {

    private final Set<Class<? extends Exception>> ignoredTypes;

    /**
     * Create a new {@link IgnoringPatientExecutionHandler} instance. All exceptions
     * thrown during the execution of the {@link #execute(Callable, Predicate)} will
     * be checked with the given exception types so it is a best practice to ignore
     * with as much granularity as possible so that unexpected exceptions are not
     * ignored.
     *
     * @param exceptionType  an exception type to be ignored.
     *                       May not be null.
     * @param exceptionTypes additional exception types to be ignored.
     *                       If null or empty this handler will only
     *                       ignore the exceptionType type.
     *
     * @throws IllegalArgumentException if exceptionType is null.
     */
    @SafeVarargs
    public IgnoringPatientExecutionHandler(Class<? extends Exception> exceptionType,
                                           Class<? extends Exception>... exceptionTypes) {
        validate().that(exceptionType).isNotNull();
        ignoredTypes = new HashSet<>();
        ignoredTypes.add(exceptionType);
        if (null != exceptionTypes && exceptionTypes.length > 0) {
            Collections.addAll(ignoredTypes, exceptionTypes);
        }
    }

    @Override
    public <T> PatientExecutionResult<T> execute(Callable<T> callable, Predicate<T> filter) {
        validate().that(callable).isNotNull();
        validate().that(filter).isNotNull();
        try {
            return executeHelper(callable, filter);
        } catch (Throwable thrown) {
            if (ignoredTypes.stream().anyMatch(clazz -> clazz.isAssignableFrom(thrown.getClass()))) {
                // No result, but this type of exception is ignored so return an empty result
                return PatientExecutionResult.failure("Ignored exception -> " + thrown.toString());
            } else {
                // The thrown exception is not ignored, propagate it
                throw propagate(thrown);
            }
        }
    }
}
