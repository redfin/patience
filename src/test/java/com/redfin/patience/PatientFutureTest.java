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

final class PatientFutureTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final PatientWait WAIT = PatientWait.builder().build();
    private static final Callable<String> CALLABLE = () -> "hello";
    private static final Predicate<String> FILTER = t -> !t.isEmpty();

    private PatientFuture<String> newInstance(PatientWait wait, Callable<String> callable, Predicate<String> filter) {
        return new PatientFuture<>(wait, callable, filter);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiate() {
        Assertions.assertNotNull(newInstance(WAIT, CALLABLE, FILTER),
                                 "Should be able to instantiate an instance");
    }

    @Test
    void testThrowsForNullWait() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(null, CALLABLE, FILTER));
    }

    @Test
    void testThrowsForNullCallable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(WAIT, null, FILTER));
    }

    @Test
    void testThrowsForNullFilter() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(WAIT, CALLABLE, null));
    }

    @Test
    void testGetThrowsForNullDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(WAIT, CALLABLE, FILTER).get(null));
    }

    @Test
    void testGetThrowsForNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(WAIT, CALLABLE, FILTER).get(Duration.ofSeconds(-1)));
    }

    @Test
    void testGetReturnsValue() {
        Assertions.assertEquals("hello",
                                newInstance(WAIT, CALLABLE, FILTER).get(),
                                "A PatientFuture should return the expected value for a valid result");
    }
}
