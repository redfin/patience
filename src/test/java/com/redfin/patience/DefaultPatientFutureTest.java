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

final class DefaultPatientFutureTest implements PatientFutureContract<DefaultPatientFuture<String>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirement, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public DefaultPatientFuture<String> getInstanceWithDefaultTimeout(Duration timeout) {
        return new DefaultPatientFuture<>(PatientWait.builder().withDefaultTimeout(timeout).build(),
                                          CALLABLE);
    }

    @Override
    public DefaultPatientFuture<String> getSuccessfulInstance() {
        return new DefaultPatientFuture<>(WAIT, CALLABLE);
    }

    @Override
    public DefaultPatientFuture<String> getUnsuccessfulInstance() {
        return new DefaultPatientFuture<>(WAIT, () -> null);
    }

    private static final PatientWait WAIT = PatientWait.builder().build();
    private static final Callable<String> CALLABLE = () -> "hello";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testThrowsForNullWait() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new DefaultPatientFuture<>(null, CALLABLE));
    }

    @Test
    void testThrowsForNullCallable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new DefaultPatientFuture<>(WAIT, null));
    }

    @Test
    void testDefaultFilterReturnsFalseForNullValues() {
        Assertions.assertThrows(PatientTimeoutException.class,
                                () -> getUnsuccessfulInstance().get());
    }

    @Test
    void testDefaultFilterReturnsFalseForFalseValues() {
        Assertions.assertThrows(PatientTimeoutException.class,
                                () -> new DefaultPatientFuture<>(WAIT, () -> false).get());
    }

    @Test
    void testDefaultFilterReturnsTrueForNonNullValues() {
        Assertions.assertNotNull(getSuccessfulInstance().get(),
                                 "A DefaultPatientFuture should return a result for non null values");
    }

    @Test
    void testDefaultFilterReturnsTrueForTrueValues() {
        Assertions.assertNotNull(new DefaultPatientFuture<>(WAIT, () -> true).get(),
                                 "A DefaultPatientFuture should return a result for non null values");
    }

    @Test
    void testDefaultFilterWithFilterThrowsForNullPredicate() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getSuccessfulInstance().withFilter(null));
    }

    @Test
    void testDefaultFilterWithFilterSucceedsForNonNullPredicate() {
        Assertions.assertNotNull(getSuccessfulInstance().withFilter(String::isEmpty),
                                 "A DefaultPatientFuture should return a FilteredPatientFuture for a non null predicate filter");
    }
}
