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

/**
 * A PatientException is an unchecked exception. It is intended
 * to signal that an unexpected Throwable was caught while executing
 * the supplied {@link Executable} or filtering the results with
 * the supplied {@link java.util.function.Predicate}.
 */
public final class PatientExecutionException
           extends RuntimeException {

    static final long serialVersionUID = 3L;

    /**
     * Constructs a new patience execution exception with {@code null} as its
     * detail message and cause.
     */
    public PatientExecutionException() {
        super();
    }

    /**
     * Constructs a new patience execution exception with the specified detail message.
     * The cause will be {@code null}.
     *
     * @param message the detail message. If {@code null} then it is the same
     *                as calling {@link #PatientExecutionException()}.
     */
    public PatientExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs a new patience execution exception with the specified detail message and
     * cause.
     *
     * @param message the detail message. If {@code null} then it is the same
     *                as calling {@link #PatientExecutionException(Throwable)}.
     * @param cause   the cause of the exception. If {@code null} then it is the
     *                same as calling {@link #PatientExecutionException(String)}.
     */
    public PatientExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new patience execution exception with the specified cause.
     * The message will be {@code null}.
     *
     * @param cause the cause of the exception. If {@code null} then it is the
     *              same as calling {@link #PatientExecutionException()}.
     */
    public PatientExecutionException(Throwable cause) {
        super(cause);
    }
}
