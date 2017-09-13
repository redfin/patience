package com.redfin.patience.retrystrategies;

import com.redfin.patience.PatientException;
import com.redfin.patience.PatientExecutionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

final class AbstractDelayPatientRetryStrategyTest {

    private static final class NullReturningDelayPatientRetryStrategy
            extends AbstractDelayPatientRetryStrategy {

        @Override
        protected Supplier<Duration> getDelayDurationsSupplier() {
            return () -> null;
        }
    }

    private static final class NegativeReturningDelayPatientRetryStrategy
            extends AbstractDelayPatientRetryStrategy {

        @Override
        protected Supplier<Duration> getDelayDurationsSupplier() {
            return () -> Duration.ofMillis(1)
                                 .negated();
        }
    }

    private static final Supplier<PatientExecutionResult<String>> FAILURE_RESULT_SUPPLIER;
    private static final AbstractDelayPatientRetryStrategy NULL_DURATION_STRATEGY;
    private static final AbstractDelayPatientRetryStrategy NEGATIVE_DURATION_STRATEGY;

    static {
        FAILURE_RESULT_SUPPLIER = () -> PatientExecutionResult.failure("whoops");
        NULL_DURATION_STRATEGY = new NullReturningDelayPatientRetryStrategy();
        NEGATIVE_DURATION_STRATEGY = new NegativeReturningDelayPatientRetryStrategy();
    }

    @Test
    void testExecuteWithDelaySupplierThatReturnsNullDurationThrowsException() {
        Assertions.assertThrows(PatientException.class,
                                () -> NULL_DURATION_STRATEGY.execute(Duration.ofMillis(10), FAILURE_RESULT_SUPPLIER),
                                "A PatientException should be thrown if the duration supplier returns a null duration");
    }

    @Test
    void testExecuteWithDelaySupplierThatReturnsNegativeDurationThrowsException() {
        Assertions.assertThrows(PatientException.class,
                                () -> NEGATIVE_DURATION_STRATEGY.execute(Duration.ofMillis(10), FAILURE_RESULT_SUPPLIER),
                                "A PatientException should be thrown if the duration supplier returns a negative duration");
    }
}
