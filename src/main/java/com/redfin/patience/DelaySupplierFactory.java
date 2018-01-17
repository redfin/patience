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

import java.time.Duration;
import java.util.function.Supplier;

/**
 * A DelaySupplierFactory is like a {@link Supplier} of Suppliers of {@link Duration}s.
 * Unlike a normal Supplier, though, it is guaranteed that each call to {@link #create}
 * returns a unique instance if the returned Supplier contains mutable state. It is
 * intended that each Patient attempt to get an eventual result calls this once to get
 * the Supplier of Durations to use between unsuccessful attempts.
 */
public interface DelaySupplierFactory {

    /**
     * @return a new {@link Supplier} of Durations for each call. It is guaranteed
     * that each call to get returns a new {@link Supplier} instance.
     */
    Supplier<Duration> create();
}
