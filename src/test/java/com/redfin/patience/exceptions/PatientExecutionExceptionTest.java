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

@DisplayName("When a PatientExecutionException")
final class PatientExecutionExceptionTest
    extends AbstractExceptionTest<PatientExecutionException> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected PatientExecutionException getInstance() {
        return new PatientExecutionException();
    }

    @Override
    protected PatientExecutionException getInstance(String message) {
        return new PatientExecutionException(message);
    }

    @Override
    protected PatientExecutionException getInstance(Throwable cause) {
        return new PatientExecutionException(cause);
    }

    @Override
    protected PatientExecutionException getInstance(String message,
                                                    Throwable cause) {
        return new PatientExecutionException(message, cause);
    }

    @Override
    protected Class<PatientExecutionException> getClassUnderTest() {
        return PatientExecutionException.class;
    }
}
