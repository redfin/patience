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

package com.redfin.patience.retries;

import com.redfin.patience.PatientException;
import com.redfin.patience.PatientExecutionResult;
import com.redfin.patience.PatientResult;
import com.redfin.patience.PatientRetryHandler;
import com.redfin.patience.PatientSleep;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

/**
 * Intended to be the base class of most {@link PatientRetryHandler}
 * implementations. It implements the {@link #execute(Supplier, Duration)} method
 * to take care of the different requirements. It declares an abstract method that
 * the sub classes must implement that returns a supplier of durations for how long
 * to wait between unsuccessful retrieval of execution results.
 */
public abstract class AbstractPatientRetryHandler
           implements PatientRetryHandler {

    /**
     * Return the supplier of {@link Duration}s for each patient wait attempt for this retry handler.
     * Each {@link Duration} from the supplier is the next amount of time to wait until the next execution
     * attempt for that patient wait attempt. The supplier can contain state but this can be called multiple times
     * from different wait attempts so each time this supplier is retrieved, the supplier should be in it's initial
     * state. If the returned supplier is null or if any duration from the supplier is null or negative then a
     * {@link PatientException} will be thrown.
     *
     * @return a {@link Supplier} of {@link Duration}s.
     */
    protected abstract Supplier<Duration> getRetryHandlerDurationSupplier();

    @Override
    public final <T> PatientResult<T> execute(Supplier<PatientExecutionResult<T>> patientExecutionResultSupplier,
                                              Duration maxDuration) {
        // Validate the arguments
        validate().that(patientExecutionResultSupplier).isNotNull();
        validate().that(maxDuration).isAtLeast(Duration.ZERO);
        // Start trying to get a successful result, use a do-while since a duration of ZERO should attempt once
        List<String> failedAttemptDescriptions = new ArrayList<>();
        Supplier<Duration> delayDurationSupplier = getRetryHandlerDurationSupplier();
        if (null == delayDurationSupplier) {
            throw new PatientException("Received a null duration supplier from the retry handler");
        }
        // Make the first next delay zero so that we don't sleep during the first loop
        Duration nextDelay = Duration.ZERO;
        Instant maxEndTime = Instant.now().plus(maxDuration);
        do {
            // Sleep for the next duration delay
            PatientSleep.sleepFor(nextDelay);
            // Get an execution attempt result and check it's status
            try {
                PatientExecutionResult<T> result = patientExecutionResultSupplier.get();
                if (null == result) {
                    throw new PatientException("Received a null PatientExecutionResult from the execution handler.");
                }
                if (result.isSuccess()) {
                    return PatientResult.pass(result.getResult());
                } else {
                    failedAttemptDescriptions.add(result.getFailedAttemptDescription());
                }
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
        return PatientResult.fail(failedAttemptDescriptions);
    }
}
