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

import java.time.Duration;

import static com.redfin.validity.Validity.validate;

/**
 * A non-instantiable class that contains methods for sleeping
 * threads using {@link Duration} objects and using unchecked
 * exceptions rather than checked ones.
 */
public final class PatientSleep {

    /**
     * A wrapper around {@link Thread#sleep(long)} that takes in a {@link Duration}
     * as the argument. The duration is then converted into milliseconds to use in the
     * call to the sleep method. Any nanosecond accuracy (e.g. nanoseconds between each
     * millisecond) in the duration is ignored. Any {@link InterruptedException} is caught
     * and propagated via an unchecked exception of type {@link PatientInterruptedException}.
     * If no exception is thrown, then this simply sleeps the current thread just as the thread
     * sleep method does.
     *
     * @param duration the {@link Duration} to sleepFor the current thread for.
     *                 May not be null or negative. If it is zero, then nothing happens.
     *
     * @throws IllegalArgumentException    if duration is null or negative.
     * @throws ArithmeticException         if numeric overflow occurs when converting the duration to milliseconds.
     * @throws PatientInterruptedException if an {@link InterruptedException} is thrown while sleeping.
     * @see Thread#sleep(long)
     */
    public static void sleepFor(Duration duration) {
        validate().that(duration).isGreaterThanOrEqualTo(Duration.ZERO);
        if (!duration.isZero()) {
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new PatientInterruptedException(String.format("Thread sleeping for [ %s ] was interrupted.",
                                                                    duration),
                                                      interrupted);
            }
        }
    }

    /*
     * Ensure this class is non-instantiable.
     */

    private PatientSleep() {
        throw new AssertionError("Cannot instantiate the PatientSleep class");
    }
}
