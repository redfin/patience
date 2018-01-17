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

/**
 * A PatientInterruptedException is an unchecked exception. It is intended
 * to signal that an {@link InterruptedException} was thrown while manually
 * blocking a thread for the Patient library without being a checked exception.
 */
public final class PatientInterruptedException
           extends RuntimeException {

    static final long serialVersionUID = 5L;

    /**
     * Constructs a new patience exception with {@code null} as its
     * detail message and cause.
     */
    public PatientInterruptedException() {
        super();
    }

    /**
     * Constructs a new patience exception with the specified detail message.
     * The cause will be {@code null}.
     *
     * @param message the detail message. If {@code null} then it is the same
     *                as calling {@link #PatientInterruptedException()}.
     */
    public PatientInterruptedException(String message) {
        super(message);
    }

    /**
     * Constructs a new patience exception with the specified cause.
     * The message will be null, if the cause is null, or the result of
     * calling {@link Object#toString()} on the given cause.
     *
     * @param cause the cause of the exception. If {@code null} then it is the
     *              same as calling {@link #PatientInterruptedException()}.
     */
    public PatientInterruptedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new patience exception with the specified detail message and
     * cause.
     *
     * @param message the detail message. If {@code null} then it is the same
     *                as calling {@link #PatientInterruptedException(Throwable)}.
     * @param cause   the cause of the exception. If {@code null} then it is the
     *                same as calling {@link #PatientInterruptedException(String)}.
     */
    public PatientInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
