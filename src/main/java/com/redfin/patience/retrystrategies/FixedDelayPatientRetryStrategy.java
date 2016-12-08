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

package com.redfin.patience.retrystrategies;

import com.redfin.validity.Validity;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * A concrete implementation of the {@link AbstractDelayPatientRetryStrategy} that simply
 * returns the given {@link Duration} for each call to the duration supplier so that
 * each attempt is evenly spaced out.
 */
public class FixedDelayPatientRetryStrategy extends AbstractDelayPatientRetryStrategy {

    private final Duration delay;

    /**
     * Create a new instance of the {@link FixedDelayPatientRetryStrategy} with
     * the given delay {@link Duration}.
     *
     * @param delay the {@link Duration} to wait between attempts at
     *              extracting a value from the supplier in the execute method.
     *              May not be null or negative.
     *              A value of zero simply means there will be no waiting between
     *              attempts.
     *
     * @throws IllegalArgumentException if delayBetween is null or negative.
     */
    public FixedDelayPatientRetryStrategy(Duration delay) {
        this.delay = Validity.require().that(delay).isGreaterThanOrEqualTo(Duration.ZERO);
    }

    @Override
    protected Supplier<Duration> getDelayDurations() {
        return () -> delay;
    }
}
