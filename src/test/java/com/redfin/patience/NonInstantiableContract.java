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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

interface NonInstantiableContract<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return the class object of the class being tested.
     * Should never return null.
     */
    Class<T> getClassObject_NonInstantiableContract();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("is a class marked as final")
    default void testClassIsMarkedAsFinal_NonInstantiableContract() {
        // Get test class and validate precondition
        Class<T> clazz = getClassObject_NonInstantiableContract();
        Assertions.assertTrue(Modifier.isFinal(clazz.getModifiers()),
                              "A non instantiable class should be marked as final");
    }

    @Test
    @DisplayName("is a class with only one constructor")
    default void testClassHasOnlyOneConstructor_NonInstantiableContract() {
        // Get test class and validate precondition
        Class<T> clazz = getClassObject_NonInstantiableContract();
        // Perform actual test
        Assertions.assertEquals(1,
                                clazz.getDeclaredConstructors().length,
                                "A non instantiable class should only have 1 constructor");
    }

    @Test
    @DisplayName("is a class that has a constructor that takes in no arguments")
    default void testClassHasTheZeroArgumentConstructor_NonInstantiableContract() throws NoSuchMethodException {
        // Get test class and validate precondition
        Class<T> clazz = getClassObject_NonInstantiableContract();
        // Perform actual test
        Assertions.assertNotNull(clazz.getDeclaredConstructor(),
                                 "A non instantiable class should have a zero argument constructor");
    }

    @Test
    @DisplayName("is a class whose no argument constructor is marked as private")
    default void testClassSingleConstructorIsPrivate_NonInstantiableContract() throws NoSuchMethodException {
        // Get test class and validate precondition
        Class<T> clazz = getClassObject_NonInstantiableContract();
        // Perform actual test
        Assertions.assertTrue(Modifier.isPrivate(clazz.getDeclaredConstructor().getModifiers()),
                              "A non instantiable class should have a private zero argument constructor");
    }

    @Test
    @DisplayName("is a class who no argument constructor throws an AssertionError if called")
    default void testClassThrowsAssertionErrorIfConstructorIsCalled_NonInstantiableContract() {
        // Get test class and validate precondition
        Class<T> clazz = getClassObject_NonInstantiableContract();
        // Perform actual test
        Throwable thrown = Assertions.assertThrows(InvocationTargetException.class,
                                                   () -> {
                                                       Constructor<T> c = clazz.getDeclaredConstructor();
                                                       c.setAccessible(true);
                                                       c.newInstance();
                                                   });
        Assertions.assertTrue(thrown.getCause() instanceof AssertionError,
                              "The invocation target error should wrap an assertion error");
    }

    @Test
    @DisplayName("is a class that only has static fields")
    default void testClassOnlyHasStaticMembers_NonInstantiableContract() {
        // Get test class and validate precondition
        Class<?> clazz = getClassObject_NonInstantiableContract();
        // Perform actual test
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        Assertions.assertAll("All methods of a non-instantiable class should be static",
                             fields.stream()
                                   .map(field -> () -> Assertions.assertTrue(Modifier.isStatic(field.getModifiers()),
                                                                             "field [" + field.getName() + "] should be static")));
    }

    @Test
    @DisplayName("is a class that only has static methods")
    default void testClassOnlyHasStaticMethods_NonInstantiableContract() {
        // Get test class and validate precondition
        Class<?> clazz = getClassObject_NonInstantiableContract();
        // Perform actual test
        List<Method> methods = new ArrayList<>();
        while (clazz != Object.class) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        Assertions.assertAll("All methods of a non-instantiable class should be static",
                             methods.stream()
                                    .map(method -> () -> Assertions.assertTrue(Modifier.isStatic(method.getModifiers()),
                                                                               "method [" + method.getName() + "] should be static")));
    }
}
