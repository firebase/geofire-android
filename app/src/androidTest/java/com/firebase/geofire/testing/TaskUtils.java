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

import com.google.android.gms.tasks.Task;

import org.junit.Assert;

import java.util.Date;

public class TaskUtils {

    private static int DEFAULT_TIMEOUT_MS = 10000;

    public static <T> Task<T> waitForTask(final Task<T> task) throws Exception {
        final Date startTime = new Date();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!task.isComplete()) {
                    Date now = new Date();
                    if (now.getTime() - startTime.getTime() > DEFAULT_TIMEOUT_MS) {
                        Assert.fail("Timed out waiting for task!");
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Assert.fail("Error waiting for task: " + e.getLocalizedMessage());
                    }
                }
            }
        });

        thread.run();
        thread.join();

        return task;
    }

}
