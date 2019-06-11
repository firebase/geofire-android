/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.firebase.geofire.testing;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

abstract class TestListener {
    private final List<String> events = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    void addEvent(String event) {
        lock.lock();

        try {
            events.add(event);
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * This allows you to assert that certain events were retrieved.
     * You can use the static factory methods on {@link GeoQueryDataEventTestListener} or
     * {@link GeoQueryEventTestListener} to retrieve the strings.
     */
    public void expectEvents(Collection<String> events) throws InterruptedException {
        boolean stillWaiting = true;
        lock.lock();

        try {
            while (!contentsEqual(this.events, events)) {
                if (!stillWaiting) {
                    Assert.assertEquals(events, new LinkedHashSet<>(this.events));
                    Assert.fail("Timeout occured");
                    return;
                }
                stillWaiting = condition.await(10, TimeUnit.SECONDS);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean contentsEqual(Collection<String> c1, Collection<String> c2) {
        return (new LinkedHashSet<>(c1).equals(new LinkedHashSet<>(c2)));
    }
}
