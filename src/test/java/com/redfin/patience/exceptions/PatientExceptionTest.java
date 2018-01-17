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

@DisplayName("When a PatientException")
final class PatientExceptionTest
    extends AbstractExceptionTest<PatientException> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected PatientException getInstance() {
        return new PatientException();
    }

    @Override
    protected PatientException getInstance(String message) {
        return new PatientException(message);
    }

    @Override
    protected PatientException getInstance(Throwable cause) {
        return new PatientException(cause);
    }

    @Override
    protected PatientException getInstance(String message,
                                           Throwable cause) {
        return new PatientException(message, cause);
    }

    @Override
    protected Class<PatientException> getClassUnderTest() {
        return PatientException.class;
    }
}
