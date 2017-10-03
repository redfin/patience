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
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.*;

/**
 * A PatientFuture is an immutable object that holds all of the information needed
 * to wait patiently for a valid result from the given executable.
 *
 * @param <T> the type to be returned from this future instance.
 */
public final class PatientFuture<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Fields & Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final Duration initialDelay;
    private final Duration defaultTimeout;
    private final PatientRetryHandler retryHandler;
    private final PatientExecutionHandler executionHandler;
    private final Executable<T> executable;
    private final Predicate<T> filter;
    private final Supplier<String> failureMessageSupplier;

    /**
     * Create a new {@link PatientFuture} instance with the given values.
     *
     * @param initialDelay     the {@link Duration} time to sleep when waiting for a value.
     *                         A value of zero means to not sleep.
     *                         May not be null or negative.
     * @param defaultTimeout   the {@link Duration} default maximum wait time. This is used
     *                         for the {@link #get()} method.
     *                         A value of zero means to attempt to get a value only once.
     *                         May not be null or negative.
     * @param retryHandler     the {@link PatientRetryHandler} to be used for this future.
     *                         May not be null.
     * @param executionHandler the {@link PatientExecutionHandler} to be used for this future.
     *                         May not be null.
     * @param executable       the {@link Executable} to be used to retrieve values.
     *                         May not be null.
     * @param filter           the {@link Predicate} to be used to test values from the executable.
     *                         May not be null.
     * @param failureMessage   the String message for the {@link PatientTimeoutException} if no
     *                         valid value is found within the timeout.
     *                         May be null.
     *
     * @throws IllegalArgumentException if any argument other than failureMessage is null or if
     *                                  either initialDelay or defaultTimeout are negative.
     */
    public PatientFuture(Duration initialDelay,
                         Duration defaultTimeout,
                         PatientRetryHandler retryHandler,
                         PatientExecutionHandler executionHandler,
                         Executable<T> executable,
                         Predicate<T> filter,
                         String failureMessage) {
        this(initialDelay,
             defaultTimeout,
             retryHandler,
             executionHandler,
             executable,
             filter,
             () -> failureMessage);
    }

    /**
     * Create a new {@link PatientFuture} instance with the given values.
     *
     * @param initialDelay           the {@link Duration} time to sleep when waiting for a value.
     *                               A value of zero means to not sleep.
     *                               May not be null or negative.
     * @param defaultTimeout         the {@link Duration} default maximum wait time. This is used
     *                               for the {@link #get()} method.
     *                               A value of zero means to attempt to get a value only once.
     *                               May not be null or negative.
     * @param retryHandler           the {@link PatientRetryHandler} to be used for this future.
     *                               May not be null.
     * @param executionHandler       the {@link PatientExecutionHandler} to be used for this future.
     *                               May not be null.
     * @param executable             the {@link Executable} to be used to retrieve values.
     *                               May not be null.
     * @param filter                 the {@link Predicate} to be used to test values from the executable.
     *                               May not be null.
     * @param failureMessageSupplier the {@link Supplier} of String messages for the {@link PatientTimeoutException} if no
     *                               valid value is found within the timeout. May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if
     *                                  either initialDelay or defaultTimeout are negative.
     */
    public PatientFuture(Duration initialDelay,
                         Duration defaultTimeout,
                         PatientRetryHandler retryHandler,
                         PatientExecutionHandler executionHandler,
                         Executable<T> executable,
                         Predicate<T> filter,
                         Supplier<String> failureMessageSupplier) {
        this.initialDelay = validate().that(initialDelay).isAtLeast(Duration.ZERO);
        this.defaultTimeout = validate().that(defaultTimeout).isAtLeast(Duration.ZERO);
        this.retryHandler = validate().that(retryHandler).isNotNull();
        this.executionHandler = validate().that(executionHandler).isNotNull();
        this.executable = validate().that(executable).isNotNull();
        this.filter = validate().that(filter).isNotNull();
        this.failureMessageSupplier = validate().that(failureMessageSupplier).isNotNull();
    }

    /**
     * @param failureMessage the String message to be given to the {@link PatientTimeoutException}
     *                       if no valid result is found within the timeout.
     *
     * @return a new {@link PatientFuture} instance with the current values and the given failure message.
     */
    public PatientFuture<T> withMessage(String failureMessage) {
        validate().that(failureMessage).isNotEmpty();
        return withMessage(() -> failureMessage);
    }

    /**
     * @param failureMessageSupplier the String supplier to be used to generate the failure message
     *                               for the {@link PatientTimeoutException} if no valid result is
     *                               found within the timeout.
     *
     * @return a new {@link PatientFuture} instance with the current values and
     * the given failure message supplier.
     *
     * @throws IllegalArgumentException if failureMessageSupplier is null.
     */
    public PatientFuture<T> withMessage(Supplier<String> failureMessageSupplier) {
        validate().that(failureMessageSupplier).isNotNull();
        return new PatientFuture<>(initialDelay,
                                   defaultTimeout,
                                   retryHandler,
                                   executionHandler,
                                   executable,
                                   filter,
                                   failureMessageSupplier);
    }

    /**
     * @param filter the {@link Predicate} to use to verify if a value from the given
     *               {@link Executable} is a valid result.
     *               May not be null.
     *
     * @return a new {@link PatientFuture} instance with the current values and the given filter.
     *
     * @throws IllegalArgumentException if filter is null.
     */
    public PatientFuture<T> withFilter(Predicate<T> filter) {
        validate().that(filter).isNotNull();
        return new PatientFuture<>(initialDelay,
                                   defaultTimeout,
                                   retryHandler,
                                   executionHandler,
                                   executable,
                                   filter,
                                   failureMessageSupplier);
    }

    /**
     * This is the same as calling {@link #get(Duration)} with the default timeout
     * duration for this patient future.
     *
     * @return the first found valid result from this patient future instance.
     *
     * @throws PatientTimeoutException if no valid result is found within the
     *                                 given default timeout.
     */
    public T get() {
        return get(defaultTimeout);
    }

    /**
     * Begin executing the patient wait in the following way:
     * <ul>
     * <li>Sleep for the initial delay, if any.</li>
     * <li>Use the given retry handler and execution handler to begin execution.</li>
     * <li>If a successful {@link PatientResult} is returned from the retry handler, then return the result value.</li>
     * <li>If the {@link PatientResult} is not successful, throw a {@link PatientTimeoutException}.</li>
     * </ul>
     *
     * @param timeout the {@link Duration} that represents the maximum amount
     *                of time to try to find a valid result. Note that a failure
     *                can occur before the timeout is reached depending upon the
     *                {@link PatientRetryHandler}.
     *                A value of zero means only attempt to get a value once.
     *                May not be null or negative.
     *
     * @return the first found valid result from this patient future instance.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     * @throws PatientTimeoutException  if no valid result is found within the
     *                                  given timeout.
     */
    public T get(Duration timeout) {
        validate().that(timeout).isAtLeast(Duration.ZERO);
        // Sleep for the initial timeout (if any)
        PatientSleep.sleepFor(initialDelay);
        // Start waiting for a result
        PatientResult<T> result = retryHandler.execute(() -> executionHandler.execute(executable, filter),
                                                       timeout);
        // Validate the result
        if (null == result) {
            throw new PatientException("Received a null PatientResult from the wait function.");
        }
        // Return the value or throw
        if (result.isSuccess()) {
            return result.getResult();
        } else {
            throw new PatientTimeoutException(failureMessageSupplier.get(), result.getFailedAttemptDescriptions());
        }
    }

    // ----------------------------------------------------
    // Package-private methods for testing
    // ----------------------------------------------------

    Duration getInitialDelay() {
        return initialDelay;
    }

    Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    PatientRetryHandler getRetryHandler() {
        return retryHandler;
    }

    PatientExecutionHandler getExecutionHandler() {
        return executionHandler;
    }

    Executable<T> getExecutable() {
        return executable;
    }

    Predicate<T> getFilter() {
        return filter;
    }

    Supplier<String> getFailureMessageSupplier() {
        return failureMessageSupplier;
    }
}
