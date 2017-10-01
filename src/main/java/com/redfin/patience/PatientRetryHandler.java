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
 * An interface defining a customization point in the Patience library.
 * A PatientRetryHandler handles the checking of execution results and,
 * upon failure while still within the timeout, the behavior between the
 * failed execution and the next one.
 */
@FunctionalInterface
public interface PatientRetryHandler {

    /**
     * Get results from the given result supplier. If the execution result is a successful result,
     * then return a passing result with the given value. If the result is not a success, then
     * keep trying until either a passing result is received or the maximum timeout has been reached.
     * A maxDuration of zero implies that only a single execution result will be retrieved from the
     * supplier. The retry handler is responsible for the behavior of this (like the wait time between
     * attempts at retrieving execution results).
     *
     * @param resultSupplier the supplier of {@link PatientExecutionResult}s that will be used to
     *                       know if the execution was a success or failure.
     *                       May not be null.
     * @param maxDuration    the {@link Duration} maximum time to wait to get a successful result.
     *                       A duration of zero means to only get one execution result.
     *                       May not be null or negative.
     * @param <T>            the type of the value returned by the execution result.
     *
     * @return a passing result with a value if a passing execution result is found within the timeout or
     * a failing result with a list of failed execution descriptions if no passing execution result
     * is found.
     *
     * @throws IllegalArgumentException if resultSupplier or maxDuration are null or if maxDuration is negative.
     * @throws PatientException         if getting results from the resultSupplier ever throws, or any
     *                                  value from resultSupplier is ever null.
     */
    <T> PatientResult<T> execute(Supplier<PatientExecutionResult<T>> resultSupplier,
                                 Duration maxDuration);
}
