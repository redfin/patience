package com.redfin.patience.executionhandlers;

import com.redfin.patience.PatientExecutionResult;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * An immutable sub class of the {@link AbstractPatientExecutionHandler}. It catches
 * any exception thrown during the execution of the given callable or predicate and
 * ignores them to continue executing the wait.
 */
public class IgnoringAllPatientExecutionHandler
     extends AbstractPatientExecutionHandler {

    @Override
    public <T> PatientExecutionResult<T> execute(Callable<T> callable,
                                                 Predicate<T> filter) {
        validate().that(callable).isNotNull();
        validate().that(filter).isNotNull();
        try {
            return executeHelper(callable, filter);
        } catch (Throwable thrown) {
            return PatientExecutionResult.failure("Ignored throwable -> " + thrown.toString());
        }
    }
}
