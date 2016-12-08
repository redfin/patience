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
import java.util.function.Consumer;

/**
 * An immutable container for results of running a Patient execution attempt.
 * It is similar to an {@link java.util.Optional} except that a null value
 * can be considered a valid, non-empty result.
 *
 * @param <T> the type of result this holds.
 */
public final class PatientExecutionResult<T> {

    private final T result;
    private final boolean isPresent;

    private PatientExecutionResult() {
        this.result = null;
        this.isPresent = false;
    }

    private PatientExecutionResult(T result) {
        this.result = result;
        this.isPresent = true;
    }

    /**
     * If this is not a valid result this will throw an exception. You
     * should call {@link #isPresent()} before calling this.
     *
     * @return the value contained in this result.
     * This may be null.
     *
     * @throws NoSuchElementException if there is no result present.
     */
    public T get() {
        if (!isPresent) {
            throw new NoSuchElementException("No result present");
        }
        return result;
    }

    /**
     * @return true if this was a success result.
     */
    public boolean isPresent() {
        return isPresent;
    }

    /**
     * If this is a valid result, apply the result to the given consumer.
     * If this isn't a valid result, do nothing.
     *
     * @param consumer the {@link Consumer} to apply a valid result to.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if consumer is null.
     */
    public void ifPresent(Consumer<T> consumer) {
        Validity.require().that(consumer).isNotNull();
        if (isPresent) {
            consumer.accept(result);
        }
    }

    /**
     * @param <T> the type of the returned result instance.
     *
     * @return a new, unsuccessful {@link PatientExecutionResult} instance.
     */
    public static <T> PatientExecutionResult<T> empty() {
        return new PatientExecutionResult<>();
    }

    /**
     * @param result the value to store in this successful result instance.
     *               May be null.
     * @param <T>    the type of the returned result instance.
     *
     * @return a new, successful {@link PatientExecutionResult} instance with the given value.
     */
    public static <T> PatientExecutionResult<T> of(T result) {
        return new PatientExecutionResult<>(result);
    }
}
