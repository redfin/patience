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

/**
 * An {@link Executable} represents a block of code that can be executed and which
 * will return a value. It can throw any type of Throwable without handling it, even
 * checked exceptions.
 *
 * @param <T> the type to be returned from the execute method.
 */
@FunctionalInterface
public interface Executable<T> {

    /**
     * Actually execute some code. The code should either return a value
     * or throw some sort of Throwable instance.
     *
     * @return a value.
     *
     * @throws Throwable possibly.
     */
    T execute() throws Throwable;
}
