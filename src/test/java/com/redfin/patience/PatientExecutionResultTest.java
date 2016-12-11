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
    void testCanCreateFailureResult() {
        Assertions.assertNotNull(PatientExecutionResult.failure(VALUE),
                                 "Should be able to create a failure result");
    }

    @Test
    void testFailureResultThrowsForNullDescription() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientExecutionResult.failure(null));
    }

    @Test
    void testCanCreateSuccessResultWithNull() {
        Assertions.assertNotNull(PatientExecutionResult.success(null),
                                 "Should be able to create a success result with null");
    }

    @Test
    void testCanCreateSuccessResultWithNonNull() {
        Assertions.assertNotNull(PatientExecutionResult.success(VALUE),
                                 "Should be able to create a success result");
    }

    @Test
    void testFailureResultGetSuccessResultThrowsException() {
        Assertions.assertThrows(NoSuchElementException.class,
                                () -> PatientExecutionResult.failure(VALUE).getSuccessResult());
    }

    @Test
    void testFailureResultWasSuccessfulReturnsFalse() {
        Assertions.assertFalse(PatientExecutionResult.failure(VALUE).wasSuccessful(),
                               "A failure PatientExecutionResult should return false for wasSuccessful");
    }

    @Test
    void testFailureResultGetFailureDescriptionReturnsGivenDescription() {
        Assertions.assertEquals(VALUE,
                                PatientExecutionResult.failure(VALUE).getFailureDescription(),
                                "A failure PatientExecutionResult should return the given failure description");
    }

    @Test
    void testSuccessResultGetReturnsGivenValue() {
        Assertions.assertEquals(VALUE,
                                PatientExecutionResult.success(VALUE).getSuccessResult(),
                                "A PatientExecutionResult with a value should return the given value");
    }

    @Test
    void testSuccessResultGetReturnsGivenValueForNull() {
        Assertions.assertNull(PatientExecutionResult.success(null).getSuccessResult(),
                              "A PatientExecutionResult with a value of null should return null for get");
    }

    @Test
    void testSuccessResultWasSuccessfulReturnsTrue() {
        Assertions.assertTrue(PatientExecutionResult.success(VALUE).wasSuccessful(),
                              "A non-empty PatientExecutionResult should return true for wasSuccessful");
    }

    @Test
    void testSuccessResultGetFailureDescriptionThrows() {
        Assertions.assertThrows(NoSuchElementException.class,
                                () -> PatientExecutionResult.success(VALUE).getFailureDescription());
    }
}
