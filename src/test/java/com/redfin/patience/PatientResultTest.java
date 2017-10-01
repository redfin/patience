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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class PatientResultTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private List<String> getNonEmptyList() {
        return Arrays.asList("hello", "world");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testFailingResultThrowsForNullDescriptionList() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientResult.fail(null),
                                "Should throw an exception for a null failure description list");
    }

    @Test
    void testFailingResultThrowsForEmptyDescriptionList() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientResult.fail(Collections.emptyList()),
                                "Should throw an exception for an empty failure description list");
    }

    @Test
    void testFailingResultReturnsNonNullForNonEmptyDescriptionList() {
        Assertions.assertNotNull(PatientResult.fail(getNonEmptyList()),
                                 "Should get a non-null execution result for a valid description list");
    }

    @Test
    void testFailingResultReturnsFalseForIsSuccess() {
        Assertions.assertFalse(PatientResult.fail(getNonEmptyList())
                                                     .isSuccess(),
                               "A failing result should return false for isSuccess()");
    }

    @Test
    void testPassingResultReturnsTrueForIsSuccess_TrueResult() {
        Assertions.assertTrue(PatientResult.pass(true)
                                           .isSuccess(),
                              "A passing result should return true for isSuccess()");
    }

    @Test
    void testPassingResultReturnsTrueForIsSuccess_FalseResult() {
        Assertions.assertTrue(PatientResult.pass(false)
                                           .isSuccess(),
                              "A passing result should return true for isSuccess()");
    }

    @Test
    void testPassingResultReturnsTrueForIsSuccess_NonBooleanResult() {
        Assertions.assertTrue(PatientResult.pass("hello")
                                           .isSuccess(),
                              "A passing result should return true for isSuccess()");
    }

    @Test
    void testPassingResultReturnsGivenResult() {
        String value = "hello";
        Assertions.assertEquals(value,
                                PatientResult.pass(value)
                                             .getResult(),
                                "A passing result should return the given result");
    }

    @Test
    void testPassingResultThrowsForGetFailureDescription() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> PatientResult.pass("hello")
                                                   .getFailedAttemptDescriptions(),
                                "A passing result should throw for getFailedAttemptDescriptions()");
    }

    @Test
    void testFailingResultThrowsForGivenResult() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> PatientResult.fail(getNonEmptyList())
                                                   .getResult(),
                                "A failing result should throw for getResult()");
    }

    @Test
    void testFailingResultReturnsGivenFailureDescription() {
        List<String> list = getNonEmptyList();
        Assertions.assertEquals(list,
                                PatientResult.fail(list)
                                             .getFailedAttemptDescriptions(),
                                "A failing result should return given failure description list");
    }
}
