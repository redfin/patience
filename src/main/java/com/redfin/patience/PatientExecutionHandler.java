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

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * An interface representing a customization point for the Patience library.
 * An implementation of this interface needs to be sure that it can be re-used.
 * It is where the actual handling of retrieving a value from the callable and subsequent
 * testing of the value with the given predicate occurs. For example, if
 * you wanted a simple, naive, retrieval of values or if you want to ignore certain types of
 * exceptions thrown during the execution of those steps.
 * <p>
 * Each call to the {@link PatientFuture#get(Duration)} method might make multiple calls
 * to the {@link #execute(Callable, Predicate)} method.
 */
@FunctionalInterface
public interface PatientExecutionHandler {

    /**
     * @param callable the {@link Callable} to use to get each value that is tested by the
     *                 predicate and then returned as a result.
     *                 May not be null.
     * @param filter   the {@link Predicate} to test each value returned from the callable.
     *                 May not be null.
     * @param <T>      the type of value returned from the callable and tested by the filter.
     *
     * @return a {@link PatientExecutionResult} instance that is either successful with
     * a valid value or unsuccessful depending upon the test of the filter with
     * the value returned from the callable.
     *
     * @throws IllegalArgumentException if callable or filter are null.
     * @throws RuntimeException         if an unhandled exception occurs during the calling
     *                                  of {@link Callable#call()} or {@link Predicate#test(Object)}
     *                                  that isn't handled by the execution handler.
     */
    <T> PatientExecutionResult<T> execute(Callable<T> callable, Predicate<T> filter);
}
