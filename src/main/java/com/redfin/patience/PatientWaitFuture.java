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
import com.redfin.patience.exceptions.PatientTimeoutException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

/**
 * A PatientWaitFuture is an immutable object that holds all of the information needed
 * to wait patiently for a valid result from the given executable.
 *
 * @param <T> the type to be returned from this future instance.
 */
public final class PatientWaitFuture<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Fields & Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final Sleep sleep;
    private final Duration initialDelay;
    private final Duration defaultTimeout;
    private final PatientExecutionHandler executionHandler;
    private final DelaySupplierFactory delaySupplierFactory;
    private final PatientExecutable<T> executable;
    private final Predicate<T> filter;
    private final Supplier<String> failureMessageSupplier;

    /**
     * Create a new {@link PatientWaitFuture} instance with the given values.
     *
     * @param sleep                the {@link Sleep} to be used for making the current thread sleep.
     *                             May not be null.
     * @param initialDelay         the {@link Duration} time to sleep when waiting for a value.
     *                             A value of zero means to not sleep.
     *                             May not be null or negative.
     * @param defaultTimeout       the {@link Duration} default maximum wait time. This is used
     *                             for the {@link #get()} or {@link #check()} methods.
     *                             A value of zero means to attempt to get a value only once.
     *                             May not be null or negative.
     * @param executionHandler     the {@link PatientExecutionHandler} to be used for this future.
     *                             May not be null.
     * @param delaySupplierFactory the {@link DelaySupplierFactory} to be used to get a supplier
     *                             of durations to wait between unsuccessful attempts to get a result.
     *                             May not be null.
     * @param executable           the {@link PatientExecutable} to be used to retrieve values.
     *                             May not be null.
     * @param filter               the {@link Predicate} to be used to test values from the executable.
     *                             May not be null.
     * @param failureMessage       the String message for the {@link PatientTimeoutException} if no
     *                             valid value is found within the timeout.
     *                             May be null.
     *
     * @throws IllegalArgumentException if any argument other than failureMessage is null or if
     *                                  either initialDelay or defaultTimeout are negative.
     */
    public PatientWaitFuture(Sleep sleep,
                             Duration initialDelay,
                             Duration defaultTimeout,
                             PatientExecutionHandler executionHandler,
                             DelaySupplierFactory delaySupplierFactory,
                             PatientExecutable<T> executable,
                             Predicate<T> filter,
                             String failureMessage) {
        this(sleep,
             initialDelay,
             defaultTimeout,
             executionHandler,
             delaySupplierFactory,
             executable,
             filter,
             () -> failureMessage);
    }

    /**
     * Create a new {@link PatientWaitFuture} instance with the given values.
     *
     * @param sleep                  the {@link Sleep} to be used for making the current thread sleep.
     *                               May not be null.
     * @param initialDelay           the {@link Duration} time to sleep when waiting for a value.
     *                               A value of zero means to not sleep.
     *                               May not be null or negative.
     * @param defaultTimeout         the {@link Duration} default maximum wait time. This is used
     *                               for the {@link #get()} or {@link #check()} methods.
     *                               A value of zero means to attempt to get a value only once.
     *                               May not be null or negative.
     * @param executionHandler       the {@link PatientExecutionHandler} to be used for this future.
     *                               May not be null.
     * @param delaySupplierFactory   the {@link DelaySupplierFactory} to be used to get a supplier
     *                               of durations to wait between unsuccessful attempts to get a result.
     *                               May not be null.
     * @param executable             the {@link PatientExecutable} to be used to retrieve values.
     *                               May not be null.
     * @param filter                 the {@link Predicate} to be used to test values from the executable.
     *                               May not be null.
     * @param failureMessageSupplier the {@link Supplier} of String messages for the {@link PatientTimeoutException} if no
     *                               valid value is found within the timeout. May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if
     *                                  either initialDelay or defaultTimeout are negative.
     */
    public PatientWaitFuture(Sleep sleep,
                             Duration initialDelay,
                             Duration defaultTimeout,
                             PatientExecutionHandler executionHandler,
                             DelaySupplierFactory delaySupplierFactory,
                             PatientExecutable<T> executable,
                             Predicate<T> filter,
                             Supplier<String> failureMessageSupplier) {
        this.sleep = validate().that(sleep).isNotNull();
        this.initialDelay = validate().that(initialDelay).isAtLeast(Duration.ZERO);
        this.defaultTimeout = validate().that(defaultTimeout).isAtLeast(Duration.ZERO);
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
                      Duration maxDuration) {
        // Validate the arguments
        validate().that(patientExecutionResultSupplier).isNotNull();
        validate().that(maxDuration).isAtLeast(Duration.ZERO);
        // Start trying to get a successful result, use a do-while since a duration of ZERO should attempt once
        List<String> failedAttemptDescriptions = new ArrayList<>();
        Supplier<Duration> delayDurationSupplier = delaySupplierFactory.create();
        if (null == delayDurationSupplier) {
            throw new PatientException("Received a null duration supplier from the retry handler");
        }
        // Make the first next delay zero so that we don't sleep during the first loop
        Duration nextDelay = Duration.ZERO;
        Instant maxEndTime = Instant.now().plus(maxDuration);
        do {
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
            // Don't sleep if the next delay would put the wake time after the max timeout
        } while (Instant.now().plus(nextDelay).isBefore(maxEndTime));
        // No valid result found within the max duration
        throw new PatientTimeoutException(failureMessageSupplier.get(), failedAttemptDescriptions);
    }

    // ----------------------------------------------------
    // Public API
    // ----------------------------------------------------

    /**
     * @param failureMessage the String message to be given to the {@link PatientTimeoutException}
     *                       if no valid result is found within the timeout.
     *
     * @return a new {@link PatientWaitFuture} instance with the current values and the given failure message.
     */
    public PatientWaitFuture<T> withMessage(String failureMessage) {
        return withMessage(() -> failureMessage);
    }

    /**
     * @param failureMessageSupplier the String supplier to be used to generate the failure message
     *                               for the {@link PatientTimeoutException} if no valid result is
     *                               found within the timeout.
     *
     * @return a new {@link PatientWaitFuture} instance with the current values and
     * the given failure message supplier.
     *
     * @throws IllegalArgumentException if failureMessageSupplier is null.
     */
    public PatientWaitFuture<T> withMessage(Supplier<String> failureMessageSupplier) {
        validate().that(failureMessageSupplier).isNotNull();
        return new PatientWaitFuture<>(sleep,
                                       initialDelay,
                                       defaultTimeout,
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
     * @return a new {@link PatientWaitFuture} instance with the current values and the given filter.
     *
     * @throws IllegalArgumentException if filter is null.
     */
    public PatientWaitFuture<T> withFilter(Predicate<T> filter) {
        validate().that(filter).isNotNull();
        return new PatientWaitFuture<>(sleep,
                                       initialDelay,
                                       defaultTimeout,
                                       executionHandler,
                                       delaySupplierFactory,
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
     * <li>Use the given execution handler to try to get a value (checked with the filter).</li>
     * <li>If a valid value is found, then return it.</li>
     * <li>If an invalid value is found, then check the next sleep duration from the duration supplier created by the delay supplier.</li>
     * <li><ul>
     * <li>If the next delay duration would make the time past the timeout duration then throw a {@link PatientTimeoutException}</li>
     * <li>Otherwise go back to trying to get a value from the execution handler again.</li>
     * </ul></li></ul>
     *
     * @param timeout the {@link Duration} that represents the maximum amount
     *                of time to try to find a valid result. Note that this can
     *                return before the timeout is reached if the next delay
     *                between execution attempts would take it over the maximum
     *                timeout. A value of zero means only attempt to get a value once.
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
        sleep.sleepFor(initialDelay);
        // Start trying to get a valid result
        return execute(() -> executionHandler.execute(executable, filter),
                       timeout);
    }

    /**
     * This is the same as calling {@link #check(Duration)} with the default timeout
     * duration for this patient future.
     *
     * @return the true if a valid result is found before the timeout or false
     * otherwise.
     */
    public boolean check() {
        return check(defaultTimeout);
    }

    /**
     * Begin executing the patient wait in the following way:
     * <ul>
     * <li>Sleep for the initial delay, if any.</li>
     * <li>Use the given execution handler to try to get a value (checked with the filter).</li>
     * <li>If a valid value is found, then return true.</li>
     * <li>If an invalid value is found, then check the next sleep duration from the duration supplier created by the delay supplier.</li>
     * <li><ul>
     * <li>If the next delay duration would make the time past the timeout duration then return false</li>
     * <li>Otherwise go back to trying to get a value from the execution handler again.</li>
     * </ul></li></ul>
     *
     * @param timeout the {@link Duration} that represents the maximum amount
     *                of time to try to find a valid result. Note that this can
     *                return before the timeout is reached if the next delay
     *                between execution attempts would take it over the maximum
     *                timeout. A value of zero means only attempt to get a value once.
     *                May not be null or negative.
     *
     * @return true if a successful value is found before the timeout or false otherwise.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    public boolean check(Duration timeout) {
        try {
            get(timeout);
            return true;
        } catch (PatientTimeoutException ignore) {
            return false;
        }
    }

    // ----------------------------------------------------
    // Package-private methods for testing
    // ----------------------------------------------------

    Sleep getSleep() {
        return sleep;
    }

    Duration getInitialDelay() {
        return initialDelay;
    }

    Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    PatientExecutionHandler getExecutionHandler() {
        return executionHandler;
    }

    DelaySupplierFactory getDelaySupplierFactory() {
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
