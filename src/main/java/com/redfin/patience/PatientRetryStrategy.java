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
import java.util.function.Supplier;

/**
 * An interface representing a customization point for the Patience library.
 * An implementation of this interface needs to be sure that it can be re-used.
 * It is where the actual retry logic customizations can occur. For example, if
 * you wanted a simple fixed delay versus exponential back off between attempts
 * of running the given code to be waited upon.
 * Each call to a {@link PatientFuture#get(Duration)} method will make exactly one
 * call to the {@link #execute(Duration, Supplier)} method. Each execute call should
 * be considered a fresh wait attempt.
 */
@FunctionalInterface
public interface PatientRetryStrategy {

    /**
     * Attempt to retrieve a value from the resultSupplier within the given timeout
     * as per the implementing class's specification.
     *
     * @param timeout        the {@link Duration} timeout for this call. A value of zero
     *                       should indicate a single attempt to retrieve a valid result.
     *                       May not be null or negative.
     * @param resultSupplier the {@link Supplier} of {@link PatientExecutionResult}
     *                       objects. This should be called once per retry attempt and
     *                       then be inspected if it contains a result value.
     *                       May not be null.
     * @param <T>            the type of object returned from the result supplier.
     *
     * @return a valid value from the first successful result from the result supplier or
     * throw a {@link PatientTimeoutException} if no valid value is found within the
     * given timeout.
     *
     * @throws IllegalArgumentException    if timeout is null, timeout is negative, or if resultSupplier
     *                                     is null.
     * @throws PatientTimeoutException     if no value {@link PatientExecutionResult} is returned from
     *                                     the supplier within the given timeout.
     * @throws PatientInterruptedException if the executing thread receives an {@link InterruptedException}
     *                                     while the Patient methods are manually blocking or sleeping.
     * @throws PatientException            if the resultSupplier ever returns a null value, or any other
     *                                     illegal state occurs while performing the wait.
     */
    <T> T execute(Duration timeout, Supplier<PatientExecutionResult<T>> resultSupplier);
}
