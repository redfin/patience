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

package com.redfin.patience.executionhandlers;

import com.redfin.patience.PatientExecutionResult;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * A simple, concrete sub class of the {@link AbstractPatientExecutionHandler}.
 * It delegates the actual execution to the super class and propagates any
 * unchecked exceptions thrown during the execution of the given callable or
 * predicate to a {@link RuntimeException}  (either via a cast if
 * it is already a runtime exception or set as the cause of a new instance if not).
 */
public class SimplePatientExecutionHandler extends AbstractPatientExecutionHandler {

    @Override
    public <T> PatientExecutionResult<T> execute(Callable<T> callable, Predicate<T> filter) {
        validate().that(callable).isNotNull();
        validate().that(filter).isNotNull();
        try {
            return executeHelper(callable, filter);
        } catch (Exception exception) {
            // An execution occurred during the code execution or testing of a result,
            // propagate the error
            throw propagate(exception);
        }
    }
}
