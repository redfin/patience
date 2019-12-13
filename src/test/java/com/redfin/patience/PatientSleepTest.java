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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("A PatientSleep implementation, when sleepFor(Duration) is called")
final class PatientSleepTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private PatientSleep getDefaultSleep() {
        return Thread::sleep;
    }

    private static final class InvalidDurationArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((Duration) null),
                             Arguments.of(Duration.ofMillis(-500)));
        }
    }

    private static final class DurationAndConversionArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // index 0 - Duration
            // index 1 - duration in millis
            // index 2 - nanosecond precision
            Duration day = Duration.ofDays(1);
            return Stream.of(Arguments.of(Duration.ofMillis(100), 100L, 0),
                             Arguments.of(Duration.ofNanos(1_000_012), 1L, 12),
                             Arguments.of(Duration.ofNanos(2_999_999), 2L, 999_999),
                             Arguments.of(day.plus(Duration.ofNanos(980_000)), day.toMillis(), 980_000));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("when sleepFor(Duration) is called")
    final class SleepForDurationTests {

        @ParameterizedTest
        @DisplayName("throws an exception for an invalid duration")
        @ArgumentsSource(InvalidDurationArgumentsProvider.class)
        void testSleepForThrowsExceptionForInvalidDuration(Duration duration) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getDefaultSleep().sleepFor(duration),
                                    "Should throw an exception for an invalid duration.");
        }

        @Test
        @DisplayName("is a no-op for a zero duration")
        void testSleepForCanBeCalledWithZeroDuration() throws InterruptedException {
            PatientSleep sleep = mock(PatientSleep.class);
            sleep.sleepFor(Duration.ZERO);
            verify(sleep, times(0)).sleepFor(1, 0);
        }

        @ParameterizedTest
        @DisplayName("calls sleepFor(long, int) with the expected arguments for the given duration")
        @ArgumentsSource(DurationAndConversionArgumentsProvider.class)
        void testSleepForProperlyConvertsTheGivenDuration(Duration duration,
                                                          long expectedMillis,
                                                          int expectedNanos) throws InterruptedException {
            PatientSleep sleep = spy(PatientSleep.class);
            sleep.sleepFor(duration);
            verify(sleep, times(1)).sleepFor(expectedMillis, expectedNanos);
        }

        @Test
        @DisplayName("throws an exception for overflow")
        void testSleepForThrowsExceptionOnOverflow() {
            Duration duration = Duration.ofSeconds(Long.MAX_VALUE);
            Assertions.assertThrows(ArithmeticException.class,
                                    () -> getDefaultSleep().sleepFor(duration));
        }

        @Test
        @DisplayName("throws an exception for an interruption")
        void testSleepForThrowsForInterrupted() {
            PatientSleep sleep = (millis, nanos) -> {
                throw new InterruptedException("expected");
            };
            Assertions.assertThrows(PatientInterruptedException.class,
                                    () -> sleep.sleepFor(Duration.ofMinutes(10)),
                                    "Should throw a PatientInterruptedException if the sleep is interrupted.");
        }
    }
}
