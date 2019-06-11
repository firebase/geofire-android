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

import com.firebase.geofire.core.GeoHashQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GeoHashQueryUtilsTest {

    @Test
    public void boundingBoxBits() {
        Assert.assertEquals(28, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(35, 0), 1000));
        Assert.assertEquals(27, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(35.645, 0), 1000));
        Assert.assertEquals(27, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(36, 0), 1000));
        Assert.assertEquals(28, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(0, 0), 1000));
        Assert.assertEquals(28, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(0, -180), 1000));
        Assert.assertEquals(28, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(0, 180), 1000));
        Assert.assertEquals(22, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(0, 0), 8000));
        Assert.assertEquals(27, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(45, 0), 1000));
        Assert.assertEquals(25, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(75, 0), 1000));
        Assert.assertEquals(23, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(75, 0), 2000));
        Assert.assertEquals(1, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(90, 0), 1000));
        Assert.assertEquals(1, GeoHashQuery.Utils.bitsForBoundingBox(new GeoLocation(90, 0), 2000));
    }
}
