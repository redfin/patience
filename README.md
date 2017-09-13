[![Build Status](https://travis-ci.org/redfin/patience.svg?branch=master)](https://travis-ci.org/redfin/patience)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Patience

## Overview

Patience is a fluent, customizable Java library for waiting on expected conditions.
There are a few interfaces that allow for highly different behaviors at key
sections along with immutable classes to implement the generic behavior that
occurs around those customization points.

## Installation

```xml
<dependency>
    <groupId>com.redfin</groupId>
    <artifactId>patience</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Main classes

The initial entry into the library is via a `PatientWait` object.
This is an immutable instance, with a builder for easy and fluent object creation,
 that contains the standard wait configurations.
It is intended to be re-usable.

Once you have created a `PatientWait` instance you start to make a wait call by supplying a `Callable`
 to the `from` method of the `PatientWait` object instance. This will return a `DefaultPatientFuture`
 instance. Either the `DefaultPatientFuture` or the `FilteredPatientFuture` objects allow you to extract
 the value. Doing so from a `DefaultPatientFuture` object will use a default filter to test the returned
 values that considers any non-null, non-false value to be a valid result. You can use a custom test
 by calling the `withFilter` method on the `DefaultPatientFuture` object which will return a `FilteredPatientFuture`.

You can then extract the value from any `PatientFuture` object via the `get` methods.
There are two, one that uses the default timeout (set in the `PatientWait`) or one that takes in a custom timeout.
The `PatientFuture` will then either wait until a valid value is found and return it or throw a `PatientTimeoutException`
if the timeout is reached without having found a valid value. It may return from the call earlier than the full timeout if
an unsuccessful attempt has occurred and the next attempt would not be scheduled until after the timeout has been reached.

## Example usage

First, you would create the `PatientWait` instance.

```java
PatientWait wait = PatientWait.builder()
                              .withInitialDelay(Duration.ofSeconds(1))
                              .withDefaultTimeout(Duration.ofMinutes(2))
                              .withRetryStrategy(PatientRetryStrategies.withFixedDelay(Duration.ofMillis(500)))
                              .withExecutionHandler(PatientExecutionHandlers.simpleHandler())
                              .build();
```

Then you can use that (repeatedly) to wait for a condition:

```java
String[] array = {"hello", "1", "a", "b", "c"};
wait.from(() -> array[(int)(Math.random() * array.length)])
    .withFilter(t -> t.length() > 1)
    .get(Duration.ofMinutes(1));
```

Since the `PatientWait` object simply contains the handlers for retry behavior, execution handler, and timing
the wait object can be reused. When the `from` method is called on `wait` it will return a new object that gets
the information from `wait` and that is the object whose state is consumed by the repeated attempts. This does,
however, assume that any handlers given to the `PatientWait` object are stateless between executions.

In the above example, the `wait` object will make repeated attempts of calculating an index of the array
and checking the String at that index against the filter that is checking for Strings with a length
greater than 1. If one attempt returns a String with a length of 1, then it will wait for 500 milliseconds
and then try again. Once the random index computation calculates 0 it will return the String `hello` and
be done. If it never calculates an index of 0 and the timeout is reached, then a `PatientTimeoutException`
will be thrown.

## Customization points

The two main customization points of the `Patience` library are the `PatientRetryStrategy` and the `PatientExecutionHandler`.
The `PatientRetryStrategy` is where the behavior around repeated executions of the `Callable` given in the `from` method on an unsuccessful attempt.
This can include things like logging, changing how long to wait between attempts, etc.
The `PatientExecutionHandler` is where the behavior around the extraction of a value from the given `Callable` and the
testing of that value with the given filter `Predicate` is controlled.

