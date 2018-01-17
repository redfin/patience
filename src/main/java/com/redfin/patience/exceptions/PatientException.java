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

package com.redfin.patience.exceptions;

import com.redfin.patience.PatientExecutable;

/**
 * A PatientException is an unchecked exception. It is intended
 * to signal that a problem has occurred within the Patient library itself.
 * This does not indicate a timeout (see {@link PatientException}), an issue
 * thrown by executing the supplied {@link PatientExecutable} or filtering {@link java.util.function.Predicate}
 * (see {@link PatientExecutionException}, or a thread interruption while sleeping
 * (see {@link PatientInterruptedException}).
 */
public final class PatientException
           extends RuntimeException {

    static final long serialVersionUID = 5L;

    /**
     * Constructs a new patience exception with {@code null} as its
     * detail message and cause.
     */
    public PatientException() {
        super();
    }

    /**
     * Constructs a new patience exception with the specified detail message.
     * The cause will be {@code null}.
     *
     * @param message the detail message. If {@code null} then it is the same
     *                as calling {@link #PatientException()}.
     */
    public PatientException(String message) {
        super(message);
    }

    /**
     * Constructs a new patience exception with the specified cause.
     * The message will be either null, for a null cause, or the
     * {@link Object#toString()} method of the given cause.
     *
     * @param cause the cause of the exception. If {@code null} then it is the
     *              same as calling {@link #PatientException()}.
     */
    public PatientException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new patience exception with the specified detail message and
     * cause.
     *
     * @param message the detail message. If {@code null} then it is the same
     *                as calling {@link #PatientException(Throwable)}.
     * @param cause   the cause of the exception. If {@code null} then it is the
     *                same as calling {@link #PatientException(String)}.
     */
    public PatientException(String message,
                            Throwable cause) {
        super(message, cause);
    }
}
