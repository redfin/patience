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

final class PatientExecutionResultTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testFailingResultThrowsForNullDescription() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientExecutionResult.fail(null),
                                "Should throw an exception for a null failure description");
    }

    @Test
    void testFailingResultThrowsForEmptyDescription() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientExecutionResult.fail(""),
                                "Should throw an exception for an empty failure description");
    }

    @Test
    void testFailingResultReturnsNonNullForValidDescription() {
        Assertions.assertNotNull(PatientExecutionResult.fail("failure"),
                                 "Should get a non-null execution result for a valid description");
    }

    @Test
    void testFailingResultReturnsFalseForIsSuccess() {
        Assertions.assertFalse(PatientExecutionResult.fail("failure")
                                                     .isSuccess(),
                               "A failing execution result should return false for isSuccess()");
    }

    @Test
    void testPassingResultReturnsTrueForIsSuccess_TrueResult() {
        Assertions.assertTrue(PatientExecutionResult.pass(true)
                                                    .isSuccess(),
                              "A passing execution result should return true for isSuccess()");
    }

    @Test
    void testPassingResultReturnsTrueForIsSuccess_FalseResult() {
        Assertions.assertTrue(PatientExecutionResult.pass(false)
                                                    .isSuccess(),
                              "A passing execution result should return true for isSuccess()");
    }

    @Test
    void testPassingResultReturnsTrueForIsSuccess_NonBooleanResult() {
        Assertions.assertTrue(PatientExecutionResult.pass("hello")
                                                    .isSuccess(),
                              "A passing execution result should return true for isSuccess()");
    }

    @Test
    void testPassingResultReturnsGivenResult() {
        String value = "hello";
        Assertions.assertEquals(value,
                                PatientExecutionResult.pass(value)
                                                      .getResult(),
                                "A passing execution result should return the given result");
    }

    @Test
    void testPassingResultThrowsForGetFailureDescription() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> PatientExecutionResult.pass("hello")
                                                            .getFailedAttemptDescription(),
                                "A passing execution result should throw for getFailedAttemptDescription()");
    }

    @Test
    void testFailingResultThrowsForGivenResult() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> PatientExecutionResult.fail("hello")
                                                            .getResult(),
                                "A failing execution result should throw for getResult()");
    }

    @Test
    void testFailingResultReturnsGivenFailureDescription() {
        String message = "hello";
        Assertions.assertEquals(message,
                                PatientExecutionResult.fail(message)
                                                      .getFailedAttemptDescription(),
                                "A failing execution result should return given failure description");
    }
}
