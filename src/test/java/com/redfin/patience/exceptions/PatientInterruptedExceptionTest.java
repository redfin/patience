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

package com.redfin.patience.exceptions;

import org.junit.jupiter.api.DisplayName;

@DisplayName("When a PatientInterruptedException")
final class PatientInterruptedExceptionTest
    extends AbstractExceptionTest<PatientInterruptedException> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected PatientInterruptedException getInstance() {
        return new PatientInterruptedException();
    }

    @Override
    protected PatientInterruptedException getInstance(String message) {
        return new PatientInterruptedException(message);
    }

    @Override
    protected PatientInterruptedException getInstance(Throwable cause) {
        return new PatientInterruptedException(cause);
    }

    @Override
    protected PatientInterruptedException getInstance(String message, Throwable cause) {
        return new PatientInterruptedException(message, cause);
    }

    @Override
    protected Class<PatientInterruptedException> getClassUnderTest() {
        return PatientInterruptedException.class;
    }
}
