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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This is a JUnit rule that can be used for hooking up Geofire with a real database instance.
 */
public final class GeoFireTestingRule {

    private static final String TAG = "GeoFireTestingRule";

    // TODO: Get this to work with the emulators
    //    private static final String DEFAULT_DATABASE_URL = "http://10.0.2.2:9000";
    private static final String DEFAULT_DATABASE_URL = "https://geofiretest-8d811.firebaseio.com/";

    static final long DEFAULT_TIMEOUT_SECONDS = 5;

    private static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    private DatabaseReference databaseReference;

    public final String databaseUrl;

    /** Timeout in seconds. */
    public final long timeout;

    public GeoFireTestingRule() {
        this (DEFAULT_DATABASE_URL, DEFAULT_TIMEOUT_SECONDS);
    }

    public GeoFireTestingRule(final String databaseUrl) {
        this(databaseUrl, DEFAULT_TIMEOUT_SECONDS);
    }

    public GeoFireTestingRule(final String databaseUrl, final long timeout) {
        this.databaseUrl = databaseUrl;
        this.timeout = timeout;
    }

    public void before(Context context) throws Exception {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setApplicationId("1:1010498001935:android:f17a2f247ad8e8bc")
                    .setApiKey("AIzaSyBys-YxxE7kON5PxZc5aY6JwVvreyx_owc")
                    .setDatabaseUrl(databaseUrl)
                    .build();
            FirebaseApp.initializeApp(context, firebaseOptions);
            FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Task<AuthResult> signInTask = TaskUtils.waitForTask(FirebaseAuth.getInstance().signInAnonymously());
            if (signInTask.isSuccessful()) {
                Log.d(TAG, "Signed in as " + signInTask.getResult().getUser());
            } else {
                throw new Exception("Failed to sign in: " + signInTask.getException());
            }
        }

        this.databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(databaseUrl);
    }

    public void after() {
        this.databaseReference.setValue(null);
        this.databaseReference = null;
    }

    /** This will return you a new Geofire instance that can be used for testing. */
    public GeoFire newTestGeoFire() {
        return new GeoFire(databaseReference.child(randomAlphaNumericString(16)));
    }

    /**
     * Sets a given location key from the latitude and longitude on the provided Geofire instance.
     * This operation will run asychronously.
     */
    public void setLocation(GeoFire geoFire, String key, double latitude, double longitude) {
        setLocation(geoFire, key, latitude, longitude, false);
    }

    /**
     * Removes a location on the provided Geofire instance.
     * This operation will run asychronously.
     */
    public void removeLocation(GeoFire geoFire, String key) {
        removeLocation(geoFire, key, false);
    }

    /** Sets the value on the given databaseReference and waits until the operation has successfully finished. */
    public void setValueAndWait(DatabaseReference databaseReference, Object value) {
        final SimpleFuture<DatabaseError> futureError = new SimpleFuture<DatabaseError>();
        databaseReference.setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                futureError.put(databaseError);
            }
        });
        try {
            Assert.assertNull(futureError.get(timeout, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            Assert.fail("Timeout occured!");
        }
    }

    /**
     * Sets a given location key from the latitude and longitude on the provided Geofire instance.
     * This operation will run asychronously or synchronously depending on the wait boolean.
     */
    public void setLocation(GeoFire geoFire, String key, double latitude, double longitude, boolean wait) {
        final SimpleFuture<DatabaseError> futureError = new SimpleFuture<DatabaseError>();
        geoFire.setLocation(key, new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                futureError.put(error);
            }
        });
        if (wait) {
            try {
                Assert.assertNull(futureError.get(timeout, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                Assert.fail("Timeout occured!");
            }
        }
    }

    /**
     * Removes a location on the provided Geofire instance.
     * This operation will run asychronously or synchronously depending on the wait boolean.
     */
    public void removeLocation(GeoFire geoFire, String key, boolean wait) {
        final SimpleFuture<DatabaseError> futureError = new SimpleFuture<DatabaseError>();
        geoFire.removeLocation(key, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                futureError.put(error);
            }
        });
        if (wait) {
            try {
                Assert.assertNull(futureError.get(timeout, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                Assert.fail("Timeout occured!");
            }
        }
    }

    /** This lets you blockingly wait until the onGeoFireReady was fired on the provided Geofire instance. */
    public void waitForGeoFireReady(GeoFire geoFire) throws InterruptedException {
        final Semaphore semaphore = new Semaphore(0);
        geoFire.getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                semaphore.release();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Assert.fail("Firebase error: " + databaseError);
            }
        });

        Assert.assertTrue("Timeout occured!", semaphore.tryAcquire(timeout, TimeUnit.SECONDS));
    }

    private static String randomAlphaNumericString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++ ) {
            sb.append(ALPHA_NUM_CHARS.charAt(random.nextInt(ALPHA_NUM_CHARS.length())));
        }
        return sb.toString();
    }
}
