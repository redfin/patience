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

final class PatientExecutionHandlersTest implements NonInstantiableContract<PatientExecutionHandlers> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements, constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Class<PatientExecutionHandlers> getClassObject_NonInstantiableContract() {
        return PatientExecutionHandlers.class;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testSimpleHandlerReturnsInstance() {
        Assertions.assertNotNull(PatientExecutionHandlers.simpleHandler(),
                                 "simpleHandler shouldn't return a null instance");
    }

    @Test
    void testIgnoringHandlerReturnsInstance() {
        Assertions.assertNotNull(PatientExecutionHandlers.ignoring(NullPointerException.class,
                                                                   IllegalArgumentException.class),
                                 "ignoring shouldn't return a null instance");
    }

    @Test
    void testIgnoringThrowsExceptionForNullExceptionType() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> PatientExecutionHandlers.ignoring(null,
                                                                        IllegalArgumentException.class));
    }

    @Test
    void testIgnoringDoesNotThrowForNullExceptionTypesArray() {
        PatientExecutionHandlers.ignoring(IllegalArgumentException.class,
                                          null);
    }
}
