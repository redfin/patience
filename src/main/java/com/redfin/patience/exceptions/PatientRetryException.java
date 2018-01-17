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

import java.util.List;

/**
 * A PatientRetryException is an unchecked exception. It is intended to signal that
 * a maximum number of retries was reached without receiving a valid value.
 */
public final class PatientRetryException
           extends AbstractRepeatedAttemptsException {

    static final long serialVersionUID = 5L;

    /**
     * Constructs a new patient retry exception with the specified detail message,
     * and the list of (string) descriptions of the unsuccessful attempts.
     * The cause will be {@code null}.
     *
     * @param message                    the detail message.
     *                                   May be null.
     * @param failedAttemptsDescriptions the list of String descriptions of the invalid
     *                                   results when waiting.
     *                                   May not be null or empty.
     *
     * @throws IllegalArgumentException if failedAttemptsDescriptions is null or empty.
     */
    public PatientRetryException(String message,
                                 List<String> failedAttemptsDescriptions) {
        super(message, failedAttemptsDescriptions);
    }
}
