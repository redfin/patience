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

import com.redfin.patience.exceptions.PatientException;
import com.redfin.patience.exceptions.PatientRetryException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

/**
 * A PatientWaitFuture is an immutable object that holds all of the information needed
 * to retry until a valid result is retrieved from the given executable or a set
 * number of retries has been reached.
 *
 * @param <T> the type to be returned from this future instance.
 */
public final class PatientRetryFuture<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Fields & Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final PatientSleep sleep;
    private final Duration initialDelay;
    private final int defaultNumberOfRetries;
    private final PatientExecutionHandler executionHandler;
    private final PatientDelaySupplierFactory delaySupplierFactory;
    private final PatientExecutable<T> executable;
    private final Predicate<T> filter;
    private final Supplier<String> failureMessageSupplier;

    /**
     * Create a new {@link PatientWaitFuture} instance with the given values.
     *
     * @param sleep                  the {@link PatientSleep} to be used for making the current thread sleep.
     *                               May not be null.
     * @param initialDelay           the {@link Duration} time to sleep when waiting for a value.
     *                               A value of zero means to not sleep.
     *                               May not be null or negative.
     * @param defaultNumberOfRetries The default maximum number of retries. This is used
     *                               for the {@link #get()} and {@link #check()} methods.
     *                               A value of zero means to attempt to get a value only once.
     *                               May not be negative.
     * @param executionHandler       the {@link PatientExecutionHandler} to be used for this future.
     *                               May not be null.
     * @param delaySupplierFactory   the {@link PatientDelaySupplierFactory} to be used to get a supplier
     *                               of durations to wait between unsuccessful attempts to get a result.
     *                               May not be null.
     * @param executable             the {@link PatientExecutable} to be used to retrieve values.
     *                               May not be null.
     * @param filter                 the {@link Predicate} to be used to test values from the executable.
     *                               May not be null.
     * @param failureMessage         the String message for the {@link PatientRetryException} if no
     *                               valid value is found within the timeout.
     *                               May be null.
     *
     * @throws IllegalArgumentException if any argument other than failureMessage is null or if
     *                                  either initialDelay or defaultNumberOfRetries are negative.
     */
    public PatientRetryFuture(PatientSleep sleep,
                              Duration initialDelay,
                              int defaultNumberOfRetries,
                              PatientExecutionHandler executionHandler,
                              PatientDelaySupplierFactory delaySupplierFactory,
                              PatientExecutable<T> executable,
                              Predicate<T> filter,
                              String failureMessage) {
        this(sleep,
             initialDelay,
             defaultNumberOfRetries,
             executionHandler,
             delaySupplierFactory,
             executable,
             filter,
             () -> failureMessage);
    }

    /**
     * Create a new {@link PatientWaitFuture} instance with the given values.
     *
     * @param sleep                  the {@link PatientSleep} to be used for making the current thread sleep.
     *                               May not be null.
     * @param initialDelay           the {@link Duration} time to sleep when waiting for a value.
     *                               A value of zero means to not sleep.
     *                               May not be null or negative.
     * @param defaultNumberOfRetries The default maximum number of retries. This is used
     *                               for the {@link #get()} and {@link #check()} methods.
     *                               A value of zero means to attempt to get a value only once.
     *                               May not be negative.
     * @param executionHandler       the {@link PatientExecutionHandler} to be used for this future.
     *                               May not be null.
     * @param delaySupplierFactory   the {@link PatientDelaySupplierFactory} to be used to get a supplier
     *                               of durations to wait between unsuccessful attempts to get a result.
     *                               May not be null.
     * @param executable             the {@link PatientExecutable} to be used to retrieve values.
     *                               May not be null.
     * @param filter                 the {@link Predicate} to be used to test values from the executable.
     *                               May not be null.
     * @param failureMessageSupplier the {@link Supplier} of String messages for the {@link PatientRetryException} if no
     *                               valid value is found within the timeout. May not be null.
     *
     * @throws IllegalArgumentException if any argument other than failureMessage is null or if
     *                                  either initialDelay or defaultNumberOfRetries are negative.
     */
    public PatientRetryFuture(PatientSleep sleep,
                              Duration initialDelay,
                              int defaultNumberOfRetries,
                              PatientExecutionHandler executionHandler,
                              PatientDelaySupplierFactory delaySupplierFactory,
                              PatientExecutable<T> executable,
                              Predicate<T> filter,
                              Supplier<String> failureMessageSupplier) {
        this.sleep = validate().that(sleep).isNotNull();
        this.initialDelay = validate().that(initialDelay).isAtLeast(Duration.ZERO);
        this.defaultNumberOfRetries = validate().that(defaultNumberOfRetries).isAtLeast(0);
        this.executionHandler = validate().that(executionHandler).isNotNull();
        this.delaySupplierFactory = validate().that(delaySupplierFactory).isNotNull();
        this.executable = validate().that(executable).isNotNull();
        this.filter = validate().that(filter).isNotNull();
        this.failureMessageSupplier = validate().that(failureMessageSupplier).isNotNull();
    }

    // ----------------------------------------------------
    // Helpers
    // ----------------------------------------------------

    private T execute(Supplier<PatientExecutionResult<T>> patientExecutionResultSupplier,
                      int numRetries) {
        // Validate the arguments
        validate().that(patientExecutionResultSupplier).isNotNull();
        validate().that(numRetries).isAtLeast(0);
        // Start trying to get a successful result
        List<String> failedAttemptDescriptions = new ArrayList<>();
        Supplier<Duration> delayDurationSupplier = delaySupplierFactory.create();
        if (null == delayDurationSupplier) {
            throw new PatientException("Received a null duration supplier from the retry handler");
        }
        // Make the first next delay zero so that we don't sleep during the first loop
        Duration nextDelay = Duration.ZERO;
        // We go while <= since the first attempt is NOT a retry
        for (int i = 0; i <= numRetries; i++) {
            // Sleep for the next duration delay
            sleep.sleepFor(nextDelay);
            // Get an execution attempt result and check it's status
            try {
                PatientExecutionResult<T> result = patientExecutionResultSupplier.get();
                if (null == result) {
                    throw new PatientException("Received a null PatientExecutionResult from the execution handler.");
                }
                if (result.isSuccess()) {
                    return result.getResult();
                } else {
                    failedAttemptDescriptions.add(result.getFailedAttemptDescription());
                }
            } catch (PatientException e) {
                // simply propagate this
                throw e;
            } catch (Throwable throwable) {
                throw new PatientException("Unexpected throwable caught while getting execution result", throwable);
            }
            // Failure, get the next delay duration
            nextDelay = delayDurationSupplier.get();
            if (null == nextDelay || nextDelay.isNegative()) {
                throw new PatientException("Received a null or negative Duration from the delay duration supplier.");
            }
        }
        // No valid result found within the max duration
        throw new PatientRetryException(failureMessageSupplier.get(), failedAttemptDescriptions);
    }

    // ----------------------------------------------------
    // Public API
    // ----------------------------------------------------

    /**
     * @param failureMessage the String message to be given to the {@link PatientRetryFuture}
     *                       if no valid result is found within the timeout.
     *
     * @return a new {@link PatientRetryFuture} instance with the current values and the given failure message.
     */
    public PatientRetryFuture<T> withMessage(String failureMessage) {
        return withMessage(() -> failureMessage);
    }

    /**
     * @param failureMessageSupplier the String supplier to be used to generate the failure message
     *                               for the {@link PatientRetryFuture} if no valid result is
     *                               found within the timeout.
     *
     * @return a new {@link PatientRetryFuture} instance with the current values and
     * the given failure message supplier.
     *
     * @throws IllegalArgumentException if failureMessageSupplier is null.
     */
    public PatientRetryFuture<T> withMessage(Supplier<String> failureMessageSupplier) {
        validate().that(failureMessageSupplier).isNotNull();
        return new PatientRetryFuture<>(sleep,
                                        initialDelay,
                                        defaultNumberOfRetries,
                                        executionHandler,
                                        delaySupplierFactory,
                                        executable,
                                        filter,
                                        failureMessageSupplier);
    }

    /**
     * @param filter the {@link Predicate} to use to verify if a value from the given
     *               {@link PatientExecutable} is a valid result.
     *               May not be null.
     *
     * @return a new {@link PatientRetryFuture} instance with the current values and the given filter.
     *
     * @throws IllegalArgumentException if filter is null.
     */
    public PatientRetryFuture<T> withFilter(Predicate<T> filter) {
        validate().that(filter).isNotNull();
        return new PatientRetryFuture<>(sleep,
                                        initialDelay,
                                        defaultNumberOfRetries,
                                        executionHandler,
                                        delaySupplierFactory,
                                        executable,
                                        filter,
                                        failureMessageSupplier);
    }

    /**
     * This is the same as calling {@link #get(int)} with the default number
     * of retries for this patient future.
     *
     * @return the first found valid result from this patient future instance.
     *
     * @throws PatientRetryException if no valid result is found within the
     *                               specified number of retries.
     */
    public T get() {
        return get(defaultNumberOfRetries);
    }

    /**
     * Begin executing the patient wait in the following way:
     * <ul>
     * <li>Sleep for the initial delay, if any.</li>
     * <li>Use the given execution handler to try to get a value (checked with the filter).</li>
     * <li>If a valid value is found, then return true.</li>
     * <li>If an invalid value is found, then check if there are more retries.</li>
     * <li><ul>
     * <li>If there are no more retries then throw a {@link PatientRetryException}</li>
     * <li>Otherwise sleep for the next delay duration and go back to trying to get a value again.</li>
     * </ul></li></ul>
     *
     * @param numRetries the number of retries possible when trying to get
     *                   a valid result. A value of zero means only attempt to get a value once.
     *                   May not be negative.
     *
     * @return the first found valid result from this patient future instance.
     *
     * @throws IllegalArgumentException if numRetries is negative.
     * @throws PatientRetryException    if no valid result is found within the
     *                                  specified number of retries.
     */
    public T get(int numRetries) {
        validate().that(numRetries).isAtLeast(0);
        // Sleep for the initial timeout (if any)
        sleep.sleepFor(initialDelay);
        // Start trying to get a valid result
        return execute(() -> executionHandler.execute(executable, filter),
                       numRetries);
    }

    /**
     * This is the same as calling {@link #check(int)} with the default number of
     * retries for this patient future.
     *
     * @return the true if a valid result is found before the timeout or false
     * otherwise.
     */
    public boolean check() {
        return check(defaultNumberOfRetries);
    }

    /**
     * Begin executing the patient wait in the following way:
     * <ul>
     * <li>Sleep for the initial delay, if any.</li>
     * <li>Use the given execution handler to try to get a value (checked with the filter).</li>
     * <li>If a valid value is found, then return true.</li>
     * <li>If an invalid value is found, then check if there are more retries.</li>
     * <li><ul>
     * <li>If there are no more retries then return false</li>
     * <li>Otherwise sleep for the next delay duration and go back to trying to get a value again.</li>
     * </ul></li></ul>
     *
     * @param numRetries the number of retries possible when trying to get
     *                   a valid result. A value of zero means only attempt to get a value once.
     *                   May not be negative.
     *
     * @return true if a successful value is found before the timeout or false otherwise.
     *
     * @throws IllegalArgumentException if numRetries is negative.
     */
    public boolean check(int numRetries) {
        try {
            get(numRetries);
            return true;
        } catch (PatientRetryException ignore) {
            return false;
        }
    }

    // ----------------------------------------------------
    // Package-private methods for testing
    // ----------------------------------------------------

    PatientSleep getSleep() {
        return sleep;
    }

    Duration getInitialDelay() {
        return initialDelay;
    }

    int getDefaultNumberOfRetries() {
        return defaultNumberOfRetries;
    }

    PatientExecutionHandler getExecutionHandler() {
        return executionHandler;
    }

    PatientDelaySupplierFactory getDelaySupplierFactory() {
        return delaySupplierFactory;
    }

    PatientExecutable<T> getExecutable() {
        return executable;
    }

    Predicate<T> getFilter() {
        return filter;
    }

    Supplier<String> getFailureMessageSupplier() {
        return failureMessageSupplier;
    }
}
