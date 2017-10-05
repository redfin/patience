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
    <version>3.3.0</version>
</dependency>
```

## Main classes

The initial entry into the library is via a `PatientWait` object.
This is an immutable instance, with a builder for easy and fluent object creation,
 that contains the standard wait configurations.
It is intended to be re-usable.
The `PatientWait` instance is a factory that is used to create `PatientFuture` objects via the `from` method.
An implementation of the `Executable` interface (basically a Java `Callable` that can throw any Throwable) is given
 as the argument to the `from` method.
A `PatientFuture` is also immutable but not intended to be re-used.
You can supply a custom filter (`Predicate` with which to test results) and a string to use as a message
 if the expected condition never occurs.
You begin actually waiting for a result by calling the `get()` or `get(Duration)` methods on the `PatientFuture` object.
If the `Executable` returns a valid result within the timeout then the result will be returned from `get`.
If no valid result is found within the timeout, then a `PatientTimeoutException` will be thrown.

## Example usage

First, you would create the `PatientWait` instance via the builder and set the desired behaviors and properties.
Then you can use that factory (repeatedly) to generate `PatientFuture` instances.
Finally you would call `get` to begin waiting.

```java
PatientWait wait = PatientWait.builder()
                              .withInitialDelay(Duration.ofSeconds(1))
                              .withDefaultTimeout(Duration.ofMinutes(2))
                              .withRetryHadler(PatientRetryHandlers.fixedDelay(Duration.ofMillis(500)))
                              .withExecutionHandler(PatientExecutionHandlers.simple())
                              .build();
double result = wait.from(Math::random)
                      .withFilter(dbl -> dbl > 0.5)
                      .withMessage("Never generatedd a random double that was greater than 0.5")
                      .get(Duration.ofMinutes(1));
```

After this call to `get`, first the executing thread will sleep for 1 second (the initial delay).
Then the executable (`Math.random()`) will be called and the resutl will be tested with the given filter.
If it passes the filter (is greater than `0.5`) the double `result` variable will be set to the generated value.
If the generated value doesn't pass the filter, then the executing thread will sleep for 500 ms and the cycle will repeat.
If no valid value is generated and 2 minutes has been reached (or less if the next sleep would put the execution over the timeout period)
 then a `PatientTimeoutException` will be thrown with the given message.

## Customization points

The two main customization points of the `Patience` library are the `PatientRetryHandler` and the `PatientExecutionHandler`.
The `PatientRetryHandler` controls behavior following an unsuccessful attempt to get a valid value from the `Executable` and the
 next attempt (if any).
The `PatientExecutionHandler` is where the behavior around the extraction of a value from the given `Executable` and the
testing of that value with the given filter `Predicate` is controlled.
