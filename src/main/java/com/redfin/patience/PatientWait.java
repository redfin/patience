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
 * A {@link PatientWait} instance is intended to be an immutable, pre-configured,
 * re-usable starting point for waiting for expected conditions. The normal way to
 * create one is via the static builder method {@link #builder()} and the subsequent
 * {@link PatientWaitBuilder#build()} method. Once a PatientWait instance has been
 * created it can be used repeatedly to create {@link PatientCompletableFuture} and
 * {@link PatientFuture} objects with the same initial configurations.
 */
public final class PatientWait {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constants
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final Predicate<?> NON_NULL_NON_FALSE_PREDICATE = t -> null != t && (!(t instanceof Boolean) || (Boolean) t);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance fields
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final Duration initialDelay;
    private final Duration defaultTimeout;
    private final PatientRetryStrategy patientRetryStrategy;
    private final PatientExecutionHandler patientExecutionHandler;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Create a new {@link PatientWait} instance with the given values.
     *
     * @param initialDelay            the {@link Duration} to wait before attempting the first execution
     *                                of the code given to the created {@link PatientCompletableFuture}s.
     *                                May not be null or negative. A value of zero means don't wait
     *                                before attempting the first execution.
     * @param defaultTimeout          the default {@link Duration} maximum wait time for each {@link PatientFuture}
     *                                instances created from this instance. This is used in the case that the
     *                                {@link PatientFuture#get()} method is called.
     *                                May not be null or negative.
     *                                A value of zero means only attempt one execution.
     * @param patientRetryStrategy    the {@link PatientRetryStrategy} for all {@link PatientCompletableFuture} and
     *                                {@link PatientFuture}s created from this {@link PatientWait}.
     *                                May not be null.
     * @param patientExecutionHandler the {@link PatientExecutionHandler} for all {@link PatientCompletableFuture}
     *                                and {@link PatientFuture}s created from this {@link PatientWait}.
     *                                May not be null.
     */
    public PatientWait(Duration initialDelay,
                       Duration defaultTimeout,
                       PatientRetryStrategy patientRetryStrategy,
                       PatientExecutionHandler patientExecutionHandler) {
        this.initialDelay = Validity.require().that(initialDelay).isGreaterThanOrEqualTo(Duration.ZERO);
        this.defaultTimeout = Validity.require().that(defaultTimeout).isGreaterThanOrEqualTo(Duration.ZERO);
        this.patientRetryStrategy = Validity.require().that(patientRetryStrategy).isNotNull();
        this.patientExecutionHandler = Validity.require().that(patientExecutionHandler).isNotNull();
    }

    /**
     * @return the given initial delay {@link Duration}.
     */
    public Duration getInitialDelay() {
        return initialDelay;
    }

    /**
     * @return the given default timeout {@link Duration}.
     */
    public Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * @return the given {@link PatientRetryStrategy}.
     */
    public PatientRetryStrategy getRetryStrategy() {
        return patientRetryStrategy;
    }

    /**
     * @return the given {@link PatientExecutionHandler}.
     */
    public PatientExecutionHandler getExecutionHandler() {
        return patientExecutionHandler;
    }

    /**
     * @param filter the {@link Predicate} used to test the results from the callable given
     *               to the {@link PatientCompletableFuture#from(Callable)} method.
     *               May not be null.
     * @param <T>    the type of the predicate.
     *
     * @return a {@link PatientCompletableFuture} with this {@link PatientWait} instance
     * and the given filter.
     *
     * @throws IllegalArgumentException if filter is null.
     */
    public <T> PatientCompletableFuture<T> withFilter(Predicate<T> filter) {
        return new PatientCompletableFuture<>(this, filter);
    }

    /**
     * This is the same as calling {@link #withFilter} with a predicate that returns true
     * for all objects that are not null and not Boolean false and then calling the resulting
     * {@link PatientCompletableFuture#from(Callable)} with the given callable.
     *
     * @param callable the {@link Callable} with which to create the {@link PatientFuture}.
     *                 May not be null.
     * @param <T>      the type of the value returned from the callable object.
     *
     * @return a new {@link PatientFuture} instance with this {@link PatientWait} instance,
     * the given callable, and a predicate that returns true for all non-null, non-false values.
     */
    public <T> PatientFuture<T> from(Callable<T> callable) {
        PatientCompletableFuture<T> pcf = withFilter(getNonNullNonFalsePredicate());
        return pcf.from(callable);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Static methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @SuppressWarnings("unchecked")
    private static <T> Predicate<T> getNonNullNonFalsePredicate() {
        return (Predicate<T>) NON_NULL_NON_FALSE_PREDICATE;
    }

    /**
     * @return a new {@link PatientWaitBuilder} instance with the default initial values set.
     */
    public static PatientWaitBuilder builder() {
        return new PatientWaitBuilder();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Builder
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * A class used to create {@link PatientWait} instances.
     */
    public static final class PatientWaitBuilder {

        private static final PatientRetryStrategy DEFAULT_STRATEGY = PatientRetryStrategies.withFixedDelay(Duration.ZERO);
        private static final PatientExecutionHandler DEFAULT_HANDLER = PatientExecutionHandlers.simpleHandler();

        /*
         * Start with sane defaults
         */

        private Duration initialDelay = Duration.ZERO;
        private Duration defaultTimeout = Duration.ZERO;
        private PatientRetryStrategy patientRetryStrategy = DEFAULT_STRATEGY;
        private PatientExecutionHandler patientExecutionHandler = DEFAULT_HANDLER;

        /**
         * @param initialDelay the {@link Duration} to wait before execution of any
         *                     {@link PatientFuture} generated from the {@link PatientWait} this builder creates.
         *                     May not be null or negative.
         *                     A zero initial delay means that it won't wait before executing when called.
         *
         * @return a self reference.
         */
        public PatientWaitBuilder withInitialDelay(Duration initialDelay) {
            this.initialDelay = Validity.require().that(initialDelay).isGreaterThanOrEqualTo(Duration.ZERO);
            return this;
        }

        /**
         * @param defaultTimeout the {@link Duration} default timeout to use in
         *                       the case of the {@link PatientFuture#get()} for any
         *                       {@link PatientFuture} generated from the {@link PatientWait}
         *                       this builder creates.
         *                       May not be null, or negative.
         *                       A zero timeout means that only a single execution is attempted.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if defaultTimeout is null, zero, or negative.
         */
        public PatientWaitBuilder withDefaultTimeout(Duration defaultTimeout) {
            this.defaultTimeout = Validity.require().that(defaultTimeout).isGreaterThanOrEqualTo(Duration.ZERO);
            return this;
        }

        /**
         * @param patientRetryStrategy the {@link PatientRetryStrategy} to use.
         *                             May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if patientRetryStrategy is null.
         */
        public PatientWaitBuilder withRetryStrategy(PatientRetryStrategy patientRetryStrategy) {
            this.patientRetryStrategy = Validity.require().that(patientRetryStrategy).isNotNull();
            return this;
        }

        /**
         * @param patientExecutionHandler the {@link PatientExecutionHandler} to use.
         *                                May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if patientExecutionHandler is null.
         */
        public PatientWaitBuilder withExecutionHandler(PatientExecutionHandler patientExecutionHandler) {
            this.patientExecutionHandler = Validity.require().that(patientExecutionHandler).isNotNull();
            return this;
        }

        /**
         * @return the {@link PatientWait} instance with the current values of this
         * {@link PatientWaitBuilder} instance.
         */
        public PatientWait build() {
            return new PatientWait(initialDelay,
                                   defaultTimeout,
                                   patientRetryStrategy,
                                   patientExecutionHandler);
        }
    }
}
