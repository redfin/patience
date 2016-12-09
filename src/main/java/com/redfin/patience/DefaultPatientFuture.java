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

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * An immutable container containing a {@link PatientWait} configuration instance
 * and a {@link Callable} value supplier. This implements the {@link PatientFuture}
 * interface so values can be extracted directly using the default filter, or a
 * custom filter can be given which will result in a {@link FilteredPatientFuture}
 * instance being returned.
 *
 * @param <T> the type of object returned from this object.
 */
public final class DefaultPatientFuture<T> implements PatientFuture<T> {

    private static final Predicate<?> NON_NULL_NON_FALSE_PREDICATE = t -> null != t && (!(t instanceof Boolean) || (Boolean) t);

    private final PatientWait wait;
    private final Callable<T> callable;

    /**
     * Create a new {@link DefaultPatientFuture} instance with the given
     * {@link PatientWait} and {@link Predicate} values.
     *
     * @param wait     the {@link PatientWait} instance containing the configuration
     *                 for this {@link DefaultPatientFuture} instance.
     *                 May not be null.
     * @param callable the {@link Callable} instance for retrieving values for this
     *                 {@link DefaultPatientFuture} instance.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if wait or filter are null.
     */
    DefaultPatientFuture(PatientWait wait, Callable<T> callable) {
        this.wait = Validity.require().that(wait).isNotNull();
        this.callable = Validity.require().that(callable).isNotNull();
    }

    /**
     * @param filter the {@link Predicate} to be used to verify values from
     *               the given callable.
     *               May not be null.
     *
     * @return a {@link FilteredPatientFuture} with the given callable, wait,
     * and filter.
     *
     * @throws IllegalArgumentException if filter is null.
     */
    public FilteredPatientFuture<T> withFilter(Predicate<T> filter) {
        Validity.require().that(filter).isNotNull();
        return new FilteredPatientFuture<>(wait, callable, filter);
    }

    @Override
    public T get() {
        return get(wait.getDefaultTimeout());
    }

    @Override
    public T get(Duration timeout) {
        Validity.require().that(timeout).isGreaterThanOrEqualTo(Duration.ZERO);
        return withFilter(getNonNullNonFalsePredicate()).get(timeout);
    }

    @SuppressWarnings("unchecked")
    private Predicate<T> getNonNullNonFalsePredicate() {
        return (Predicate<T>) NON_NULL_NON_FALSE_PREDICATE;
    }
}
