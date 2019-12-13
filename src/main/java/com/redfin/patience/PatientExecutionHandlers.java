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

import com.redfin.patience.executions.IgnoringPatientExecutionHandler;
import com.redfin.patience.executions.SimplePatientExecutionHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A static, non-instantiable, class for obtaining instances of different
 * implementations of the {@link PatientExecutionHandler} interface.
 */
public final class PatientExecutionHandlers {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * Make sure that the static class cannot be instantiated
     */

    private PatientExecutionHandlers() {
        throw new AssertionError("Cannot instantiate PatientExecutionHandlers.");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Static Methods and Fields
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final Set<Class<? extends Throwable>> NOT_IGNORED_CLASSES = new HashSet<>();
    static {
        NOT_IGNORED_CLASSES.add(OutOfMemoryError.class);
    }

    /**
     * @return a new {@link SimplePatientExecutionHandler} instance.
     */
    public static PatientExecutionHandler simple() {
        return new SimplePatientExecutionHandler();
    }

    /**
     * @param ignoredThrowableTypes the collection of class throwable classes that should be ignored.
     *                              A null or empty array will return an execution handler that
     *                              doesn't ignore any throwable that is thrown. Note that {@link OutOfMemoryError}s are
     *                              explicitly NOT ignored even if added to the argument array. If you want to ignore
     *                              them, then you need to create an {@link IgnoringPatientExecutionHandler} directly.
     *
     * @return a new {@link IgnoringPatientExecutionHandler} instance that ignores the given types.
     */
    @SafeVarargs
    public static PatientExecutionHandler ignoring(Class<? extends Throwable>... ignoredThrowableTypes) {
        return ignoring(null == ignoredThrowableTypes ? null : Arrays.asList(ignoredThrowableTypes));
    }

    /**
     * @param ignoredThrowableTypes the collection of class throwable classes that should be ignored.
     *                              A null or empty collection will return an execution handler that
     *                              doesn't ignore any throwable that is thrown. Note that {@link OutOfMemoryError}s are
     *                              explicitly NOT ignored even if added to the argument array. If you want to ignore
     *                              them, then you need to create an {@link IgnoringPatientExecutionHandler} directly.
     *
     * @return a new {@link IgnoringPatientExecutionHandler} instance that ignores the given types.
     */
    public static PatientExecutionHandler ignoring(Collection<Class<? extends Throwable>> ignoredThrowableTypes) {
        return new IgnoringPatientExecutionHandler(ignoredThrowableTypes, NOT_IGNORED_CLASSES);
    }

    /**
     * @return a new {@link IgnoringPatientExecutionHandler} instance that ignores all
     * Exception types, though not Errors.
     */
    public static PatientExecutionHandler ignoringAll() {
        return ignoring(Exception.class);
    }
}
