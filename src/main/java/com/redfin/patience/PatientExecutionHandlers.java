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

import com.redfin.patience.executionhandlers.IgnoringAllPatientExecutionHandler;
import com.redfin.patience.executionhandlers.IgnoringPatientExecutionHandler;
import com.redfin.patience.executionhandlers.SimplePatientExecutionHandler;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * A non-instantiable class that wraps creation
 * of the {@link PatientExecutionHandler} implementations in
 * the Patience library.
 */
public final class PatientExecutionHandlers {

    /*
     * Some execution handler don't contain any state
     * so they may be re-used safely.
     */

    private static final SimplePatientExecutionHandler SIMPLE = new SimplePatientExecutionHandler();
    private static final IgnoringAllPatientExecutionHandler IGNORING_ALL = new IgnoringAllPatientExecutionHandler();

    /**
     * @return a new {@link SimplePatientExecutionHandler} instance.
     */
    public static SimplePatientExecutionHandler simpleHandler() {
        return SIMPLE;
    }

    /**
     * All exceptions thrown during the execution of the
     * {@link PatientExecutionHandler#execute(Callable, Predicate)} will
     * be checked with the given exception types so it is a best practice to ignore
     * with as much granularity as possible so that unexpected exceptions are not
     * ignored.
     *
     * @param exceptionType  an exception type to be ignored.
     * @param exceptionTypes additional exception types to be ignored.
     *
     * @return a new {@link IgnoringPatientExecutionHandler} instance with the given types
     * to be ignored.
     *
     * @throws IllegalArgumentException if exceptionType is null.
     */
    @SafeVarargs
    public static IgnoringPatientExecutionHandler ignoring(Class<? extends Exception> exceptionType,
                                                           Class<? extends Exception>... exceptionTypes) {
        validate().that(exceptionType).isNotNull();
        return new IgnoringPatientExecutionHandler(exceptionType,
                                                   exceptionTypes);
    }

    /**
     * @return a new {@link IgnoringAllPatientExecutionHandler} instance.
     */
    public static IgnoringAllPatientExecutionHandler ignoringAll() {
        return IGNORING_ALL;
    }

    /*
     * Ensure this class is non-instantiable.
     */

    private PatientExecutionHandlers() {
        throw new AssertionError("Cannot instantiate this class");
    }
}
