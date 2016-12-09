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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

final class FilteredPatientFutureTest implements PatientFutureContract<FilteredPatientFuture<String>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirement, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final PatientWait WAIT = PatientWait.builder().build();
    private static final Callable<String> CALLABLE = () -> "hello";
    private static final Predicate<String> FILTER = t -> !t.isEmpty();

    @Override
    public FilteredPatientFuture<String> getInstanceWithDefaultTimeout(Duration timeout) {
        return new FilteredPatientFuture<>(PatientWait.builder().withDefaultTimeout(timeout).build(),
                                           CALLABLE, FILTER);
    }

    @Override
    public FilteredPatientFuture<String> getSuccessfulInstance() {
        return new FilteredPatientFuture<>(WAIT, CALLABLE, FILTER);
    }

    @Override
    public FilteredPatientFuture<String> getUnsuccessfulInstance() {
        return new FilteredPatientFuture<>(WAIT, CALLABLE, String::isEmpty);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testThrowsForNullWait() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new FilteredPatientFuture<>(null, CALLABLE, FILTER));
    }

    @Test
    void testThrowsForNullCallable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new FilteredPatientFuture<>(WAIT, null, FILTER));
    }

    @Test
    void testThrowsForNullFilter() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new FilteredPatientFuture<>(WAIT, CALLABLE, null));
    }
}
