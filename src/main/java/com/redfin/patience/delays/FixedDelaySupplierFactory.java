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

package com.redfin.patience.delays;

import com.redfin.patience.DelaySupplierFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

/**
 * An implementation of {@link DelaySupplierFactory} that creates a
 * {@link Supplier} of {@link Duration}s that return a fixed
 * {@link Duration} for each call to {@link Supplier#get()}.
 */
public final class FixedDelaySupplierFactory
        implements DelaySupplierFactory {

    private final Duration duration;

    /**
     * Create a new {@link FixedDelaySupplierFactory} instance with the given
     * duration to wait between execution attempts. A duration of zero means that
     * no time should be taken between attempts.
     *
     * @param delayDuration the {@link Duration} to wait between execution attempts.
     *                      May not be null or negative.
     *
     * @throws IllegalArgumentException if delayDuration is null or negative.
     */
    public FixedDelaySupplierFactory(Duration delayDuration) {
        this.duration = validate().withMessage("Cannot use a null or negative delayDuration.")
                                  .that(delayDuration)
                                  .isGreaterThanOrEqualToZero();
    }

    @Override
    public Supplier<Duration> create() {
        return () -> duration;
    }
}
