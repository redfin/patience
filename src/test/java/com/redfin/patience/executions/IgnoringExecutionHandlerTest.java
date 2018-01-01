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

import com.redfin.patience.PatientExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

final class IgnoringExecutionHandlerTest
 implements PatientExecutionHandlerContract<IgnoringExecutionHandler> {

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

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testCanInstantiateWithNullCollection() {
        Assertions.assertNotNull(getInstance(null),
                                 "Should be able to instantiate with a null ignored throwable type collection");
    }

    @Test
    void testCanInstantiateWithEmptyCollection() {
        Assertions.assertNotNull(getInstance(Collections.emptyList()),
                                 "Should be able to instantiate with an empty ignored throwable type collection");
    }

    @Test
    void testCanInstantiateWithNonEmptyCollection() {
        Assertions.assertNotNull(getInstance(Collections.singletonList(RuntimeException.class)),
                                 "Should be able to instantiate with a non-empty ignored throwable type collection");
    }

    @Test
    void testThrowsExceptionForNonIgnoredTypeFromExecutable() {
        Assertions.assertThrows(PatientExecutionException.class,
                                () -> getInstance(Collections.singletonList(RuntimeException.class))
                                        .execute(() -> { throw new AssertionError("whoops"); },
                                                 getNonNullPredicate()),
                                "Should throw an execution exception for a non-ignored throwable type from executable");
    }

    @Test
    void testThrowsExceptionForNonIgnoredTypeFromFilter() {
        Assertions.assertThrows(PatientExecutionException.class,
                                () -> getInstance(Collections.singletonList(RuntimeException.class))
                                        .execute(() -> true,
                                                 bool -> { throw new AssertionError("whoops"); }),
                                "Should throw an execution exception for a non-ignored throwable type from filter");
    }

    @Test
    void testDoesNotThrowForIgnoredTypeFromExecutable() {
        getInstance(Collections.singletonList(AssertionError.class)).execute(() -> { throw new AssertionError("whoops"); },
                                                                             getNonNullPredicate());
    }

    @Test
    void testDoesNotThrowForIgnoredTypeFromFilter() {
        getInstance(Collections.singletonList(AssertionError.class)).execute(() -> true,
                                                                             bool -> { throw new AssertionError("whoops"); });
    }

    @Test
    void testDoesNotThrowForIgnoredSuperTypeFromExecutable() {
        getInstance(Collections.singletonList(RuntimeException.class)).execute(() -> { throw new IllegalArgumentException("whoops"); },
                                                                             getNonNullPredicate());

    }

    @Test
    void testDoesNotThrowForIgnoredSuperTypeFromFilter() {
        getInstance(Collections.singletonList(RuntimeException.class)).execute(() -> true,
                                                                               bool -> { throw new IllegalArgumentException("whoops"); });
    }

    @Test
    void testDoesNotIgnoreDoNotIgnoreThrowable() {
        Assertions.assertThrows(PatientExecutionException.class,
                                () -> getInstance(Collections.singleton(Throwable.class),
                                                  Collections.singletonList(OutOfMemoryError.class)).execute(() -> true,
                                                                                                             bool -> { throw new OutOfMemoryError("whoops"); }),
        "Should propagate an exception on not ignored throwable.");
    }
}
