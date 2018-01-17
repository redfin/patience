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

package com.redfin.patience.executions;

import com.redfin.patience.exceptions.PatientExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

@DisplayName("When an IgnoringExecutionHandler")
final class IgnoringExecutionHandlerTest
    extends AbstractExecutionHandlerTest<IgnoringExecutionHandler> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public IgnoringExecutionHandler getInstance() {
        return getInstance(Collections.emptyList());
    }

    private IgnoringExecutionHandler getInstance(Collection<Class<? extends Throwable>> ignoredThrowableTypes) {
        return new IgnoringExecutionHandler(ignoredThrowableTypes);
    }

    private IgnoringExecutionHandler getInstance(Collection<Class<? extends Throwable>> ignoredThrowableTypes,
                                                 Collection<Class<? extends Throwable>> notIgnoredThrowableTypes) {
        return new IgnoringExecutionHandler(ignoredThrowableTypes, notIgnoredThrowableTypes);
    }

    static final class ValidIgnoredCollections
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((Class<RuntimeException>) null),
                             Arguments.of(Collections.emptyList()),
                             Arguments.of(Collections.singletonList(RuntimeException.class)));
        }
    }

    static final class ValidIgnoredAndNotIgnoredCollections
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, null),
                             Arguments.of(null, Collections.emptyList()),
                             Arguments.of(null, Collections.singletonList(RuntimeException.class)),
                             Arguments.of(Collections.emptyList(), null),
                             Arguments.of(Collections.emptyList(), Collections.emptyList()),
                             Arguments.of(Collections.emptyList(), Collections.singletonList(RuntimeException.class)),
                             Arguments.of(Collections.singletonList(RuntimeException.class), null),
                             Arguments.of(Collections.singletonList(RuntimeException.class), Collections.emptyList()),
                             Arguments.of(Collections.singletonList(RuntimeException.class), Collections.singletonList(RuntimeException.class)));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("is constructed")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("it returns successfully for ignored throwable collection")
        @ArgumentsSource(ValidIgnoredCollections.class)
        void testCanInstantiateWithNullCollection(Collection<Class<? extends Throwable>> ignored) {
            Assertions.assertNotNull(getInstance(ignored),
                                     "Should be able to instantiate with the given collection: " + ignored);
        }

        @ParameterizedTest
        @DisplayName("it returns successfully for ignored throwable and not ignored throwable collections")
        @ArgumentsSource(ValidIgnoredAndNotIgnoredCollections.class)
        void testCanInstantiateWithNullCollection(Collection<Class<? extends Throwable>> ignored,
                                                  Collection<Class<? extends Throwable>> notIgnored) {
            Assertions.assertNotNull(getInstance(ignored, notIgnored),
                                     "Should be able to instantiate with the given ignored collection: " + ignored + ", and not ignored collection: " + notIgnored);
        }
    }

    @Nested
    @DisplayName("is executing with ignored types")
    final class IgnoringBehaviorTests {

        @Test
        @DisplayName("it throws an exception for non-ignored type thrown from executable")
        void testThrowsExceptionForNonIgnoredTypeFromExecutable() {
            Assertions.assertThrows(PatientExecutionException.class,
                                    () -> getInstance(Collections.singletonList(RuntimeException.class))
                                            .execute(() -> {
                                                         throw new AssertionError("whoops");
                                                     },
                                                     getNonNullPredicate()),
                                    "Should throw an execution exception for a non-ignored throwable type from executable");
        }

        @Test
        @DisplayName("it throws an exception for non-ignored type thrown from filter")
        void testThrowsExceptionForNonIgnoredTypeFromFilter() {
            Assertions.assertThrows(PatientExecutionException.class,
                                    () -> getInstance(Collections.singletonList(RuntimeException.class))
                                            .execute(() -> true,
                                                     bool -> {
                                                         throw new AssertionError("whoops");
                                                     }),
                                    "Should throw an execution exception for a non-ignored throwable type from filter");
        }

        @Test
        @DisplayName("it does not throw an exception for ignored type thrown from executable")
        void testDoesNotThrowForIgnoredTypeFromExecutable() {
            getInstance(Collections.singletonList(AssertionError.class)).execute(() -> {
                                                                                     throw new AssertionError("whoops");
                                                                                 },
                                                                                 getNonNullPredicate());
        }

        @Test
        @DisplayName("it does not throw an exception for ignored type thrown from filter")
        void testDoesNotThrowForIgnoredTypeFromFilter() {
            getInstance(Collections.singletonList(AssertionError.class)).execute(() -> true,
                                                                                 bool -> {
                                                                                     throw new AssertionError("whoops");
                                                                                 });
        }

        @Test
        @DisplayName("it does not throw an exception for ignored super type thrown from executable")
        void testDoesNotThrowForIgnoredSuperTypeFromExecutable() {
            getInstance(Collections.singletonList(RuntimeException.class)).execute(() -> {
                                                                                       throw new IllegalArgumentException("whoops");
                                                                                   },
                                                                                   getNonNullPredicate());

        }

        @Test
        @DisplayName("it does not throw an exception for ignored super type thrown from filter")
        void testDoesNotThrowForIgnoredSuperTypeFromFilter() {
            getInstance(Collections.singletonList(RuntimeException.class)).execute(() -> true,
                                                                                   bool -> {
                                                                                       throw new IllegalArgumentException("whoops");
                                                                                   });
        }

        @Test
        @DisplayName("it throws an exception if explicitly not ignored throwable is thrown from executable")
        void testDoesNotIgnoreDoNotIgnoreThrowableFromExecutable() {
            Assertions.assertThrows(PatientExecutionException.class,
                                    () -> getInstance(Collections.singleton(Throwable.class),
                                                      Collections.singletonList(OutOfMemoryError.class)).execute(() -> {
                                                                                                                     throw new OutOfMemoryError("whoops");
                                                                                                                 },
                                                                                                                 bool -> true),
                                    "Should propagate an exception on not ignored throwable.");
        }

        @Test
        @DisplayName("it throws an exception if explicitly not ignored throwable is thrown from filter")
        void testDoesNotIgnoreDoNotIgnoreThrowableFromFilter() {
            Assertions.assertThrows(PatientExecutionException.class,
                                    () -> getInstance(Collections.singleton(Throwable.class),
                                                      Collections.singletonList(OutOfMemoryError.class)).execute(() -> true,
                                                                                                                 bool -> {
                                                                                                                     throw new OutOfMemoryError("whoops");
                                                                                                                 }),
                                    "Should propagate an exception on not ignored throwable.");
        }
    }
}
