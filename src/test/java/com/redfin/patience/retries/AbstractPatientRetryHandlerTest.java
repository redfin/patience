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

package com.redfin.patience.retries;

import com.redfin.patience.PatientException;
import com.redfin.patience.PatientExecutionResult;
import com.redfin.patience.PatientRetryHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

final class AbstractPatientRetryHandlerTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testThrowsForNullDelaySupplier() {
        PatientRetryHandler handler = new AbstractPatientRetryHandler() {
            @Override
            protected Supplier<Duration> getRetryHandlerDurationSupplier() {
                return null;
            }
        };
        Assertions.assertThrows(PatientException.class,
                                () -> handler.execute(() -> PatientExecutionResult.fail("whoops"),
                                                      Duration.ofSeconds(1)),
                                "Should thrown an exception for a null duration supplier");
    }

    @Test
    void testThrowsForNullDurationFromSupplier() {
        PatientRetryHandler handler = new AbstractPatientRetryHandler() {
            @Override
            protected Supplier<Duration> getRetryHandlerDurationSupplier() {
                return () -> null;
            }
        };
        Assertions.assertThrows(PatientException.class,
                                () -> handler.execute(() -> PatientExecutionResult.fail("whoops"),
                                                      Duration.ofSeconds(1)),
                                "Should thrown an exception for a null duration from supplier");
    }

    @Test
    void testThrowsForNegativeDurationFromSupplier() {
        PatientRetryHandler handler = new AbstractPatientRetryHandler() {
            @Override
            protected Supplier<Duration> getRetryHandlerDurationSupplier() {
                return () -> Duration.ofMillis(-100);
            }
        };
        Assertions.assertThrows(PatientException.class,
                                () -> handler.execute(() -> PatientExecutionResult.fail("whoops"),
                                                      Duration.ofSeconds(1)),
                                "Should thrown an exception for a negative duration from supplier");
    }
}
