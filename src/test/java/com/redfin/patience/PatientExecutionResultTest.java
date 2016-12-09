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

import java.util.NoSuchElementException;

final class PatientExecutionResultTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final String VALUE = "hello";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanCreateEmptyResult() {
        Assertions.assertNotNull(PatientExecutionResult.empty(),
                                 "Should be able to create an empty result");
    }

    @Test
    void testCanCreateNonEmptyResultWithNull() {
        Assertions.assertNotNull(PatientExecutionResult.of(null),
                                 "Should be able to create a non-empty result with null");
    }

    @Test
    void testCanCreateNonEmptyResultWithNonNull() {
        Assertions.assertNotNull(PatientExecutionResult.of(VALUE),
                                 "Should be able to create a non-empty result");
    }

    @Test
    void testEmptyResultGetThrowsException() {
        Assertions.assertThrows(NoSuchElementException.class,
                                () -> PatientExecutionResult.empty().get());
    }

    @Test
    void testEmptyResultIsPresentReturnsFalse() {
        Assertions.assertFalse(PatientExecutionResult.empty().isPresent(),
                               "An empty PatientExecutionResult should return false for isPresent");
    }

    @Test
    void testEmptyResultIfPresentIgnoresConsumer() {
        PatientExecutionResult.<String>empty().ifPresent(s -> {throw new RuntimeException(s);});
    }

    @Test
    void testEmptyResultIfPresentThrowsForNullConsumer() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientExecutionResult.empty().ifPresent(null));
    }

    @Test
    void testNonEmptyResultGetReturnsGivenValue() {
        Assertions.assertEquals(VALUE,
                                PatientExecutionResult.of(VALUE).get(),
                                "A PatientExecutionResult with a value should return the given value");
    }

    @Test
    void testNonEmptyResultGetReturnsGivenValueForNull() {
        Assertions.assertNull(PatientExecutionResult.of(null).get(),
                              "A PatientExecutionResult with a value of null should return null for get");
    }

    @Test
    void testNonEmptyResultIsPresentReturnsTrue() {
        Assertions.assertTrue(PatientExecutionResult.of(VALUE).isPresent(),
                              "A non-empty PatientExecutionResult should return true for isPresent");
    }

    @Test
    void testNonEmptyResultIfPresentThrowsForNullConsumer() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientExecutionResult.of(VALUE).ifPresent(null));
    }

    @Test
    void testNonEmptyResultIfPresentConsumesGivenValue() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientExecutionResult.of(VALUE)
                                                            .ifPresent(s -> { if (s.equals(VALUE)) throw new IllegalArgumentException(); }));
    }
}
