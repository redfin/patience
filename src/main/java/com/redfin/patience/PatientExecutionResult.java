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

import static com.redfin.validity.Validity.*;

/**
 * A PatientExecutionResult is an immutable object that signifies the outcome of a single
 * execution attempt while patiently waiting and is returned by a {@link PatientExecutionHandler}.
 * It will contain either a result or a String description of the failed attempt.
 *
 * @param <T> the type of the result.
 */
public final class PatientExecutionResult<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Fields & Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final T result;
    private final String failedAttemptDescription;

    private PatientExecutionResult(T result,
                                   String failedAttemptDescription) {
        validate().withMessage("Cannot have a PatientResult with a non-null result and a non-null failed attempt description")
                  .that(null != result && null != failedAttemptDescription)
                  .isFalse();
        this.result = result;
        this.failedAttemptDescription = failedAttemptDescription;
    }

    /**
     * @return true if this is a successful result or false if it is not.
     */
    public boolean isSuccess() {
        return null == failedAttemptDescription;
    }

    /**
     * @return the result value if {@link #isSuccess()} returns true. This may
     * be null.
     *
     * @throws UnsupportedOperationException if {@link #isSuccess()} returns false.
     */
    public T getResult() {
        if (isSuccess()) {
            return result;
        } else {
            throw new UnsupportedOperationException("Cannot get the result from an unsuccessful PatientExecutionResult.");
        }
    }

    /**
     * @return the String description of the failed execution attempt if {@link #isSuccess()} returns false.
     *
     * @throws UnsupportedOperationException if {@link #isSuccess()} returns true.
     */
    public String getFailedAttemptDescription() {
        if (isSuccess()) {
            throw new UnsupportedOperationException("Cannot get the failed attempt description from a successful PatientExecutionResult.");
        } else {
            return failedAttemptDescription;
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Static Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param result the result that is wrapped by this object.
     * @param <T>    the type of the result.
     *
     * @return a new {@link PatientExecutionResult} instance that is successful and
     * has the given result.
     */
    public static <T> PatientExecutionResult<T> pass(T result) {
        return new PatientExecutionResult<>(result, null);
    }

    /**
     * @param failedAttemptDescription the String description of the failed attempt.
     * @param <T>                      the type of the result.
     *
     * @return a new {@link PatientExecutionResult} instance that is not successful and
     * has the given failure description.
     */
    public static <T> PatientExecutionResult<T> fail(String failedAttemptDescription) {
        if (null == failedAttemptDescription || failedAttemptDescription.isEmpty()) {
            failedAttemptDescription = "Failed execution attempt";
        }
        return new PatientExecutionResult<>(null, failedAttemptDescription);
    }
}
