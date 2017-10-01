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

import java.util.List;

import static com.redfin.validity.Validity.*;

/**
 * A PatientResult is an immutable object that signifies the outcome of patiently
 * waiting and is returned from the used {@link PatientRetryHandler}.
 * It will have either the successful result or a list of String descriptions
 * of the failed attempts.
 *
 * @param <T> the type of the result.
 */
public final class PatientResult<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Fields & Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final T result;
    private final List<String> failedAttemptDescriptions;

    private PatientResult(T result,
                          List<String> failedAttemptDescriptions) {
        validate().withMessage("Cannot have a PatientResult with a non-null result and a non-null failed attempt descriptions")
                  .that(null != result && null != failedAttemptDescriptions)
                  .isFalse();
        this.result = result;
        this.failedAttemptDescriptions = failedAttemptDescriptions;
    }

    /**
     * @return true if this is a successful result or false otherwise.
     */
    public boolean isSuccess() {
        return null == failedAttemptDescriptions;
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
            throw new UnsupportedOperationException("Cannot get the result from an unsuccessful PatientResult.");
        }
    }

    /**
     * @return the {@link List} of String descriptions of the failed execution attempts if {@link #isSuccess()} returns false.
     *
     * @throws UnsupportedOperationException if {@link #isSuccess()} returns true.
     */
    public List<String> getFailedAttemptDescriptions() {
        if (isSuccess()) {
            throw new UnsupportedOperationException("Cannot get the list of failed attempt descriptions from a successful PatientResult.");
        } else {
            return failedAttemptDescriptions;
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Static Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param result the result value to be stored in the returned successful instance.
     * @param <T>    the type of the result.
     *
     * @return a new {@link PatientResult} instance that is successful and has the given result value.
     */
    public static <T> PatientResult<T> pass(T result) {
        return new PatientResult<>(result, null);
    }

    /**
     * @param failedAttemptDescriptions the {@link List} of String descriptions of the failed execution attempts.
     *                                  May not be null or empty.
     * @param <T>                       the type of the result.
     *
     * @return a new {@link PatientResult} instance that is not successful and has the
     * given list of string descriptions.
     *
     * @throws IllegalArgumentException if failedAttemptDescriptions is null or empty.
     */
    public static <T> PatientResult<T> fail(List<String> failedAttemptDescriptions) {
        validate().that(failedAttemptDescriptions).isNotEmpty();
        return new PatientResult<>(null, failedAttemptDescriptions);
    }
}
