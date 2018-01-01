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

import com.redfin.patience.Executable;
import com.redfin.patience.PatientExecutionException;
import com.redfin.patience.PatientExecutionHandler;
import com.redfin.patience.PatientExecutionResult;
import com.redfin.validity.ValidityUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.redfin.validity.Validity.validate;

/**
 * An implementation of the {@link PatientExecutionHandler}. It will
 * extract a value from the executable and test it with the predicate. Any Throwable
 * thrown from the executable or the predicate will be checked against a set of
 * ignored types. If the thrown Throwable was one of the ignored types, a non-successful
 * {@link PatientExecutionResult} will be returned. If the Throwable was not one of
 * the ignored types it will be set as the cause of a {@link PatientExecutionResult} which
 * will then be thrown.
 */
public final class IgnoringExecutionHandler
        implements PatientExecutionHandler {

    private final Set<Class<? extends Throwable>> ignoredThrowableTypes;
    private final Set<Class<? extends Throwable>> notIgnoredThrowableTypes;

    /**
     * Create a new {@link IgnoringExecutionHandler} with the collection of types
     * to be ignored. If the collection of ignored is null or empty then no thrown types will be
     * ignored.
     *
     * @param ignoredThrowableTypes    the collection of Throwable classes to be ignored.
     *                                 Note that Throwable is considered a match to an element of
     *                                 this collection even if it is a subclass of a type of the element.
     */
    public IgnoringExecutionHandler(Collection<Class<? extends Throwable>> ignoredThrowableTypes) {
        this(ignoredThrowableTypes, null);
    }

    /**
     * Create a new {@link IgnoringExecutionHandler} with the collection of types
     * to be ignored. If the collection of ignored is null or empty then no thrown types will be
     * ignored.
     *
     * @param ignoredThrowableTypes    the collection of Throwable classes to be ignored.
     *                                 Note that Throwable is considered a match to an element of
     *                                 this collection even if it is a subclass of a type of the element.
     * @param notIgnoredThrowableTypes the collection of Throwable classes explicitly not ignored
     *                                 even if they or a super class are in the ignoredThrowableTypes
     *                                 collection. Note that a Throwable is only considered to be a match
     *                                 to elements of this collection if it is an exact type matches, not
     *                                 a subclasse of an element of this collection.
     */
    public IgnoringExecutionHandler(Collection<Class<? extends Throwable>> ignoredThrowableTypes,
                                    Collection<Class<? extends Throwable>> notIgnoredThrowableTypes) {
        if (null == ignoredThrowableTypes) {
            this.ignoredThrowableTypes = new HashSet<>();
        } else {
            this.ignoredThrowableTypes = ignoredThrowableTypes.stream()
                                                              .filter(Objects::nonNull)
                                                              .collect(Collectors.toSet());
        }
        if (null == notIgnoredThrowableTypes) {
            this.notIgnoredThrowableTypes = new HashSet<>();
        } else {
            this.notIgnoredThrowableTypes = notIgnoredThrowableTypes.stream()
                                                                    .filter(Objects::nonNull)
                                                                    .collect(Collectors.toSet());
        }
    }

    @Override
    public <T> PatientExecutionResult<T> execute(Executable<T> executable,
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
            String errorMessage = "Unexpected throwable caught while waiting patiently.";
            // Check the type of the throwable
            if (notIgnoredThrowableTypes.stream().anyMatch(clazz -> clazz.equals(thrown.getClass()))) {
                // It was explicitly not ignored, throw an exception
                throw new PatientExecutionException(errorMessage, thrown);
            } else if (ignoredThrowableTypes.stream()
                                            .anyMatch(clazz -> clazz.isAssignableFrom(thrown.getClass()))) {
                // It was ignored and not explicitly NOT ignored, return a failure case
                return PatientExecutionResult.fail("Caught throwable: " + thrown.toString());
            } else {
                // It was neither ignored or not ignored, throw an exception
                throw new PatientExecutionException(errorMessage, thrown);
            }
        }
    }
}
