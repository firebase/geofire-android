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
package com.firebase.geofire.core;

import android.support.annotation.NonNull;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.util.Base32Utils;

import static java.util.Locale.US;

public class GeoHash {
    private final String geoHash;

    // The default precision of a geohash
    private static final int DEFAULT_PRECISION = 10;

    // The maximal precision of a geohash
    public static final int MAX_PRECISION = 22;

    // The maximal number of bits precision for a geohash
    public static final int MAX_PRECISION_BITS = MAX_PRECISION * Base32Utils.BITS_PER_BASE32_CHAR;

    /**
     * Convert a GeoHash string back into a GeoLocation.
     *
     * See: https://en.wikipedia.org/wiki/Geohash#Algorithm_and_example
     */
    @NonNull
    public static GeoLocation locationFromHash(@NonNull String hashString) {
        long decoded = 0;
        long numBits = hashString.length() * Base32Utils.BITS_PER_BASE32_CHAR;

        for (int i = 0; i < hashString.length(); i++) {
            int charVal = Base32Utils.base32CharToValue(hashString.charAt(i));
            decoded = decoded << Base32Utils.BITS_PER_BASE32_CHAR;
            decoded = decoded + charVal;
        }

        double minLng = -180;
        double maxLng = 180;

        double minLat = -90;
        double maxLat = 90;

        for (int i = 0; i < numBits; i++) {
            // Get the high bit
            long bit = (decoded >> (numBits - i - 1)) & 1;

            // Even bits are longitude, odd bits are latitude
            if (i % 2 == 0) {
                if (bit == 1) {
                    minLng = (minLng + maxLng) / 2;
                } else {
                    maxLng = (minLng + maxLng) / 2;
                }
            } else {
                if (bit == 1) {
                    minLat = (minLat + maxLat) / 2;
                } else {
                    maxLat = (minLat + maxLat) / 2;
                }
            }
        }

        double lat = (minLat + maxLat) / 2;
        double lng = (minLng + maxLng) / 2;

        return new GeoLocation(lat, lng);
    }

    public GeoHash(double latitude, double longitude) {
        this(latitude, longitude, DEFAULT_PRECISION);
    }

    public GeoHash(GeoLocation location) {
        this(location.latitude, location.longitude, DEFAULT_PRECISION);
    }

    public GeoHash(double latitude, double longitude, int precision) {
        if (precision < 1) {
            throw new IllegalArgumentException("Precision of GeoHash must be larger than zero!");
        }
        if (precision > MAX_PRECISION) {
            throw new IllegalArgumentException("Precision of a GeoHash must be less than " + (MAX_PRECISION + 1) + "!");
        }
        if (!GeoLocation.coordinatesValid(latitude, longitude)) {
            throw new IllegalArgumentException(String.format(US, "Not valid location coordinates: [%f, %f]", latitude, longitude));
        }
        double[] longitudeRange = { -180, 180 };
        double[] latitudeRange = { -90, 90 };

        char[] buffer = new char[precision];

        for (int i = 0; i < precision; i++) {
            int hashValue = 0;
            for (int j = 0; j < Base32Utils.BITS_PER_BASE32_CHAR; j++) {
                boolean even = (((i*Base32Utils.BITS_PER_BASE32_CHAR) + j) % 2) == 0;
                double val = even ? longitude : latitude;
                double[] range = even ? longitudeRange : latitudeRange;
                double mid = (range[0] + range[1])/2;
                if (val > mid) {
                    hashValue = (hashValue << 1) + 1;
                    range[0] = mid;
                } else {
                    hashValue = hashValue << 1;
                    range[1] = mid;
                }
            }
            buffer[i] = Base32Utils.valueToBase32Char(hashValue);
        }
        this.geoHash = new String(buffer);
    }

    public GeoHash(String hash) {
        if (hash.length() == 0 || !Base32Utils.isValidBase32String(hash)) {
            throw new IllegalArgumentException("Not a valid geoHash: " + hash);
        }
        this.geoHash = hash;
    }

    public String getGeoHashString() {
        return this.geoHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoHash other = (GeoHash) o;

        return this.geoHash.equals(other.geoHash);
    }

    @Override
    public String toString() {
        return "GeoHash{" +
                "geoHash='" + geoHash + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return this.geoHash.hashCode();
    }
}
