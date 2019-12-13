[![Build Status](https://travis-ci.org/redfin/patience.svg?branch=master)](https://travis-ci.org/redfin/patience)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Patience

## Overview

Patience is a fluent, customizable Java library for waiting on expected conditions. There are a few interfaces that
allow for highly different behaviors at key sections along with immutable classes to implement the generic behavior
that occurs around those customization points.

Desired behavior:
* The ability to execute a block of code repeatedly and check the result against a filter.
* To have a "sane" break out condition so we don't loop infinitely
  * But don't break out during the execution of the code block, only check the condition "between" attempts.
* If the "break out" condition is reached and no valid result has been found then throw an exception that contains
 information about the unsuccessful attempts that have been made.
* At a minimum case, the block of code should be executed at least once (e.g. so it's like having the code block inline
 rather than using the Patience library if a timeout of `0` is set).

## Installation

```xml
<dependency>
    <groupId>com.redfin</groupId>
    <artifactId>patience</artifactId>
    <version>5.0.0</version>
</dependency>
```

## Main Classes and Interfaces

### PatientWait / PatientRetry

A couple of immutable classes that are very similar in most respects and act as factories for the actual future objects
(see `PatientWaitFuture` and `PatientRetryFuture` below) that will execute the code that can be retried. Each are built
via a Builder class that allows for customization of the behavior of the future objects they create. These do not
contain mutable state and are intended to be able to be created and reused multiple times and even across different
threads.

### PatientWaitFuture / PatientRetryFuture

A couple of immutable classes that are created from the `PatientWait` and `PatientRetry` instances respectively. These
are the core instances you directly interact with that execute the conditional waiting. Please note that while these
classes themselves are immutable some of their internal structures may contain state and, unlike the `PatientWait` and
`PatientRetry` instances) these are **not** intended to be shared between multiple threads or re-used.

### Sleep

The `Sleep` interface is a functional interface with the defined abstract method
`sleepFor(long millis, int nanos) throws InterruptedException`. The purpose of the interface is to define how the
patient object will wait in between execution attempts. The default implementation (used if nothing else is specified)
is simply the `Thread::sleep` method. You can supply a custom implementation and add it via the `PatientWait` or
`PatientRetry` builders.

```java
builder.withSleep(Sleep)
```

### PatientExecutable

A block of code that returns a value and can throw a `Throwable`. This is similar to the Java `Callable` type, but it
can throw checked exceptions and errors.

### DelaySupplierFactory

The `DelaySupplierFactory` interface is an interface with the defined abstract method `Supplier<Duration> create()`.
While `PatientWaitFuture` and `PatientRetryFuture` are immutable, they may need to contain state during the course
of the waiting. An example where this is necessary if the wait uses an exponentially increasing duration between failed
attempts. So the `Supplier<Duration>` for the future would have the first duration wait some amount of time, the next
duration from it would have the exponential increase from the first, etc. The `DelaySupplierFactory` would, each time
`create()` is called, return a `Supplier<Duration>` that has that same initial first duration and increase for each
subsequent duration. There are some default implementations available via the static `DelaySuppliers` class.

### PatientExecutionHandler

The PatientExecutionHandler interface is a functional interface with the defined abstract method
`T execute(PatientExecutable<T>, Predicate<T>)`. The purpose of the handler is to "wrap" every invocation of the
executable that is performed for each attempt. This allows you to customize what happens if the execution throws a
specific type of exception or to report certain results, etc. The default for the builder (if none is set) is an
execution handler that will either return a pass or fail for the execution and will, upon any `Throwable` being thrown
from the `PatientExecutable`, throw a `PatientExecutionException` with the caught throwable set as the cause. There are
some other default implementations available via the static `PatientExecutionHandlers` class.

## Basic Wait & Retry Logic Flow

The general flow of using the `PatientWait` and `PatientRetry` objects is as follows:

* Use a builder to create a factory instance.
* Repeatedly call that factory instance to create future instances whenever you need to wait for a condition.
* Call the future instance to wait for a condition and then let the reference to the future instance fall out of scope
 and be garbage collected.

Overall flow of a individual waiting attempt is as follows:

* Check if there is an initial delay set, if so sleep for that amount of time.
* Retrieve a supplier of Duration objects from the DelaySupplierFactory for this waiting attempt.
* Execute the given PatientExecutable and check if it was a valid result based on the given predicate.
  * The result is valid
    * Return the result and break out.
  * The result is invalid
    * Save a description of the result (e.g. maybe a stack trace message or a string representation of the invalid result)
    * Check the break out condition
      * The waiting can continue
        * Retrieve a Duration from the supplier and sleep for that amount of time.
      * The waiting can't continue
        * Throw an instance of an `AbstractRepeatedAttemptsException` that contains a list of the unsuccessful attempts.

Note that the breakout condition is different for the `PatientWaitFuture` and the `PatientRetryFuture` types. A
`PatientRetryFuture` type will keep attempting until either a successful result is found or the maximum number of
attempts have been reached. The breakout condition for a `PatientWaitFuture` is a little more complicated. When the
waiting attempt begins, an `Instant` is grabbed for the current time. It will then calculate an "end time" based on that
start time and the given timeout. After each unsuccessful attempt when checking the break condition, it will grab a
new `Instant` timestamp and the next `Duration` from the delay supplier. If the current `Duration` from the start time
plus the next wait duration is less than the timeout, then it will sleep for the next delay `Duration` and another
attempt will be made. Also, please note that the default `filter(Predicate<T>)` is one that returns true for any object
that isn't `null` and isn't a `Boolean` that evaluates to `false`.

## Code Examples

### Building a PatientWait

Example of building a `PatientWait` (note that in some cases the default is used for a value which isn't necessary
but it does illustrate the available methods):

```java
PatientWait.builder()
           // Not needed as this is the default ... allows you to set the Sleep implementation
           .withSleep(Thread::sleep)
           // Gives an initial amount of time to sleep if desired
           // This is useful if the types of waiting you are doing consistently will never be ready
           // before say 5 seconds and the code that is being executed is resource intensive so it
           // doesn't make sense to start trying to execute the code yet (e.g. maybe it's a network API call).
           .withInitialDelay(Duration.ZERO)
           // Set some default timeout that will be used on the future instance if no custom timeout is given
           // Note that a value of ZERO means that the code will be only attempted once
           .withDefaultTimeout(Duration.ZERO)
           // A fixed delay supplier means that each sleep in between attempts will be for the given duration
           .withDelaySupplier(DelaySuppliers.fixed(Duration.ofMillis(500)))
           // The ignoring execution handler will catch any throwable thrown by the code and, if it's a
           // Runtime exception in this case will just add that as information to the unsuccessful attempts.
           // If the executable throws a non-runtimeException throwable, however, it will cause a
           // PatientExecutionException with that throwable set as the cause. 
           .withExecutionHandler(PatientExecutionHandlers.ignoring(RuntimeException.class))
           .build();
```

### Waiting for a successful status code

For an example of using a `PatientWaitFuture`, let's say there is a method you are calling that makes a network call
to an endpoint. The method you are calling will return the http status code as an integer. Let's also say you need to
keep repeating this call until you get a result that isn't in the range of 500 (but any other status code is ok).
Let's also assume you want to keep trying to make the API call, waiting 500 milliseconds between each attempt, until
either a non-5xx status code is returned, or 2 minutes have passed at which point you want to throw an exception. 
Finally, you already have a `PatientWait` instance built earlier saved to a variable named `wait`.

```java
int statusCode = wait.from(() -> Example::makeApiCall)
                     .withMessage("Never received a non-5xx status code from Example::makeApiCall")
                     // Only fail if the status code is in the range of 5xx
                     .withFilter(statusCode -> 500 > statusCode || 599 < statusCode)
                     .get(Duration.ofMinutes(2));
```

### Check vs Get

Sometimes, though, you don't need to retrieve the value from a call, but just need to know if it completed successfully
within the given number of attempts (or within the given timeout). Again note for the timeout case, this isn't intended
to be a hard timeout so you **could** have a case where you get a successful value from the above example code that
doesn't throw an exception but the entire waiting time took `2.5` minutes (if you need a stronger guarantee, then the
`Patience` library isn't what you are looking for). In those cases, instead of calling `get(Duration.ofMinutes(2))` in
the above example you can use the `check(Duration)` method instead (or `check(int)` for a `PatientRetryFuture` object).
In that case it will keep trying until it finds a successful result and then returns `true`. If no valid result and the
break condition is reached then `false` will be returned instead and no `PatientTimeoutException` or
`PatientRetryException` will be thrown. Note that a `PatientExecutionException` **can** still be thrown if there is an
unexpected error thrown from the `PatientExecutable`.

```java
PatientRetry retry = PatientRetry.builder()
                                 .withInitialDelay(Duration.ofSeconds(1))
                                 .withDefaultNumberOfRetries(10)
                                 .withExecutionHandler(PatientExecutionHandlers.simple())
                                 .withDelaySupplier(DelaySupplier.fixed(Duration.ofMillis(500)))
                                 .build();
boolean resultFound = retry.from(Math::random)
                           .withFilter(dbl -> dbl > 0.5)
                           .check(5); // This is the number of retries, not attempts.
                                      // This is to keep consistent with PatientWaitFuture in which a Duration.ZERO
                                      // means to execute exactly once.
```

Following the `check` call the code will perform the following steps:

* The executing thread will sleep for 1 second (the initial delay).
* The executable `Math.random()` will be called and the return a result
* The result from the random call above will be checked with the predicate to see if the generated value is greater than
`0.5`.
  * If the value is greater than `0.5` then this was a successful attempt.
    * Return true since `check` was used. If `get` were used instead the actual generated value would be returned.
  * If the value is less than or equal to `0.5` then this was an unsuccessful attempt.
    * Check the current attempt number.
      * If less than `6` attempts have been made we will need to repeat the process above (the value to `check` is the
        number of retries, so `# attempts == # retries + 1`).
      * If `6` attempts have been made, then return `false`.

Note that if `check` is used it won't ever throw a `PatientTimeoutException` or `PatientRetryException` so any
`withMessage(String)` on the `PatientWaitFuture` or `PatientRetryFuture` objects are useless.
