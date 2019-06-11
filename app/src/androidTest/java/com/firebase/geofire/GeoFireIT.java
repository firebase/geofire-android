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
package com.firebase.geofire;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.firebase.geofire.example.MainActivity;
import com.firebase.geofire.testing.GeoFireTestingRule;
import com.firebase.geofire.testing.SimpleFuture;
import com.firebase.geofire.testing.TestCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class GeoFireIT {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    private final GeoFireTestingRule geoFireTestingRule = new GeoFireTestingRule();

    @Before
    public void before() throws Exception {
        geoFireTestingRule.before(mActivityRule.getActivity());
    }

    @After
    public void after() {
        geoFireTestingRule.after();
    }

    @Test
    public void geoFireSetsLocations() throws InterruptedException, TimeoutException {
        GeoFire geoFire = geoFireTestingRule.newTestGeoFire();
        geoFireTestingRule.setLocation(geoFire, "loc1", 0.1, 0.1);
        geoFireTestingRule.setLocation(geoFire, "loc2", 50.1, 50.1);
        geoFireTestingRule.setLocation(geoFire, "loc3", -89.1, -89.1, true);

        final SimpleFuture<Object> future = new SimpleFuture<>();
        geoFire.getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                future.put(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.put(databaseError);
            }
        });

        Map<String, Object> expected = new HashMap<>();
        expected.put("loc1", new HashMap<String, Object>() {{
            put("l", Arrays.asList(0.1, 0.1));
            put("g", "s000d60yd1");
        }});
        expected.put("loc2", new HashMap<String, Object>() {{
            put("l", Arrays.asList(50.1, 50.1));
            put("g", "v0gth03tws");
        }});
        expected.put("loc3", new HashMap<String, Object>() {{
            put("l", Arrays.asList(-89.1, -89.1));
            put("g", "400th7z6gs");
        }});
        Object result = future.get(geoFireTestingRule.timeout, TimeUnit.SECONDS);
        Assert.assertEquals(expected, ((DataSnapshot)result).getValue());
    }

    @Test
    public void getLocationReturnsCorrectLocation() throws InterruptedException, TimeoutException {
        GeoFire geoFire = geoFireTestingRule.newTestGeoFire();

        TestCallback testCallback1 = new TestCallback();
        geoFire.getLocation("loc1", testCallback1);
        Assert.assertEquals(TestCallback.noLocation("loc1"), testCallback1.getCallbackValue());

        TestCallback testCallback2 = new TestCallback();
        geoFireTestingRule.setLocation(geoFire, "loc1", 0, 0, true);
        geoFire.getLocation("loc1", testCallback2);
        Assert.assertEquals(TestCallback.location("loc1", 0, 0), testCallback2.getCallbackValue());

        TestCallback testCallback3 = new TestCallback();
        geoFireTestingRule.setLocation(geoFire, "loc2", 1, 1, true);
        geoFire.getLocation("loc2", testCallback3);
        Assert.assertEquals(TestCallback.location("loc2", 1, 1), testCallback3.getCallbackValue());

        TestCallback testCallback4 = new TestCallback();
        geoFireTestingRule.setLocation(geoFire, "loc1", 5, 5, true);
        geoFire.getLocation("loc1", testCallback4);
        Assert.assertEquals(TestCallback.location("loc1", 5, 5), testCallback4.getCallbackValue());

        TestCallback testCallback5 = new TestCallback();
        geoFireTestingRule.removeLocation(geoFire, "loc1");
        geoFire.getLocation("loc1", testCallback5);
        Assert.assertEquals(TestCallback.noLocation("loc1"), testCallback5.getCallbackValue());
    }

    @Test
    public void getLocationOnWrongDataReturnsError() throws InterruptedException {
        GeoFire geoFire = geoFireTestingRule.newTestGeoFire();
        geoFireTestingRule.setValueAndWait(geoFire.getDatabaseRefForKey("loc1"), "NaN");

        final Semaphore semaphore = new Semaphore(0);
        geoFire.getLocation("loc1", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                Assert.fail("This should not be a valid location!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                semaphore.release();
            }
        });
        semaphore.tryAcquire(geoFireTestingRule.timeout, TimeUnit.SECONDS);

        geoFireTestingRule.setValueAndWait(geoFire.getDatabaseRefForKey("loc2"), new HashMap<String, Object>() {{
           put("l", 10);
           put("g", "abc");
        }});

        geoFire.getLocation("loc2", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                Assert.fail("This should not be a valid location!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                semaphore.release();
            }
        });
        semaphore.tryAcquire(geoFireTestingRule.timeout, TimeUnit.SECONDS);
    }

    @Test
    public void invalidCoordinatesThrowException() {
        GeoFire geoFire = geoFireTestingRule.newTestGeoFire();
        try {
            geoFire.setLocation("test", new GeoLocation(-91, 90));
            Assert.fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }

        try {
            geoFire.setLocation("test", new GeoLocation(0, -180.1));
            Assert.fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }

        try {
            geoFire.setLocation("test", new GeoLocation(0, 181.1));
            Assert.fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void locationWorksWithLongs() throws InterruptedException, TimeoutException {
        GeoFire geoFire = geoFireTestingRule.newTestGeoFire();
        DatabaseReference databaseReference = geoFire.getDatabaseRefForKey("loc");

        final Semaphore semaphore = new Semaphore(0);
        databaseReference.setValue(new HashMap<String, Object>() {{
            put("l", Arrays.asList(1L, 2L));
            put("g", "7zzzzzzzzz"); // this is wrong but we don't care in this test
        }}, "7zzzzzzzzz", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                semaphore.release();
            }
        });
        semaphore.tryAcquire(geoFireTestingRule.timeout, TimeUnit.SECONDS);

        TestCallback testCallback = new TestCallback();
        geoFire.getLocation("loc", testCallback);
        Assert.assertEquals(TestCallback.location("loc", 1, 2), testCallback.getCallbackValue());
    }
}
