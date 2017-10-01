/*
 * Copyright: (c) 2016 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed txo in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.patience;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

final class PatientTimeoutExceptionTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void testException(String message,
                               List<String> descriptions,
                               Consumer<PatientTimeoutException> consumer) {
        PatientTimeoutException exception = Assertions.assertThrows(PatientTimeoutException.class,
                                                                    () -> { throw new PatientTimeoutException(message, descriptions); },
                                                                    "Should be able to instantiate a patient timeout exception.");
        consumer.accept(exception);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiateTimeoutException() {
        testException("hello",
                      Collections.singletonList("description"),
                      exception -> {}); // instantiation check already done by default
    }

    @Test
    void testCanInstantiateTimeoutExceptionWithNullMessage() {
        testException(null,
                      Collections.singletonList("description"),
                      exception -> {}); // instantiation check already done by default
    }

    @Test
    void testThrowsForNullDescriptionList() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientTimeoutException("hello", null),
                                "Should throw an exception if a null description list is given.");
    }

    @Test
    void testThrowsForEmptyDescriptionList() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientTimeoutException("hello", new ArrayList<>()),
                                "Should throw an exception if an empty description list is given.");
    }

    @Test
    void testReturnsGivenAttemptCount() {
        testException("message",
                      Arrays.asList("description 1", "description 2"),
                      exception -> Assertions.assertEquals(2,
                                                           exception.getAttemptsCount(),
                                                           "Should return the number of given descriptions."));
    }

    @Test
    void testReturnsGivenDescriptions() {
        List<String> descriptions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            descriptions.add("count: " + i);
        }
        testException("message",
                      descriptions,
                      exception -> Assertions.assertEquals(descriptions,
                                                           exception.getAttemptDescriptions(),
                                                           "Should return the given attempt descriptions list."));
    }
}
