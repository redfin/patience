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

final class PatientTimeoutExceptionTest {

    @Test
    void testCanInstantiateWithNoArguments() {
        Assertions.assertNotNull(new PatientTimeoutException(),
                                 "Should be able to instantiate a PatientTimeoutException with the zero argument constructor");
    }

    @Test
    void testCanInstantiateWithMessage() {
        Assertions.assertNotNull(new PatientTimeoutException("hello"),
                                 "Should be able to instantiate a PatientTimeoutException with a non-null message");
    }

    @Test
    void testCanInstantiateWithNullMessage() {
        Assertions.assertNotNull(new PatientTimeoutException((String) null),
                                 "Should be able to instantiate a PatientTimeoutException with a null message");
    }

    @Test
    void testCanInstantiateWithCause() {
        Assertions.assertNotNull(new PatientTimeoutException(new RuntimeException("hello")),
                                 "Should be able to instantiate a PatientTimeoutException with a non-null cause");
    }

    @Test
    void testCanInstantiateWithNullCause() {
        Assertions.assertNotNull(new PatientTimeoutException((Throwable) null),
                                 "Should be able to instantiate a PatientTimeoutException with a null cause");
    }

    @Test
    void testCanInstantiateWithMessageAndCause() {
        Assertions.assertNotNull(new PatientTimeoutException("hello", new RuntimeException()),
                                 "Should be able to instantiate a PatientTimeoutException with a message and cause");
    }

    @Test
    void testCanInstantiateWithMessageAndNullCause() {
        Assertions.assertNotNull(new PatientTimeoutException("hello", null),
                                 "Should be able to instantiate a PatientTimeoutException with a message and null cause");
    }

    @Test
    void testCanInstantiateWithCauseAndNullMessage() {
        Assertions.assertNotNull(new PatientTimeoutException(null, new RuntimeException()),
                                 "Should be able to instantiate a PatientTimeoutException with a cause and null message");
    }

    @Test
    void testCanInstantiateWithNullMessageAndNullCause() {
        Assertions.assertNotNull(new PatientTimeoutException(null, null),
                                 "Should be able to instantiate a PatientTimeoutException with a null message and null cause");
    }

    @Test
    void testCanBeInstantiatedWithFlags() {
        Assertions.assertNotNull(new PatientTimeoutException("hello", new RuntimeException(), true, true),
                                 "Should be able to instantiate a PatientTimeoutException with flags");
    }
}
