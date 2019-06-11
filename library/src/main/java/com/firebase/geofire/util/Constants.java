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
package com.firebase.geofire.util;

public final class Constants {

    // Length of a degree latitude at the equator
    public static final double METERS_PER_DEGREE_LATITUDE = 110574;

    // The equatorial circumference of the earth in meters
    public static final double EARTH_MERIDIONAL_CIRCUMFERENCE = 40007860;

    // The equatorial radius of the earth in meters
    public static final double EARTH_EQ_RADIUS = 6378137;

    // The meridional radius of the earth in meters
    public static final double EARTH_POLAR_RADIUS = 6357852.3;

    /* The following value assumes a polar radius of
     * r_p = 6356752.3
     * and an equatorial radius of
     * r_e = 6378137
     * The value is calculated as e2 == (r_e^2 - r_p^2)/(r_e^2)
     * Use exact value to avoid rounding errors
     */
    public static final double EARTH_E2 =  0.00669447819799;

    // Cutoff for floating point calculations
    public static final double EPSILON = 1e-12;

    private Constants() {
        throw new AssertionError("No instances.");
    }
}
