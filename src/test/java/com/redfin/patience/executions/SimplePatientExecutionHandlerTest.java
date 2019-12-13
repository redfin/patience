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

package com.redfin.patience.executions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("When a SimplePatientExecutionHandler")
final class SimplePatientExecutionHandlerTest
    extends AbstractExecutionHandlerTest<SimplePatientExecutionHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public SimplePatientExecutionHandler getInstance() {
        return new SimplePatientExecutionHandler();
    }

    @Nested
    @DisplayName("is constructed")
    final class ConstructorTests {

        @Test
        @DisplayName("it returns successfully")
        void testCanInstantiate() {
            try {
                Assertions.assertNotNull(getInstance(),
                                         "Should be able to create a non-null instance.");
            } catch (Throwable thrown) {
                Assertions.fail("Should be able to instantiate the object but caught the exception: " + thrown);
            }
        }
    }
}
