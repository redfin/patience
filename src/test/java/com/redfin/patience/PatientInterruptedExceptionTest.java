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

final class PatientInterruptedExceptionTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiateWithNoArguments() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException();
                                },
                                "Should be able to instantiate a PatientInterruptedException with the zero argument constructor");
    }

    @Test
    void testCanInstantiateWithMessage() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException("hello");
                                },
                                "Should be able to instantiate a PatientInterruptedException with a non-null message");
    }

    @Test
    void testCanInstantiateWithNullMessage() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException((String) null);
                                },
                                "Should be able to instantiate a PatientInterruptedException with a null message");
    }

    @Test
    void testCanInstantiateWithCause() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException(new RuntimeException("hello"));
                                },
                                "Should be able to instantiate a PatientInterruptedException with a non-null cause");
    }

    @Test
    void testCanInstantiateWithNullCause() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException((Throwable) null);
                                },
                                "Should be able to instantiate a PatientInterruptedException with a null cause");
    }

    @Test
    void testCanInstantiateWithMessageAndCause() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException("hello", new RuntimeException());
                                },
                                "Should be able to instantiate a PatientInterruptedException with a message and cause");
    }

    @Test
    void testCanInstantiateWithMessageAndNullCause() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException("hello", null);
                                },
                                "Should be able to instantiate a PatientInterruptedException with a message and null cause");
    }

    @Test
    void testCanInstantiateWithCauseAndNullMessage() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException(null, new RuntimeException());
                                },
                                "Should be able to instantiate a PatientInterruptedException with a cause and null message");
    }

    @Test
    void testCanInstantiateWithNullMessageAndNullCause() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException(null, null);
                                },
                                "Should be able to instantiate a PatientInterruptedException with a null message and null cause");
    }

    @Test
    void testCanBeInstantiatedWithFlags() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException("hello", new RuntimeException(), true, true);
                                },
                                "Should be able to instantiate a PatientInterruptedException with flags");
    }

    @Test
    void testCanBeInstantiatedWithFlagsAndNullMessage() {
        Assertions.assertThrows(PatientInterruptedException.class,
                                () -> {
                                    throw new PatientInterruptedException(null, new RuntimeException(), false, false);
                                },
                                "Should be able to instantiate a PatientInterruptedException with flags and a null message");
    }}
