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

package com.redfin.patience.retrystrategies;

import com.redfin.patience.PatientException;
import com.redfin.patience.PatientExecutionResult;
import com.redfin.patience.PatientSleep;
import com.redfin.patience.PatientTimeoutException;
import com.redfin.patience.PatientRetryStrategy;
import com.redfin.validity.Validity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A class intended to be the super class of all delay between implementations of the
 * {@link PatientRetryStrategy} interface. It takes care of actually implementing
 * the retry strategy with delays between each attempt in a generic way and sub classes
 * take care of calculating the duration to wait between attempts.
 * <p>
 * If an attempt is unsuccessful and the next delay would take the time past the given timeout,
 * then no delay or extra attempt will be executed and it will be considered an unsuccessful
 * wait attempt overall. No matter the timeout (even if it's 0) there will be at least one attempt
 * at retrieving a value. The timeout simply calculates the end time based off the given
 * duration and the time at which the {@link #execute(Duration, Supplier)} method is called,
 * so the length of time spent trying to get a result for each attempt is counted into the
 * overall timeout period.
 */
public abstract class AbstractDelayPatientRetryStrategy implements PatientRetryStrategy {

    /**
     * Return a new {@link Duration} {@link Supplier} for the current execution of
     * the {@link #execute(Duration, Supplier)} method. Each call to that method
     * will call this method only once but the supplier this returns will have it's
     * {@link Supplier#get()} method called multiple times by a single execute invocation.
     * <p>
     * If the {@link Supplier#get()} method ever returns a null or negative {@link Duration}
     * then a {@link PatientException} will be thrown.
     *
     * @return a new {@link Duration} {@link Supplier} for the current execution of
     * the {@link #execute(Duration, Supplier)} method.
     */
    protected abstract Supplier<Duration> getDelayDurations();

    @Override
    public final <T> T execute(Duration timeout, Supplier<PatientExecutionResult<T>> patientExecutionResultSupplier) {
        Validity.require().that(timeout).isGreaterThanOrEqualTo(Duration.ZERO);
        Validity.require().that(patientExecutionResultSupplier).isNotNull();
        // Capture the start time and calculate the end time
        Instant start = Instant.now();
        Instant end = start.plus(timeout);
        // Only execute once if the timeout is zero
        boolean stop = timeout.isZero();
        // Begin attempting to get a result
        Supplier<Duration> delaySupplier = getDelayDurations();
        List<String> failureDescriptions = new ArrayList<>();
        do {
            // Begin attempting to get a result
            PatientExecutionResult<T> result = patientExecutionResultSupplier.get();
            if (null == result) {
                throw new PatientException("Null value return from the result supplier");
            }
            if (result.wasSuccessful()) {
                return result.getSuccessResult();
            } else {
                failureDescriptions.add(result.getFailureDescription());
            }
            // The result wasn't a successful value, try again possibly
            Duration nextDelay = delaySupplier.get();
            if (null == nextDelay || nextDelay.isNegative()) {
                throw new PatientException("Invalid delay returned by the delay supplier. Expected a non-null, non-negative duration, but found: " + nextDelay);
            }
            if (Instant.now().plus(nextDelay).compareTo(end) > 0) {
                // The end time is before or the same as the current time plus the next delay, stop
                stop = true;
            } else {
                // There is enough time in the timeout to make another attempt, sleep for the next delay
                PatientSleep.sleepFor(nextDelay);
            }
        } while (!stop);
        throw new PatientTimeoutException("No value was successfully retrieved within the timeout of " + timeout, failureDescriptions);
    }
}
