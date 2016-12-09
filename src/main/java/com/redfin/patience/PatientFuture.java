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

/**
 * This interface defines the extraction of a value from
 * the Patient library. These methods are intended to be the
 * terminal calls of any wait.
 *
 * @param <T> the type of object returned from the given callable.
 */
public interface PatientFuture<T> {

    /**
     * Attempt to retrieve a valid result from the future instance. If a
     * valid result is found within the default timeout form the {@link PatientWait} object,
     * then return it. If not, then a {@link PatientTimeoutException} will be thrown.
     *
     * @return the first value from the set callable that passes the given predicate test.
     *
     * @throws ArithmeticException     if an overflow occurs when converting the timeout duration
     *                                 to milliseconds and nanoseconds for the sleepFor duration.
     * @throws PatientTimeoutException if no valid result is found within the given timeout.
     * @throws PatientException        if an illegal internal state occurs during the retry attempts
     *                                 or a thread {@link InterruptedException} is thrown while sleeping.
     * @throws RuntimeException        if an unhandled error occurs during execution by the
     *                                 {@link PatientExecutionHandler}.
     */
    T get();

    /**
     * Attempt to retrieve a valid result from the future instance. If a
     * valid result is found within the given timeout form the {@link PatientWait} object,
     * then return it. If not, then a {@link PatientTimeoutException} will be thrown.
     *
     * @param timeout the link {@link Duration} timeout to use.
     *                May not be null.
     *                A duration of zero simply means make one attempt and then stop.
     *
     * @return the first value from the set callable that passes the given predicate test.
     *
     * @throws ArithmeticException     if an overflow occurs when converting the timeout duration
     *                                 to milliseconds and nanoseconds for the sleepFor duration.
     * @throws PatientTimeoutException if no valid result is found within the given timeout.
     * @throws PatientException        if an illegal internal state occurs during the retry attempts
     *                                 or a thread {@link InterruptedException} is thrown while sleeping.
     * @throws RuntimeException        if an unhandled error occurs during execution by the
     *                                 {@link PatientExecutionHandler}.
     */
    T get(Duration timeout);
}
