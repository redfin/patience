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

import java.util.Collection;
import java.util.Collections;

final class PatientExecutionHandlersTest
 implements NonInstantiableContract<PatientExecutionHandlers> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Class<PatientExecutionHandlers> getClassObject_NonInstantiableContract() {
        return PatientExecutionHandlers.class;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testReturnsNonNullForSimple() {
        Assertions.assertNotNull(PatientExecutionHandlers.simple(),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringWithNoArguments() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring(),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringWithOneArgument() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring(RuntimeException.class),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringWithTwoArguments() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring(RuntimeException.class, AssertionError.class),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringWithNullArray() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring((Class<? extends Throwable>[]) null),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringWithNullCollection() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring((Collection<Class<? extends Throwable>>) null),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringWithEmptyCollection() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring(Collections.emptyList()),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringWithNonEmptyCollection() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring(Collections.singletonList(RuntimeException.class)),
                                 "Should be able to receive a non-null execution handler.");
    }

    @Test
    void testReturnsNonNullForIgnoringAll() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoringAll(),
                                 "Should be able to receive a non-null execution handler.");
    }
}
