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

import java.util.ArrayList;
import java.util.List;

final class PatientTimeoutExceptionTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanIntantiateWithMessage() {
        Assertions.assertNotNull(new PatientTimeoutException("hello", new ArrayList<>()),
                                 "Should be able to instantiate a PatientTimeoutException with a message");
    }

    @Test
    void testCanInstantiateWithNullMessage() {
        Assertions.assertNotNull(new PatientTimeoutException(null, new ArrayList<>()),
                                 "Should be able to instantiate a PatientTimeoutException with a null message");
    }

    @Test
    void testThrowsWithNullList() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientTimeoutException("hello", null));
    }

    @Test
    void testReturnsGivenMessage() {
        String message = "hello";
        Assertions.assertEquals(message,
                                new PatientTimeoutException(message, new ArrayList<>()).getMessage(),
                                "A PatientTimeoutException should return the given message");
    }

    @Test
    void testReturnsAttemptCount() {
        List<String> list = new ArrayList<>();
        list.add("hello");
        list.add("whoa");
        Assertions.assertEquals(2,
                                new PatientTimeoutException("hello", list).getAttemptsCount(),
                                "A PatientTimeoutException should return the given attempt descriptions list");
    }

    @Test
    void testReturnsGivenList() {
        List<String> list = new ArrayList<>();
        list.add("hello");
        list.add("whoa");
        Assertions.assertEquals(list,
                                new PatientTimeoutException("hello", list).getAttemptDescriptions(),
                                "A PatientTimeoutException should return the given attempt descriptions list");
    }

    @Test
    void testReturnsUnmodifiableList() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> new PatientTimeoutException("hello", new ArrayList<>()).getAttemptDescriptions().add("hi"));
    }
}
