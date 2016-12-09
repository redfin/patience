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

import com.redfin.validity.Validity;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * An immutable container holding all the information needed to retry a
 * {@link Callable} instance and test it's result with a given
 * {@link Predicate}. This is the final instance you would interact
 * with as part of a Patience call.
 *
 * @param <T> the type of object returned from the given callable.
 */
public final class PatientFuture<T> {

    private final PatientWait wait;
    private final Callable<T> callable;
    private final Predicate<T> filter;

    /**
     * Create a new {@link PatientFuture} instance with
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
    public PatientFuture(PatientWait wait,
                         Callable<T> callable,
                         Predicate<T> filter) {
        this.wait = Validity.require().that(wait).isNotNull();
        this.callable = Validity.require().that(callable).isNotNull();
        this.filter = Validity.require().that(filter).isNotNull();
    }

    /**
     * Attempt to retrieve a valid result from the given {@link Callable}, validated with
     * the given {@link Predicate} filter, with the waiting configuration in the
     * {@link PatientWait} instance used to create this {@link PatientFuture} object. If a
     * valid result is found within the default timeout form the {@link PatientWait} object,
     * then return it. If not, then a {@link PatientTimeoutException} will be thrown.
     *
     * @return the first value from the set callable that passes the given predicate test.
     *
     * @throws ArithmeticException     if an overflow occurs when converting the timeout duration
     *                                 to milliseconds and nanoseconds for the sleepFor duration.
     * @throws PatientTimeoutException if no valid result is found within the given timeout.
     * @throws PatientException        if an illegal internal state occurs during the retry attempts
     *                                 or a thread {@link InterruptedException} is thrown while sleeping.
     * @throws RuntimeException        if an unhandled error occurs during execution by the
     *                                 {@link PatientExecutionHandler}.
     */
    public T get() {
        return get(wait.getDefaultTimeout());
    }

    /**
     * Attempt to retrieve a valid result from the given {@link Callable}, validated with
     * the given {@link Predicate} filter, with the waiting configuration in the
     * {@link PatientWait} instance used to create this {@link PatientFuture} object. If a
     * valid result is found within the given timeout, then return it. If not, then a
     * {@link PatientTimeoutException} will be thrown.
     *
     * @param timeout the link {@link Duration} timeout to use.
     *                May not be null.
     *                A duration of zero simply means make one attempt and then stop.
     *
     * @return the first value from the set callable that passes the given predicate test.
     *
     * @throws ArithmeticException     if an overflow occurs when converting the timeout duration
     *                                 to milliseconds and nanoseconds for the sleepFor duration.
     * @throws PatientTimeoutException if no valid result is found within the given timeout.
     * @throws PatientException        if an illegal internal state occurs during the retry attempts
     *                                 or a thread {@link InterruptedException} is thrown while sleeping.
     * @throws RuntimeException        if an unhandled error occurs during execution by the
     *                                 {@link PatientExecutionHandler}.
     */
    public T get(Duration timeout) {
        Validity.require().that(timeout).isGreaterThanOrEqualTo(Duration.ZERO);
        // Perform initial sleep if requested, then execute the retry attempts
        PatientSleep.sleepFor(wait.getInitialDelay());
        return wait.getRetryStrategy()
                   .execute(timeout,
                            () -> wait.getExecutionHandler()
                                      .execute(callable, filter));
    }
}
