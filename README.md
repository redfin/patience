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
    <version>4.0.0</version>
</dependency>
```

## Main classes

The initial entry points in the library are the `PatientWait` and the `PatientRetry` objects.
These two classes are similar in that they are immutable instances that serve as factories for
  the `PatientWaitFuture` and `PatientRetryFuture` classes.
They are both intended to be reusable.
The main customization points are the `PatientExecutionHandler`, which allow the user to customize
  the code that executes around the `PatientExecutable` given to create the future instances, and the 
  `DelaySupplierFactory`, which allows customization of the delay between unsuccessful attempts.

The future classes `PatientWaitFuture` and `PatientRetryFuture` are also immutable classes but are
  not intended for re-use.
If a valid result (as determined by the given `Predicate` used as a filter) is retrieved from the
  `PatientExecutable` then the future instance will return the valid result when the `get` method
  is called.
If no valid result is found and the timeout has been reached (`PatientWaitFuture`) or the maximum
  number of retries has been reached (`PatientRetryFuture`) then an exception will be thrown.
There are `check` methods that can be used instead of the `get` methods which return true or false
  depending on whether a valid result is found or not if you just need to know if it was successful
  instead of returning a value or throwing an exception.

## PatientWait Example usage

First, you would create the `PatientWait` instance via the builder and set the desired behaviors and properties.
Then you can use that factory (repeatedly) to generate `PatientWaitFuture` instances.
Finally you would call `get` to begin waiting.

```java
PatientWait wait = PatientWait.builder()
                              .withInitialDelay(Duration.ofSeconds(1))
                              .withDefaultTimeout(Duration.ofMinutes(2))
                              .withExecutionHandler(PatientExecutionHandlers.simple())
                              .withDelaySupplier(DelaySupplier.fixed(Duration.ofMillis(500)))
                              .build();
double result = wait.from(Math::random)
                    .withFilter(dbl -> dbl > 0.5)
                    .withMessage("Never generated a random double that was greater than 0.5")
                    .get(Duration.ofMinutes(1));
```

After this call to `get`, first the executing thread will sleep for 1 second (the initial delay).
Then the executable (`Math.random()`) will be called and the resutl will be tested with the given filter.
If it passes the filter (is greater than `0.5`) the double `result` variable will be set to the generated value.
If the generated value doesn't pass the filter, then the executing thread will sleep for 500 ms and the cycle will repeat.
If no valid value is generated and 2 minutes has been reached (or less if the next sleep would put the execution over the timeout period)
 then a `PatientTimeoutException` will be thrown with the given message.

Note that the `PatientWait` type was developed for automation tests that have external components.
We did not want to stop an executing test in a preemptive manner during a network call or to execute
  the code in a different thread than the rest of the test, but rather
  just wanted a sane way to tell the test to stop retrying if an external service is down.
Best practices are to make the `from(PatientExecutable)` executable be as "atomic" as possible and
  to note that the `PatientWait` does not guarantee that a result will return or even that a successful
  result is returned within the timeout.
If you need that level of guarantee then you should look into using an `ExecutorService` which would execute
  your code in a different thread and allow you to set a timeout.

If you just need to check if a valid result is ever found within a given timeout and
you don't want an exception thrown if it is not, there is also a `check` method available.

```java
PatientWait wait = PatientWait.builder()
                              .withInitialDelay(Duration.ofSeconds(1))
                              .withDefaultTimeout(Duration.ofMinutes(2))
                              .withExecutionHandler(PatientExecutionHandlers.simple())
                              .withDelaySupplier(DelaySupplier.fixed(Duration.ofMillis(500)))
                              .build();
boolean resultFound = wait.from(Math::random)
                          .withFilter(dbl -> dbl > 0.5)
                          .check(Duration.ofMinutes(1));
```

## PatientRetry Example usage

First, you would create the `PatientRetry` instance via the builder and set the desired behaviors and properties.
Then you can use that factory (repeatedly) to generate `PatientRetryFuture` instances.
Finally you would call `get` to begin waiting.

```java
PatientRetry retry = PatientRetry.builder()
                                 .withInitialDelay(Duration.ofSeconds(1))
                                 .withDefaultNumberOfRetries(10)
                                 .withExecutionHandler(PatientExecutionHandlers.simple())
                                 .withDelaySupplier(DelaySupplier.fixed(Duration.ofMillis(500)))
                                 .build();
double result = retry.from(Math::random)
                     .withFilter(dbl -> dbl > 0.5)
                     .withMessage("Never generated a random double that was greater than 0.5")
                     .get(5); // number of retries
```

After this call to `get`, first the executing thread will sleep for 1 second (the initial delay).
Then the executable (`Math.random()`) will be called and the result will be tested with the given filter.
If it passes the filter (is greater than `0.5`) the double `result` variable will be set to the generated value.
If the generated value doesn't pass the filter, then the executing thread will sleep for 500 ms and the cycle will repeat.
If no valid value is generated and 6 attempts have occurred (the initial attempt and 5 retries)
 then a `PatientRetryException` will be thrown with the given message.

If you just need to check if a valid result is ever found within a given timeout and
you don't want an exception thrown if it is not, there is also a `check` method available.

```java
PatientRetry retry = PatientRetry.builder()
                                 .withInitialDelay(Duration.ofSeconds(1))
                                 .withDefaultNumberOfRetries(10)
                                 .withExecutionHandler(PatientExecutionHandlers.simple())
                                 .withDelaySupplier(DelaySupplier.fixed(Duration.ofMillis(500)))
                                 .build();
boolean resultFound = retry.from(Math::random)
                           .withFilter(dbl -> dbl > 0.5)
                           .check(5); // number of retries
```
