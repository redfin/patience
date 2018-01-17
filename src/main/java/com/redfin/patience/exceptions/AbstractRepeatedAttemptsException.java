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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.redfin.validity.Validity.validate;

/**
 * An AbstractRepeatedAttemptsException is the base class for
 * exceptions in the Patient library when it tries to
 * successfully get a value but runs out of attempts.<br>
 * <br>
 * It contains a list of string descriptions for all of
 * the unsuccessful attempts.
 */
public abstract class AbstractRepeatedAttemptsException
              extends RuntimeException {

    private final List<String> failedAttemptsDescriptions;

    /**
     * @param message                    the String message for the exception.
     *                                   May be null.
     * @param failedAttemptsDescriptions the List of String descriptions for each of
     *                                   the unsuccessful attempts.
     *                                   May not be null or empty.
     *
     * @throws IllegalArgumentException if failedAttemptsDescriptions is null or empty.
     */
    public AbstractRepeatedAttemptsException(String message,
                                             List<String> failedAttemptsDescriptions) {
        super(message);
        validate().withMessage("Cannot use a null list")
                  .that(failedAttemptsDescriptions)
                  .isNotNull();
        this.failedAttemptsDescriptions = Collections.unmodifiableList(new ArrayList<>(failedAttemptsDescriptions));
        validate().withMessage("Cannot use an empty list")
                  .that(failedAttemptsDescriptions)
                  .isNotEmpty();
    }

    /**
     * @return the number of failed attempts.
     */
    public int getFailedAttemptsCount() {
        return failedAttemptsDescriptions.size();
    }

    /**
     * @return an unmodifiable copy of the list of failed attempts descriptions.
     */
    public List<String> getFailedAttemptsDescriptions() {
        return failedAttemptsDescriptions;
    }
}
