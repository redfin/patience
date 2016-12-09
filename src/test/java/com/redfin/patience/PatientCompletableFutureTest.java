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

import java.util.function.Predicate;

final class PatientCompletableFutureTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final PatientWait WAIT = PatientWait.builder().build();
    private static final Predicate<String> FILTER = t -> null != t && !t.isEmpty();

    private PatientCompletableFuture<String> newInstance(PatientWait wait, Predicate<String> filter) {
        return new PatientCompletableFuture<>(wait, filter);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiate() {
        Assertions.assertNotNull(newInstance(WAIT, FILTER),
                                 "A PatientCompletableFuture should be able to be instantiated");
    }

    @Test
    void testThrowsForNullPatientWait() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(null, FILTER));
    }

    @Test
    void testThrowsForNullFilter() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(WAIT, null));
    }

    @Test
    void testFromThrowsExceptionForNullCallable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> newInstance(WAIT, FILTER).from(null));
    }

    @Test
    void testFromReturnsNonNullForNonNullCallable() {
        Assertions.assertNotNull(newInstance(WAIT, FILTER).from(() -> "hello"),
                                 "PatientCompletableFuture should return a non-null PatientFuture with a non-null callable");
    }
}
