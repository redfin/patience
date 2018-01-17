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

import static com.redfin.validity.Validity.validate;

/**
 * A PatientRetry instance is a factory for {@link PatientRetryFuture} instances.
 * It is immutable and thread safe.
 */
public final class PatientRetry {

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

    private final Sleep sleep;
    private final Duration initialDelay;
    private final int defaultNumberOfRetries;
    private final PatientExecutionHandler executionHandler;
    private final DelaySupplierFactory delaySupplierFactory;

    /**
     * Create a {@link PatientRetry} instance with the given default values used when
     * creating {@link PatientRetryFuture} instances via the {@link #from(PatientExecutable)} method.
     *
     * @param sleep                  the {@link Sleep} implementation to make the current thread sleep.
     *                               May not be null.
     * @param initialDelay           the {@link Duration} time to sleep before trying to execute the {@link PatientExecutable}
     *                               given to the {@link #from} method.
     *                               May not be null or negative.
     * @param defaultNumberOfRetries the defautl number of retries used for
     *                               the {@link PatientRetryFuture#get()} and {@link PatientRetryFuture#check()} methods.
     *                               May not be negative.
     * @param executionHandler       the {@link PatientExecutionHandler} to use for generated {@link PatientRetryFuture}
     *                               instances.
     *                               May not be null.
     * @param delaySupplierFactory   the {@link DelaySupplierFactory} used between unsuccessful attempts to get a value.
     *                               May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if either initialDelay or defaultTime
     *                                  are negative.
     */
    public PatientRetry(Sleep sleep,
                        Duration initialDelay,
                        int defaultNumberOfRetries,
                        PatientExecutionHandler executionHandler,
                        DelaySupplierFactory delaySupplierFactory) {
        this.sleep = validate().that(sleep).isNotNull();
        this.initialDelay = validate().that(initialDelay).isAtLeast(Duration.ZERO);
        this.defaultNumberOfRetries = validate().that(defaultNumberOfRetries).isAtLeast(0);
        this.executionHandler = validate().that(executionHandler).isNotNull();
        this.delaySupplierFactory = validate().that(delaySupplierFactory).isNotNull();
    }

    /**
     * @return the given {@link Sleep} instance.
     */
    public Sleep getSleep() {
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
     * @return the given int default number of retries used when
     * calling the {@link PatientRetryFuture#get} or {@link PatientRetryFuture#check()} methods.
     */
    public int getDefaultNumberOfRetries() {
        return defaultNumberOfRetries;
    }

    /**
     * @return the given {@link PatientExecutionHandler}.
     */
    public PatientExecutionHandler getExecutionHandler() {
        return executionHandler;
    }

    /**
     * @return the given {@link DelaySupplierFactory}.
     */
    public DelaySupplierFactory getDelaySupplierFactory() {
        return delaySupplierFactory;
    }

    /**
     * Generate a {@link PatientRetryFuture} instance from this {@link PatientRetry} instance.
     * The {@link PatientRetryFuture} will have the default filter {@link Predicate}.
     * The default filter return true for any non-null value, unless the value is a Boolean in which
     * case it will return true only if the value is non-null and not false.
     *
     * @param executable the {@link PatientExecutable} to try to retrieve a value from.
     *                   May not be null.
     * @param <T>        the type returned from the given executable.
     *
     * @return a {@link PatientRetryFuture} instance with the given executable and values.
     *
     * @throws IllegalArgumentException if executable is null.
     */
    public <T> PatientRetryFuture<T> from(PatientExecutable<T> executable) {
        validate().that(executable).isNotNull();
        return new PatientRetryFuture<>(sleep,
                                        initialDelay,
                                        defaultNumberOfRetries,
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
     * Create a {@link Builder} instance for fluently generating a {@link PatientRetry} instance
     * with defaults for non-specified arguments.
     * <br>
     * The default is a wait that executes only once and does not catch any throwable thrown by the
     * execution of a given {@link PatientExecutable} or when testing values with a supplied filter.
     *
     * @return a new {@link Builder} instance.
     */
    public static Builder builder() {
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
     * A mutable builder class used to generate a {@link PatientRetry} instance.
     */
    public static final class Builder {

        private Sleep sleep = Thread::sleep;
        private Duration initialDelay = Duration.ZERO;
        private int defaultNumberOfRetries = 0;
        private PatientExecutionHandler executionHandler = PatientExecutionHandlers.simple();
        private DelaySupplierFactory delaySupplierFactory = DelaySuppliers.fixed(Duration.ZERO);

        /**
         * Set the {@link Sleep} for {@link PatientRetry} instances generated by this {@link Builder}.
         *
         * @param sleep the {@link Sleep} instance to use for making the thread wait.
         *              May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if sleep is null.
         */
        public PatientRetry.Builder withSleep(Sleep sleep) {
            this.sleep = validate().that(sleep).isNotNull();
            return this;
        }

        /**
         * Set the initial delay for {@link PatientRetry} instances generated by this {@link Builder}.
         *
         * @param initialDelay the {@link Duration} to be used as an initial delay.
         *                     May not be null or negative.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if initialDelay is null or negative.
         */
        public PatientRetry.Builder withInitialDelay(Duration initialDelay) {
            this.initialDelay = validate().that(initialDelay).isAtLeast(Duration.ZERO);
            return this;
        }

        /**
         * Set the default maximum number of retries for {@link PatientRetry} instances generated by this {@link Builder}.
         *
         * @param defaultNumberOfRetries the int default maximum number of retries.
         *                               May not be negative.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if defaultTimeout is null or negative.
         */
        public PatientRetry.Builder withDefaultNumberOfRetries(int defaultNumberOfRetries) {
            this.defaultNumberOfRetries = validate().that(defaultNumberOfRetries).isAtLeast(0);
            return this;
        }

        /**
         * Set the execution handler for {@link PatientRetry} instances generated by this {@link Builder}.
         *
         * @param executionHandler the {@link PatientExecutionHandler} to be used.
         *                         May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if executionHandler is null.
         */
        public PatientRetry.Builder withExecutionHandler(PatientExecutionHandler executionHandler) {
            this.executionHandler = validate().that(executionHandler).isNotNull();
            return this;
        }

        /**
         * Set the delay supplier for the {@link PatientRetry} instances generated by this {@link Builder}.
         *
         * @param delaySupplier the {@link DelaySupplierFactory} to be used.
         *                      May not be null.
         *
         * @return a self reference.
         *
         * @throws IllegalArgumentException if delaySupplier is null.
         */
        public PatientRetry.Builder withDelaySupplier(DelaySupplierFactory delaySupplier) {
            this.delaySupplierFactory = validate().that(delaySupplier).isNotNull();
            return this;
        }

        /**
         * @return a new {@link PatientRetry} instance with the given or default values.
         */
        public PatientRetry build() {
            return new PatientRetry(sleep,
                                    initialDelay,
                                    defaultNumberOfRetries,
                                    executionHandler,
                                    delaySupplierFactory);
        }
    }
}
