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

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.*;

/**
 * A PatientWait instance is a factory for {@link PatientWaitFuture} instances.
 * It is immutable and thread safe.
 */
public final class PatientWait {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constants
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final Supplier<String> DEFAULT_FAILURE_MESSAGE_SUPPLIER;
    private static final Predicate<?> DEFAULT_FILTER;

    static {
        DEFAULT_FAILURE_MESSAGE_SUPPLIER = () -> "Didn't receive a valid result from the executable within the given timeout";
        DEFAULT_FILTER = t -> null != t && (!(t instanceof Boolean) || (Boolean) t);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Fields & Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final PatientSleep sleep;
    private final Duration initialDelay;
    private final Duration defaultTimeout;
    private final PatientExecutionHandler executionHandler;
    private final PatientDelaySupplierFactory delaySupplierFactory;

    /**
     * Create a {@link PatientWait} instance with the given default values used when
     * creating {@link PatientWaitFuture} instances via the {@link #from(PatientExecutable)} method.
     *
     * @param sleep                the {@link PatientSleep} implementation to make the current thread sleep.
     *                             May not be null.
     * @param initialDelay         the {@link Duration} time to sleep before trying to execute the {@link PatientExecutable}
     *                             given to the {@link #from} method.
     *                             May not be null or negative.
     * @param defaultTimeout       the {@link Duration} default timeout that is used for
     *                             the {@link PatientWaitFuture#get()} method.
     *                             May not be null or negative.
     * @param executionHandler     the {@link PatientExecutionHandler} to use for generated {@link PatientWaitFuture}
     *                             instances.
     *                             May not be null.
     * @param delaySupplierFactory the {@link PatientDelaySupplierFactory} used between unsuccessful attempts to get a value.
     *                             May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if either initialDelay or defaultTime
     *                                  are negative.
     */
    public PatientWait(PatientSleep sleep,
                       Duration initialDelay,
                       Duration defaultTimeout,
                       PatientExecutionHandler executionHandler,
                       PatientDelaySupplierFactory delaySupplierFactory) {
        this.sleep = validate().that(sleep).isNotNull();
        this.initialDelay = validate().that(initialDelay).isAtLeast(Duration.ZERO);
        this.defaultTimeout = validate().that(defaultTimeout).isAtLeast(Duration.ZERO);
        this.executionHandler = validate().that(executionHandler).isNotNull();
        this.delaySupplierFactory = validate().that(delaySupplierFactory).isNotNull();
    }

    /**
     * @return the given {@link PatientSleep} instance.
     */
    public PatientSleep getSleep() {
        return sleep;
    }

    /**
     * @return the given {@link Duration} for the length of time to sleep before attempting
     * to get a result.
     */
    public Duration getInitialDelay() {
        return initialDelay;
    }

    /**
     * @return the given {@link Duration} default timeout to use as a max timeout when
     * calling the {@link PatientWaitFuture#get} method.
     */
    public Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * @return the given {@link PatientExecutionHandler}.
     */
    public PatientExecutionHandler getExecutionHandler() {
        return executionHandler;
    }

    /**
     * @return the given {@link PatientDelaySupplierFactory}.
     */
    public PatientDelaySupplierFactory getDelaySupplierFactory() {
        return delaySupplierFactory;
    }

    /**
     * Generate a {@link PatientWaitFuture} instance from this {@link PatientWait} instance.
     * The {@link PatientWaitFuture} will have the default filter {@link Predicate}.
     * The default filter return true for any non-null value, unless the value is a Boolean in which
     * case it will return true only if the value is non-null and not false.
     *
     * @param executable the {@link PatientExecutable} to try to retrieve a value from.
     *                   May not be null.
     * @param <T>        the type returned from the given executable.
     *
     * @return a {@link PatientWaitFuture} instance with the given executable and values.
     *
     * @throws IllegalArgumentException if executable is null.
     */
    public <T> PatientWaitFuture<T> from(PatientExecutable<T> executable) {
        validate().that(executable).isNotNull();
        return new PatientWaitFuture<>(sleep,
                                       initialDelay,
                                       defaultTimeout,
                                       executionHandler,
                                       delaySupplierFactory,
                                       executable,
                                       getDefaultFilter(),
                                       DEFAULT_FAILURE_MESSAGE_SUPPLIER);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Static Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Create a {@link Builder} instance for fluently generating a {@link PatientWait} instance
     * with defaults for non-specified arguments.
     * <br>
     * The default is a wait that executes only once and does not catch any throwable thrown by the
     * execution of a given {@link PatientExecutable} or when testing values with a supplied filter.
     *
     * @return a new {@link Builder} instance.
     */
    public static PatientWait.Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> getDefaultFilter() {
        return (Predicate<T>) DEFAULT_FILTER;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Builder
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * A mutable builder class used to generate a {@link PatientWait} instance.
     */
    public static final class Builder {

        private PatientSleep sleep = Thread::sleep;
        private Duration initialDelay = Duration.ZERO;
        private Duration defaultTimeout = Duration.ZERO;
        private PatientExecutionHandler executionHandler = PatientExecutionHandlers.simple();
        private PatientDelaySupplierFactory delaySupplierFactory = PatientDelaySuppliers.fixed(Duration.ZERO);

        /**
         * Set the {@link PatientSleep} for {@link PatientWait} instances generated by this {@link Builder}.
         *
         * @param sleep the {@link PatientSleep} instance to use for making the thread wait.
         *              May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if sleep is null.
         */
        public Builder withSleep(PatientSleep sleep) {
            this.sleep = validate().that(sleep).isNotNull();
            return this;
        }

        /**
         * Set the initial delay for {@link PatientWait} instances generated by this {@link Builder}.
         *
         * @param initialDelay the {@link Duration} to be used as an initial delay.
         *                     May not be null or negative.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if initialDelay is null or negative.
         */
        public Builder withInitialDelay(Duration initialDelay) {
            this.initialDelay = validate().that(initialDelay).isAtLeast(Duration.ZERO);
            return this;
        }

        /**
         * Set the default timeout for {@link PatientWait} instances generated by this {@link Builder}.
         *
         * @param defaultTimeout the {@link Duration} to be used as the default timeout to be used.
         *                       May not be null or negative.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if defaultTimeout is null or negative.
         */
        public Builder withDefaultTimeout(Duration defaultTimeout) {
            this.defaultTimeout = validate().that(defaultTimeout).isAtLeast(Duration.ZERO);
            return this;
        }

        /**
         * Set the execution handler for {@link PatientWait} instances generated by this {@link Builder}.
         *
         * @param executionHandler the {@link PatientExecutionHandler} to be used.
         *                         May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if executionHandler is null.
         */
        public Builder withExecutionHandler(PatientExecutionHandler executionHandler) {
            this.executionHandler = validate().that(executionHandler).isNotNull();
            return this;
        }

        /**
         * Set the delay supplier for the {@link PatientWait} instances generated by this {@link Builder}.
         *
         * @param delaySupplier the {@link PatientDelaySupplierFactory} to be used.
         *                      May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if delaySupplier is null.
         */
        public Builder withDelaySupplier(PatientDelaySupplierFactory delaySupplier) {
            this.delaySupplierFactory = validate().that(delaySupplier).isNotNull();
            return this;
        }

        /**
         * @return a new {@link PatientWait} instance with the given or default values.
         */
        public PatientWait build() {
            return new PatientWait(sleep,
                                   initialDelay,
                                   defaultTimeout,
                                   executionHandler,
                                   delaySupplierFactory);
        }
    }
}
