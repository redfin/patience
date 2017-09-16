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

import com.redfin.patience.executionhandlers.SimplePatientExecutionHandler;
import com.redfin.patience.retrystrategies.FixedDelayPatientRetryStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class PatientWaitTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants & helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final Duration INITIAL_DELAY = Duration.ofMillis(100);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(100);
    private static final PatientRetryStrategy PRS = new PatientRetryStrategy() {
        @Override
        public <T> T execute(Duration timeout, Supplier<PatientExecutionResult<T>> patientExecutionResultSupplier) {
            return null;
        }
    };
    private static final PatientExecutionHandler PEH = new PatientExecutionHandler() {
        @Override
        public <T> PatientExecutionResult<T> execute(Callable<T> callable, Predicate<T> filter) {
            return null;
        }
    };

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // --------------------------------------------------------------
    // PatientWait test cases
    // --------------------------------------------------------------

    @Test
    void testCanInstantiate() {
        try {
            new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, PEH);
        } catch (Throwable thrown) {
            throw new AssertionError("Should be able to instantiate a patient wait", thrown);
        }
    }

    @Test
    void testThrowsForNullInitialDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientWait(null, DEFAULT_TIMEOUT, PRS, PEH));
    }

    @Test
    void testThrowsForNegativeInitialDelay() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientWait(Duration.ofMinutes(-1), DEFAULT_TIMEOUT, PRS, PEH));
    }

    @Test
    void testAllowsZeroInitialDelay() {
        try {
            new PatientWait(Duration.ZERO, DEFAULT_TIMEOUT, PRS, PEH);
        } catch (Throwable thrown) {
            throw new AssertionError("Should be able to instantiate with a zero initial delay", thrown);
        }
    }

    @Test
    void testThrowsForNullDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientWait(INITIAL_DELAY, null, PRS, PEH));
    }

    @Test
    void testThrowsForNegativeDefaultTimeout() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientWait(INITIAL_DELAY, Duration.ofMinutes(-1), PRS, PEH));
    }

    @Test
    void testAllowsZeroDefaultTimeout() {
        try {
            new PatientWait(INITIAL_DELAY, Duration.ZERO, PRS, PEH);
        } catch (Throwable thrown) {
            throw new AssertionError("Should be able to instantiate with a zero initial delay", thrown);
        }
    }

    @Test
    void testThrowsForNullRetryStrategy() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, null, PEH));
    }

    @Test
    void testThrowsForNullExecutionHandler() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, null));
    }

    @Test
    void testReturnsGivenInitialDelay() {
        Assertions.assertTrue(INITIAL_DELAY == new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, PEH).getInitialDelay(),
                              "Should return the given initial delay");
    }

    @Test
    void testReturnsGivenDefaultTimeout() {
        Assertions.assertTrue(DEFAULT_TIMEOUT == new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, PEH).getDefaultTimeout(),
                              "Should return the given default timeout");
    }

    @Test
    void testReturnsGivenRetryStrategy() {
        Assertions.assertTrue(PRS == new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, PEH).getRetryStrategy(),
                              "Should return the given retry strategy");
    }

    @Test
    void testReturnsGivenExecutionHandler() {
        Assertions.assertTrue(PEH == new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, PEH).getExecutionHandler(),
                              "Should return the given execution handler");
    }

    @Test
    void testCanInstantiateBuilder() {
        Assertions.assertNotNull(PatientWait.builder(),
                                 "Static builder method should return a Builder instance");
    }

    @Test
    void testFromReturnsNonNullInstance() {
        Assertions.assertNotNull(new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, PEH).from(() -> "hello"),
                                 "Should return a non null future with a valid callable");
    }

    @Test
    void testFromThrowsForNullCallable() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new PatientWait(INITIAL_DELAY, DEFAULT_TIMEOUT, PRS, PEH).from(null));
    }

    // --------------------------------------------------------------
    // Builder test cases
    // --------------------------------------------------------------

    @Nested
    final class PatientWaitBuilderTest {

        private PatientWait.Builder getInstance() {
            return new PatientWait.Builder();
        }

        @Test
        void testCanInstantiate() {
            Assertions.assertNotNull(getInstance(),
                                     "Should be able to instantiate the patient wait builder");
        }

        @Test
        void testBuilderThrowsExceptionForNullInitialDelay() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withInitialDelay(null));
        }

        @Test
        void testBuilderThrowsExceptionForNegativeInitialDelay() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withInitialDelay(Duration.ofDays(-1)));
        }

        @Test
        void testBuilderAllowsZeroForInitialDelay() {
            getInstance().withInitialDelay(Duration.ZERO);
        }

        @Test
        void testBuilderThrowsExceptionForNullDefaultTimeout() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withDefaultTimeout(null));
        }

        @Test
        void testBuilderThrowsExceptionForNegativeDefaultTimeout() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withDefaultTimeout(Duration.ofDays(-1)));
        }

        @Test
        void testBuilderAllowsZeroForDefaultTimeout() {
            getInstance().withDefaultTimeout(Duration.ZERO);
        }

        @Test
        void testBuilderThrowsExceptionForNullRetryStrategy() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withRetryStrategy(null));
        }

        @Test
        void testBuilderThrowsExceptionForNullExecutionHandler() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().withExecutionHandler(null));
        }

        @Test
        void testBuilderStartsWithExpectedDefaultInitialDelay() {
            PatientWait wait = new PatientWait.Builder().build();
            Assertions.assertEquals(Duration.ZERO,
                                    wait.getInitialDelay(),
                                    "Builder should have given the wait the default initial delay");
            Assertions.assertEquals(Duration.ZERO,
                                    wait.getDefaultTimeout(),
                                    "Builder should have given the wait the default default timeout");
            Assertions.assertTrue(wait.getRetryStrategy() instanceof FixedDelayPatientRetryStrategy,
                                  "Builder should have given the wait the default retry strategy");
            Assertions.assertTrue(wait.getExecutionHandler() instanceof SimplePatientExecutionHandler,
                                  "Builder should have given the wait the default execution handler");
        }

        @Test
        void testBuilderTransfersGivenValuesToPatientWait() {
            PatientWait wait = new PatientWait.Builder().withInitialDelay(INITIAL_DELAY)
                                                        .withDefaultTimeout(DEFAULT_TIMEOUT)
                                                        .withRetryStrategy(PRS)
                                                        .withExecutionHandler(PEH)
                                                        .build();
            Assertions.assertEquals(INITIAL_DELAY,
                                    wait.getInitialDelay(),
                                    "Builder should have given the wait the expected initial delay");
            Assertions.assertEquals(DEFAULT_TIMEOUT,
                                    wait.getDefaultTimeout(),
                                    "Builder should have given the wait the expected default timeout");
            Assertions.assertEquals(PRS,
                                    wait.getRetryStrategy(),
                                    "Builder should have given the wait the expected retry strategy");
            Assertions.assertEquals(PEH,
                                    wait.getExecutionHandler(),
                                    "Builder should have given the wait the expected execution handler");
        }
    }
}
