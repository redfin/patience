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

import com.redfin.patience.delays.ExponentialDelaySupplierFactory;
import com.redfin.patience.delays.FixedDelaySupplierFactory;

import java.time.Duration;

/**
 * A static, non-instantiable, class for obtaining instances of different
 * implementations of the {@link DelaySupplierFactory} interface.
 */
public final class DelaySuppliers {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Instance Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * Make sure that the static class cannot be instantiated
     */

    private DelaySuppliers() {
        throw new AssertionError("No instances for you!");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Static Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param delayDuration the {@link Duration} to be returned for each call
     *                      to {@link java.util.function.Supplier#get} from the
     *                      suppliers returned by {@link DelaySupplierFactory#create}.
     *                      May not be null or negative.
     *
     * @return a new {@link FixedDelaySupplierFactory} with the given
     * delay duration.
     *
     * @throws IllegalArgumentException if delayDuration is null or negative.
     */
    public static DelaySupplierFactory fixed(Duration delayDuration) {
        return new FixedDelaySupplierFactory(delayDuration);
    }

    /**
     * @param base         the int base for the exponential increase of {@link Duration}s returned
     *                     by the given {@link java.util.function.Supplier} returned by the delay
     *                     supplier. A base of 1 is the same as using a {@link FixedDelaySupplierFactory} with
     *                     the given initialDelay as the duration.
     *                     May not be less than 1.
     * @param initialDelay the {@link Duration} to be returned by the first call to
     *                     {@link java.util.function.Supplier#get} to any supplier returned by
     *                     the delay supplier and which increases exponentially depending on the
     *                     base for each subsequent call.
     *                     May not be null, zero, or negative.
     *
     * @return a new {@link ExponentialDelaySupplierFactory} with the given
     * initial duration and base.
     *
     * @throws IllegalArgumentException if base is less than 1 or if
     *                                  delayDuration is null, negative, or zero.
     */
    public static DelaySupplierFactory exponential(int base,
                                                   Duration initialDelay) {
        return new ExponentialDelaySupplierFactory(base, initialDelay);
    }
}
