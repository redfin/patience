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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

interface ExceptionContract<X extends Throwable> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    X getInstance();

    X getInstance(String message);

    X getInstance(Throwable cause);

    X getInstance(String message,
                  Throwable cause);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // ----------------------------------------------------
    // No argument constructor tests
    // ----------------------------------------------------

    @Test
    default void testCanInstantiateWithNoArguments_ExceptionContract() {
        Assertions.assertNotNull(getInstance(),
                                 "Should be able to instantiate the exception with no arguments.");
    }

    @Test
    default void testNoArgumentExceptionReturnsNullForMessage_ExceptionContract() {
        Assertions.assertNull(getInstance().getMessage(),
                              "Should return null for message with no argument constructor.");
    }

    @Test
    default void testNoArgumentExceptionReturnsNullForCause_ExceptionContract() {
        Assertions.assertNull(getInstance().getCause(),
                              "Should return null for cause with no argument constructor.");
    }

    // ----------------------------------------------------
    // String argument constructor tests
    // ----------------------------------------------------

    @Test
    default void testCanInstantiateWithStringArgument_ExceptionContract() {
        Assertions.assertNotNull(getInstance("hello"),
                                 "Should be able to instantiate the exception with a string argument.");
    }

    @Test
    default void testCanInstantiateWithNullStringArgument_ExceptionContract() {
        Assertions.assertNotNull(getInstance((String) null),
                                 "Should be able to instantiate the exception with a null string argument.");
    }

    @Test
    default void testStringArgumentExceptionReturnsGivenNonNullMessage_ExceptionContract() {
        String message = "world";
        Assertions.assertEquals(message,
                                getInstance(message).getMessage(),
                                "Should return given message with string argument constructor.");
    }

    @Test
    default void testStringArgumentExceptionReturnsGivenNullMessage_ExceptionContract() {
        Assertions.assertNull(getInstance((String) null).getMessage(),
                              "Should return given null message with string argument constructor.");
    }


    @Test
    default void testStringArgumentExceptionReturnsNullForCause_ExceptionContract() {
        Assertions.assertNull(getInstance("hello").getCause(),
                              "Should return null for cause with string argument constructor.");
    }

    // ----------------------------------------------------
    // Throwable argument constructor tests
    // ----------------------------------------------------

    @Test
    default void testCanInstantiateWithThrowableArgument_ExceptionContract() {
        Assertions.assertNotNull(getInstance(new RuntimeException("whoops")),
                                 "Should be able to instantiate the exception with a throwable argument.");
    }

    @Test
    default void testCanInstantiateWithNullThrowableArgument_ExceptionContract() {
        Assertions.assertNotNull(getInstance((Throwable) null),
                                 "Should be able to instantiate the exception with a null throwable argument.");
    }

    @Test
    default void testThrowableArgumentExceptionReturnsGivenNonNullCause_ExceptionContract() {
        String message = "whoops";
        Assertions.assertEquals(message,
                                getInstance(new RuntimeException(message)).getCause().getMessage(),
                                "Should return given cause with throwable argument constructor.");
    }

    @Test
    default void testThrowableArgumentExceptionReturnsGivenNullCause_ExceptionContract() {
        Assertions.assertNull(getInstance((Throwable) null).getCause(),
                              "Should return given null cause with throwable argument constructor.");
    }

    @Test
    default void testThrowableArgumentExceptionReturnsCauseMessageForMessage_ExceptionContract() {
        String causeMessage = "whoops";
        Assertions.assertEquals("java.lang.RuntimeException: " + causeMessage,
                                getInstance(new RuntimeException(causeMessage)).getMessage(),
                              "Should return the message of the cause for message from throwable constructor.");
    }

    // ----------------------------------------------------
    // String and throwable arguments constructor tests
    // ----------------------------------------------------

    @Test
    default void testCanInstantiateWithCauseAndMessageArguments_ExceptionContract() {
        Assertions.assertNotNull(getInstance("hello", new RuntimeException("whoops")),
                                 "Should be able to instantiate with a message and cause constructor.");
    }

    @Test
    default void testCanInstantiateWithCauseAndNullMessageArguments_ExceptionContract() {
        Assertions.assertNotNull(getInstance(null, new RuntimeException("whoops")),
                                 "Should be able to instantiate with a null message and cause constructor.");
    }

    @Test
    default void testCanInstantiateWithNullCauseAndNonNullMessageArguments_ExceptionContract() {
        Assertions.assertNotNull(getInstance("hello", null),
                                 "Should be able to instantiate with a message and null cause constructor.");
    }

    @Test
    default void testCanInstantiateWithNullCauseAndNullMessageArguments_ExceptionContract() {
        Assertions.assertNotNull(getInstance(null, null),
                                 "Should be able to instantiate with a both a null message and a null cause constructor.");
    }

    @Test
    default void testCauseAndMessageArgumentExceptionReturnsGivenNonNullMessage_ExceptionContract() {
        String message = "hello";
        Assertions.assertEquals(message,
                                getInstance(message, new RuntimeException("whoops")).getMessage(),
                              "Should return the given message with a message and cause constructor.");
    }

    @Test
    default void testCauseAndMessageArgumentExceptionReturnsGivenNullMessage_ExceptionContract() {
        Assertions.assertNull(getInstance(null, new RuntimeException("whoops")).getMessage(),
                                "Should return the given null message with a message and cause constructor.");
    }

    @Test
    default void testCauseAndMessageArgumentExceptionReturnsGivenNonNullCause_ExceptionContract() {
        String message = "whoops";
        Assertions.assertEquals(message,
                                getInstance("hello", new RuntimeException(message)).getCause().getMessage(),
                                "Should return the given cause with a message and cause constructor.");
    }

    @Test
    default void testCauseAndMessageArgumentExceptionReturnsGivenNullCause_ExceptionContract() {
        String message = "world";
        Assertions.assertNull(getInstance(message, null).getCause(),
                              "Should return the given null cause with a message and cause constructor.");
    }
}
