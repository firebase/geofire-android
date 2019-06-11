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

import com.google.firebase.database.DatabaseError;

/**
 * Classes implementing this interface can be used to receive the locations stored in GeoFire.
 */
public interface LocationCallback {

    /**
     * This method is called with the current location of the key. location will be null if there is no location
     * stored in GeoFire for the key.
     * @param key The key whose location we are getting
     * @param location The location of the key
     */
    void onLocationResult(String key, GeoLocation location);

    /**
     * Called if the callback could not be added due to failure on the server or security rules.
     * @param databaseError The error that occurred
     */
    void onCancelled(DatabaseError databaseError);

}
