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
