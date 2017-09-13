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

import static com.redfin.validity.Validity.validate;

/**
 * An immutable container holding all the information needed to retry a
 * {@link Callable} instance and test it's result with a given
 * {@link Predicate}. This is the final instance you would interact
 * with as part of a Patience call.
 *
 * @param <T> the type of object returned from the given callable.
 */
public final class FilteredPatientFuture<T> implements PatientFuture<T> {

    private final PatientWait wait;
    private final Callable<T> callable;
    private final Predicate<T> filter;

    /**
     * Create a new {@link FilteredPatientFuture} instance with
     * the given values.
     *
     * @param wait     the {@link PatientWait} containing the wait
     *                 configurations for this future.
     *                 May not be null.
     * @param callable the {@link Callable} instance that will
     *                 be repeatedly called until a valid value is found
     *                 or a timeout is reached.
     *                 May not be null.
     * @param filter   the {@link Predicate} instance with which results
     *                 from the callable instance will be tested to find
     *                 out if they are valid.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if wait, callable, or filter are null.
     */
    FilteredPatientFuture(PatientWait wait, Callable<T> callable, Predicate<T> filter) {
        this.wait = validate().that(wait).isNotNull();
        this.callable = validate().that(callable).isNotNull();
        this.filter = validate().that(filter).isNotNull();
    }

    @Override
    public T get() {
        return get(wait.getDefaultTimeout());
    }

    @Override
    public T get(Duration timeout) {
        validate().that(timeout).isGreaterThanOrEqualTo(Duration.ZERO);
        // Perform initial sleep if requested, then execute the retry attempts
        PatientSleep.sleepFor(wait.getInitialDelay());
        return wait.getRetryStrategy()
                   .execute(timeout,
                            () -> wait.getExecutionHandler()
                                      .execute(callable, filter));
    }
}
