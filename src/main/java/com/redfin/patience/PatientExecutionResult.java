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

import java.util.NoSuchElementException;

/**
 * An immutable container for results of running a Patient execution attempt.
 * It is similar to an {@link java.util.Optional} except that a null value
 * can be considered a valid, non-empty result.
 *
 * @param <T> the type of result this holds.
 */
public final class PatientExecutionResult<T> {

    private final T result;
    private final String failureDescription;

    private PatientExecutionResult(T result, String failureDescription) {
        this.result = result;
        this.failureDescription = failureDescription;
    }

    /**
     * If this is not a valid result this will throw an exception. You
     * should call {@link #wasSuccessful()} before calling this.
     *
     * @return the value contained in this result.
     * This may be null.
     *
     * @throws NoSuchElementException if this is a failure result.
     */
    public T getSuccessResult() {
        if (null != failureDescription) {
            throw new NoSuchElementException("Cannot get a success value from a failed result");
        }
        return result;
    }

    /**
     * If this is a valid result this will throw an exception. You
     * should call {@link #wasSuccessful()} before calling this.
     *
     * @return the String description of the invalid result or swallowed
     * exception.
     *
     * @throws NoSuchElementException if this is a successful result.
     */
    public String getFailureDescription() {
        if (null == failureDescription) {
            throw new NoSuchElementException("Cannot get a failure description from a success result");
        }
        return failureDescription;
    }

    /**
     * @return true if this was a success result.
     */
    public boolean wasSuccessful() {
        return null == failureDescription;
    }

    /**
     * @param description the String description of the invalid result or swallowed exception.
     *                    May not be null.
     * @param <T>         the type of the returned result instance.
     *
     * @return a new, unsuccessful {@link PatientExecutionResult} instance.
     *
     * @throws IllegalArgumentException if description is null.
     */
    public static <T> PatientExecutionResult<T> failure(String description) {
        Validity.require().that(description).isNotNull();
        return new PatientExecutionResult<>(null, description);
    }

    /**
     * @param result the value to store in this successful result instance.
     *               May be null.
     * @param <T>    the type of the returned result instance.
     *
     * @return a new, successful {@link PatientExecutionResult} instance with the given value.
     */
    public static <T> PatientExecutionResult<T> success(T result) {
        return new PatientExecutionResult<>(result, null);
    }
}
