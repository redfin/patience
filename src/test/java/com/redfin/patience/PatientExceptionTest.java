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

final class PatientExceptionTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiateWithNoArguments() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException();
                                },
                                "Should be able to instantiate a PatientException with the zero argument constructor");
    }

    @Test
    void testCanInstantiateWithMessage() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException("hello");
                                },
                                "Should be able to instantiate a PatientException with a non-null message");
    }

    @Test
    void testCanInstantiateWithNullMessage() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException((String) null);
                                },
                                "Should be able to instantiate a PatientException with a null message");
    }

    @Test
    void testCanInstantiateWithCause() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException(new RuntimeException("hello"));
                                },
                                "Should be able to instantiate a PatientException with a non-null cause");
    }

    @Test
    void testCanInstantiateWithNullCause() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException((Throwable) null);
                                },
                                "Should be able to instantiate a PatientException with a null cause");
    }

    @Test
    void testCanInstantiateWithMessageAndCause() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException("hello", new RuntimeException());
                                },
                                "Should be able to instantiate a PatientException with a message and cause");
    }

    @Test
    void testCanInstantiateWithMessageAndNullCause() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException("hello", null);
                                },
                                "Should be able to instantiate a PatientException with a message and null cause");
    }

    @Test
    void testCanInstantiateWithCauseAndNullMessage() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException(null, new RuntimeException());
                                },
                                "Should be able to instantiate a PatientException with a cause and null message");
    }

    @Test
    void testCanInstantiateWithNullMessageAndNullCause() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException(null, null);
                                },
                                "Should be able to instantiate a PatientException with a null message and null cause");
    }

    @Test
    void testCanBeInstantiatedWithFlags() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException("hello", new RuntimeException(), true, true);
                                },
                                "Should be able to instantiate a PatientException with flags");
    }

    @Test
    void testCanBeInstantiatedWithFlagsAndNullMessage() {
        Assertions.assertThrows(PatientException.class,
                                () -> {
                                    throw new PatientException(null, new RuntimeException(), false, false);
                                },
                                "Should be able to instantiate a PatientException with flags and a null message");
    }
}
