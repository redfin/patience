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

import com.redfin.patience.executions.IgnoringExecutionHandler;
import com.redfin.patience.executions.SimpleExecutionHandler;

import java.util.Arrays;
import java.util.Collection;

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
    // Static Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return a new {@link SimpleExecutionHandler} instance.
     */
    public static PatientExecutionHandler simple() {
        return new SimpleExecutionHandler();
    }

    /**
     * @param ignoredThrowableTypes the collection of class throwable classes that should be ignored.
     *                              A null or empty array will return an execution handler that
     *                              doesn't ignore any throwable that is thrown.
     *
     * @return a new {@link IgnoringExecutionHandler} instance that ignores the given types.
     */
    @SafeVarargs
    public static PatientExecutionHandler ignoring(Class<? extends Throwable>... ignoredThrowableTypes) {
        return ignoring(null == ignoredThrowableTypes ? null : Arrays.asList(ignoredThrowableTypes));
    }

    /**
     * @param ignoredThrowableTypes the collection of class throwable classes that should be ignored.
     *                              A null or empty collection will return an execution handler that
     *                              doesn't ignore any throwable that is thrown.
     *
     * @return a new {@link IgnoringExecutionHandler} instance that ignores the given types.
     */
    public static PatientExecutionHandler ignoring(Collection<Class<? extends Throwable>> ignoredThrowableTypes) {
        return new IgnoringExecutionHandler(ignoredThrowableTypes);
    }

    /**
     * @return a new {@link IgnoringExecutionHandler} instance that ignores all
     * Throwable types.
     */
    public static PatientExecutionHandler ignoringAll() {
        return ignoring(Throwable.class);
    }
}
