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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class GeoLocationTest {

    private static final double EPSILON = 0.0000001;

    @Test
    public void geoLocationHasCorrectValues() {
        assertEquals(new GeoLocation(1, 2).latitude, 1.0, EPSILON);
        assertEquals(new GeoLocation(1, 2).longitude, 2.0, EPSILON);
        assertEquals(new GeoLocation(0.000001, 2).latitude, 0.000001, EPSILON);
        assertEquals(new GeoLocation(0, 0.000001).longitude, 0.000001, EPSILON);
    }

    @Test
    public void invalidCoordinatesThrowException() {
        try {
            new GeoLocation(-90.1, 90);
            fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }

        try {
            new GeoLocation(0, -180.1);
            fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }

        try {
            new GeoLocation(0, 180.1);
            fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }

        try {
            new GeoLocation(Double.NaN, 0);
            fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }

        try {
            new GeoLocation(0, Double.NaN);
            fail("Did not throw illegal argument exception!");
        } catch (IllegalArgumentException expected) {
        }
    }
}
