package com.firebase.geofire;

import android.support.annotation.NonNull;

import com.firebase.geofire.core.GeoHash;
import com.firebase.geofire.core.GeoHashQuery;
import com.firebase.geofire.util.GeoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Database-agnostic utilities for creating and querying GeoHashes.
 */
public class GeoFireUtils {

    /**
     * Converts a lat/lng location into a GeoHash with default precision (10).
     */
    @NonNull
    public static String getGeoHashForLocation(@NonNull GeoLocation location) {
        return getGeoHashForLocation(location, 10);
    }

    /**
     * Converts a lat/lng location into a GeoHash with specified precision.
     *
     * @param location  the location to convert.
     * @param precision the precision between 1 and 22 (10 is default).
     * @return the GeoHash string.
     */
    @NonNull
    public static String getGeoHashForLocation(@NonNull GeoLocation location, int precision) {
        return new GeoHash(location.latitude, location.longitude, precision).getGeoHashString();
    }

    /**
     * Calculates the distance between two locations in meters.
     *
     * @param a the first location.
     * @param b the second location.
     * @return the distance between the two locations, in meters.
     */
    public static double getDistanceBetween(@NonNull GeoLocation a, @NonNull GeoLocation b) {
        return GeoUtils.distance(a, b);
    }

    /**
     * Determines the starting and ending geohashes to use as bounds for a database query.
     *
     * @param location the center of the query.
     * @param radius   the radius of the query, in meters. The maximum radius that is
     *                 supported is about 8587km.
     * @return a list of query bounds containing between 1 and 9 queries.
     */
    @NonNull
    public static List<GeoQueryBounds> getGeoHashQueryBounds(@NonNull GeoLocation location, double radius) {
        List<GeoQueryBounds> result = new ArrayList<>();
        Set<GeoHashQuery> queries = GeoHashQuery.queriesAtLocation(location, radius);
        for (GeoHashQuery q : queries) {
            result.add(new GeoQueryBounds(q.getStartValue(), q.getEndValue()));
        }
        return result;
    }
}
