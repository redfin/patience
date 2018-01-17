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

package com.redfin.patience.executions;

import com.redfin.patience.PatientExecutable;
import com.redfin.patience.PatientExecutionHandler;
import com.redfin.patience.exceptions.PatientExecutionException;
import com.redfin.patience.PatientExecutionResult;
import com.redfin.validity.ValidityUtils;

import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * A simple implementation of the {@link PatientExecutionHandler}. It will simply
 * extract a value from the executable and test it with the predicate. Any Throwable
 * thrown from the executable or the predicate will set as the cause of a thrown
 * {@link PatientExecutionException}.
 */
public final class SimpleExecutionHandler
        implements PatientExecutionHandler {

    @Override
    public <T> PatientExecutionResult<T> execute(PatientExecutable<T> executable,
                                                 Predicate<T> filter) {
        validate().that(executable).isNotNull();
        validate().that(filter).isNotNull();
        try {
            // Extract a value and test the result
            T value = executable.execute();
            if (filter.test(value)) {
                return PatientExecutionResult.pass(value);
            } else {
                return PatientExecutionResult.fail(ValidityUtils.describe(value));
            }
        } catch (Throwable thrown) {
            // Unexpected throwable caught, propagate it
            throw new PatientExecutionException("Unexpected throwable caught while waiting patiently.", thrown);
        }
    }
}
