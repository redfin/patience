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

import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * An immutable, incomplete, {@link PatientFuture} instance. A {@link PatientCompletableFuture}
 * holds the {@link PatientWait} instance, for waiting configuration, and a {@link Predicate}
 * instance, for testing results, until a {@link Callable} is given that allows for the
 * {@link PatientFuture} to be instantiated.
 *
 * @param <T> the type of object this can eventually return.
 */
public final class PatientCompletableFuture<T> {

    private final PatientWait wait;
    private final Predicate<T> filter;

    /**
     * Create a new {@link PatientCompletableFuture} instance with the given
     * {@link PatientWait} and {@link Predicate} values.
     *
     * @param wait   the {@link PatientWait} instance containing the configuration
     *               for this {@link PatientCompletableFuture} instance.
     *               May not be null.
     * @param filter the {@link Predicate} instance for validating values
     *               for this {@link PatientCompletableFuture} instance.
     *               May not be null.
     *
     * @throws IllegalArgumentException if wait or filter are null.
     */
    public PatientCompletableFuture(PatientWait wait, Predicate<T> filter) {
        this.wait = Validity.require().that(wait).isNotNull();
        this.filter = Validity.require().that(filter).isNotNull();
    }

    /**
     * @param callable the {@link Callable} instance that will be repeatedly called,
     *                 if necessary, to get a valid value.
     *                 May not be null.
     *
     * @return a new {@link PatientFuture} instance with the already set filter
     * and {@link PatientWait} objects and the given callable.
     *
     * @throws IllegalArgumentException if callable is null.
     */
    public PatientFuture<T> from(Callable<T> callable) {
        return new PatientFuture<>(wait,
                                   Validity.require().that(callable).isNotNull(),
                                   filter);
    }
}
