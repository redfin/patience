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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.redfin.validity.Validity.validate;

/**
 * A PatientTimeoutException is an unchecked exception. It is intended to signal that
 * a timeout was reached without receiving a valid value.
 */
public final class PatientTimeoutException
           extends RuntimeException {

    static final long serialVersionUID = 3L;

    private final List<String> attemptDescriptions;

    /**
     * Constructs a new patience exception with the specified detail message,
     * and the list of (string) descriptions of the unsuccessful attempts.
     * The cause will be {@code null}.
     *
     * @param message             the detail message.
     *                            May be null.
     * @param attemptDescriptions the list of String descriptions of the invalid
     *                            results when waiting.
     *                            May not be null or empty.
     *
     * @throws IllegalArgumentException if attemptDescriptions is null.
     */
    public PatientTimeoutException(String message,
                                   List<String> attemptDescriptions) {
        super(message);
        validate().that(attemptDescriptions).isNotEmpty();
        // Make a copy of the list and make it unmodifiable
        List<String> newList = new ArrayList<>(attemptDescriptions.size());
        newList.addAll(attemptDescriptions);
        this.attemptDescriptions = Collections.unmodifiableList(newList);
    }

    /**
     * @return the saved number of unsuccessful attempts
     */
    public int getAttemptsCount() {
        return attemptDescriptions.size();
    }

    /**
     * The returned list will contain the descriptions of the unsuccessful results.
     * If no descriptions were given then the list will be empty.
     * The returned list is unmodifiable.
     *
     * @return the unmodifiable List of String descriptions of the unsuccessful results.
     */
    public List<String> getAttemptDescriptions() {
        return attemptDescriptions;
    }
}
