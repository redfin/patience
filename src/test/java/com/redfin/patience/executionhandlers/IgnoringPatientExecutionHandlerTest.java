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

package com.redfin.patience.executionhandlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class IgnoringPatientExecutionHandlerTest implements PatientExecutionHandlerContract<IgnoringPatientExecutionHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public IgnoringPatientExecutionHandler getInstance() {
        return new IgnoringPatientExecutionHandler(NumberFormatException.class);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testThrowsExceptionForNullExceptionType() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new IgnoringPatientExecutionHandler(null,
                                                                          IllegalArgumentException.class));
    }

    @Test
    void testDoesNotThrowForNullExceptionTypes() {
        new IgnoringPatientExecutionHandler(IllegalArgumentException.class, null);
    }

    @Test
    void testSwallowsIgnoredUncheckedException() {
        IgnoringPatientExecutionHandler executionHandler = new IgnoringPatientExecutionHandler(PatientExecutionHandlerContract.ContractUncheckedException.class);
        executionHandler.execute(() -> {
            throw new PatientExecutionHandlerContract.ContractUncheckedException();
        }, PASSING_FILTER);
    }

    @Test
    void testSwallowsIgnoredUncheckedExceptionFromArray() {
        IgnoringPatientExecutionHandler executionHandler = new IgnoringPatientExecutionHandler(NumberFormatException.class,
                                                                                               PatientExecutionHandlerContract.ContractUncheckedException.class);
        executionHandler.execute(() -> {
            throw new PatientExecutionHandlerContract.ContractUncheckedException();
        }, PASSING_FILTER);
    }

    @Test
    void testSwallowsIgnoredCheckedException() {
        IgnoringPatientExecutionHandler executionHandler = new IgnoringPatientExecutionHandler(PatientExecutionHandlerContract.ContractCheckedException.class);
        executionHandler.execute(() -> {
            throw new PatientExecutionHandlerContract.ContractCheckedException();
        }, PASSING_FILTER);
    }

    @Test
    void testSwallowsIgnoredCheckedExceptionFromArray() {
        IgnoringPatientExecutionHandler executionHandler = new IgnoringPatientExecutionHandler(NumberFormatException.class,
                                                                                               PatientExecutionHandlerContract.ContractCheckedException.class);
        executionHandler.execute(() -> {
            throw new PatientExecutionHandlerContract.ContractCheckedException();
        }, PASSING_FILTER);
    }
}
