package com.redfin.patience.executionhandlers;

import org.junit.jupiter.api.Test;

final class IgnoringAllPatientExecutionHandlerTest implements PatientExecutionHandlerContract<IgnoringAllPatientExecutionHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public IgnoringAllPatientExecutionHandler getInstance() {
        return new IgnoringAllPatientExecutionHandler();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testSwallowsThrownError() {
        getInstance().execute(() -> { throw new AssertionError("whoops"); },
                              result -> true);
    }

    @Test
    void testSwallowsThrownException() {
        getInstance().execute(() -> { throw new RuntimeException("whoops"); },
                              result -> true);
    }
}
