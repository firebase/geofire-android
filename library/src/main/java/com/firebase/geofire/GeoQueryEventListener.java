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
 * GeoQuery notifies listeners with this interface about keys that entered, exited, or moved within the query.
 */
public interface GeoQueryEventListener {

    /**
     * Called if a key entered the search area of the GeoQuery. This method is called for every key currently in the
     * search area at the time of adding the listener.
     *
     * This method is once per key, and is only called again if onKeyExited was called in the meantime.
     *
     * @param key The key that entered the search area
     * @param location The location for this key as a GeoLocation object
     */
    void onKeyEntered(String key, GeoLocation location);

    /**
     * Called if a key exited the search area of the GeoQuery. This is method is only called if onKeyEntered was called
     * for the key.
     *
     * @param key The key that exited the search area
     */
    void onKeyExited(String key);

    /**
     * Called if a key moved within the search area.
     *
     * This method can be called multiple times.
     *
     * @param key The key that moved within the search area
     * @param location The location for this key as a GeoLocation object
     */
    void onKeyMoved(String key, GeoLocation location);

    /**
     * Called once all initial GeoFire data has been loaded and the relevant events have been fired for this query.
     * Every time the query criteria is updated, this observer will be called after the updated query has fired the
     * appropriate key entered or key exited events.
     */
    void onGeoQueryReady();

    /**
     * Called in case an error occurred while retrieving locations for a query, e.g. violating security rules.
     * @param error The error that occurred while retrieving the query
     */
    void onGeoQueryError(DatabaseError error);

}
