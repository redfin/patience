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

import java.util.function.Predicate;

/**
 * A PatientExecutionHandler is one of the main customization points in the
 * Patience library. An instance of this type defines the behavior around
 * extracting a value from an {@link Executable} and testing that value with
 * a {@link Predicate}.
 */
@FunctionalInterface
public interface PatientExecutionHandler {

    /**
     * Extract a value from the given {@link Executable} and test the retrieved value with
     * the given {@link Predicate} filter. If the value passes the predicate test then return
     * a passing {@link PatientExecutionResult} with the value. If it fails the test then it
     * should return a failing {@link PatientExecutionResult} with a description of the attempt.
     * Any unexpected errors or exceptions should be wrapped in a {@link PatientExecutionException}
     * and thrown.
     *
     * @param executable the {@link Executable} to use to retrieve a value.
     *                   May not be null.
     * @param filter     the {@link Predicate} to test the retrieved value with.
     *                   May not be null.
     * @param <T>        the type of the value from the executable.
     *
     * @return a {@link PatientExecutionResult} instance.
     *
     * @throws IllegalArgumentException  if executable or filter are null.
     * @throws PatientExecutionException if an unexpected Throwable is thrown during the
     *                                   execution of executable or testing the returned results with the given filter.
     */
    <T> PatientExecutionResult<T> execute(Executable<T> executable,
                                          Predicate<T> filter);
}
