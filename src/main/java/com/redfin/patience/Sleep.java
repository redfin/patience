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

import com.redfin.patience.exceptions.PatientInterruptedException;

import java.time.Duration;

import static com.redfin.validity.Validity.validate;

/**
 * A type that denotes the ability to make the currently executing thread sleep for a given
 * amount of time. This allows for customization of how a thread is made to sleep.
 */
@FunctionalInterface
public interface Sleep {

    /**
     * Make the current thread wait for the given amount of milliseconds and nanoseconds.
     * The behavior of passing in zero for both arguments is undefined.
     * In general code outside of this interface should use the {@link #sleepFor(Duration)}
     * method instead of this one directly.
     *
     * @param millis the number of milliseconds to sleep.
     *               May not be negative.
     * @param nanos  the number of nanoseconds to wait in addition to the milliseconds as long
     *               as nanosecond accuracy is possible.
     *               Must be in the range of {@code 0-999,999}.
     *
     * @throws IllegalArgumentException if millis is negative or if nanos is not the range of 0-999,999.
     * @throws InterruptedException     if an interruption occurs while sleeping/waiting.
     */
    void sleepFor(long millis, int nanos) throws InterruptedException;

    /**
     * A method that converts the given {@link Duration} into milliseconds and nanoseconds
     * and then calls {@link #sleepFor(long, int)} with those values. Any
     * {@link InterruptedException} thrown by the sleepFor method will be caught and a
     * {@link PatientInterruptedException} will be thrown.
     *
     * @param duration the {@link Duration} to sleepFor the current thread for.
     *                 May not be null or negative. If it is zero, then nothing happens.
     *
     * @throws IllegalArgumentException    if duration is null or negative.
     * @throws ArithmeticException         if numeric overflow occurs when converting the duration to milliseconds
     *                                     and nanoseconds.
     * @throws PatientInterruptedException if an {@link InterruptedException} is thrown while sleeping.
     * @see Thread#sleep(long)
     */
    default void sleepFor(Duration duration) {
        validate().that(duration).isGreaterThanOrEqualTo(Duration.ZERO);
        if (!duration.isZero()) {
            long milliseconds = duration.toMillis();
            int nanoseconds = (int) (duration.toNanos() % 1_000_000);
            try {
                sleepFor(milliseconds, nanoseconds);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new PatientInterruptedException(String.format("Thread sleeping for [ %s ] was interrupted.",
                                                                    duration),
                                                      interrupted);
            }
        }
    }
}
