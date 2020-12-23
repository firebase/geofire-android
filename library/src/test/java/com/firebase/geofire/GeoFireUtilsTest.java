package com.firebase.geofire;

import com.firebase.geofire.core.GeoHash;
import com.firebase.geofire.util.GeoUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class GeoFireUtilsTest {

    private static final GeoLocation SAN_FRANCISCO = new GeoLocation(37.7749, -122.4194);
    private static final GeoLocation NEW_YORK_CITY = new GeoLocation(40.7128, -74.0060);

    @Test
    public void testGetGeoHashForLocation() {
        Assert.assertEquals(GeoFireUtils.getGeoHashForLocation(SAN_FRANCISCO), "9q8yyk8ytp");
        Assert.assertEquals(GeoFireUtils.getGeoHashForLocation(NEW_YORK_CITY), "dr5regw3pp");
    }

    @Test
    public void testGetDistanceBetween() {
        Assert.assertEquals(Math.floor(GeoFireUtils.getDistanceBetween(SAN_FRANCISCO, NEW_YORK_CITY)), 4127138.0);
    }

    @Test
    public void testGetGeoHashQueryBounds() {
        double radiusInM = 50000;
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(SAN_FRANCISCO, radiusInM);

        for (GeoQueryBounds b : bounds) {
            String startHash = b.startHash.replace("~", "");
            GeoLocation startLoc = GeoHash.locationFromHash(startHash);

            String endHash = b.startHash.replace("~", "");
            GeoLocation endLoc = GeoHash.locationFromHash(endHash);

            Assert.assertTrue(GeoFireUtils.getDistanceBetween(SAN_FRANCISCO, startLoc) >= radiusInM / 4);
            Assert.assertTrue(GeoFireUtils.getDistanceBetween(SAN_FRANCISCO, startLoc) <= radiusInM * 4);

            Assert.assertTrue(GeoFireUtils.getDistanceBetween(SAN_FRANCISCO, endLoc) >= radiusInM / 4);
            Assert.assertTrue(GeoFireUtils.getDistanceBetween(SAN_FRANCISCO, endLoc) <= radiusInM * 4);
        }

    }

}
