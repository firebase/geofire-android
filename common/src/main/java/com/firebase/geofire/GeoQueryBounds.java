package com.firebase.geofire;

/**
 * Start/end bounds for a query on a GeoHash field.
 */
public class GeoQueryBounds {

    public final String startHash;
    public final String endHash;

    protected GeoQueryBounds(String startHash, String endHash) {
        this.startHash = startHash;
        this.endHash = endHash;
    }

}
