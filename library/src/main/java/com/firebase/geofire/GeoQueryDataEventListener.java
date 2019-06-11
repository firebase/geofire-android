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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * GeoQuery notifies listeners with this interface about dataSnapshots that entered, exited, or moved within the query.
 */
public interface GeoQueryDataEventListener {

    /**
     * Called if a dataSnapshot entered the search area of the GeoQuery. This method is called for every dataSnapshot currently in the
     * search area at the time of adding the listener.
     *
     * This method is once per datasnapshot, and is only called again if onDataExited was called in the meantime.
     *
     * @param dataSnapshot The associated dataSnapshot that entered the search area
     * @param location The location for this dataSnapshot as a GeoLocation object
     */
    void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location);

    /**
     * Called if a datasnapshot exited the search area of the GeoQuery. This is method is only called if onDataEntered was called
     * for the datasnapshot.
     *
     * @param dataSnapshot The associated dataSnapshot that exited the search area
     */
    void onDataExited(DataSnapshot dataSnapshot);

    /**
     * Called if a dataSnapshot moved within the search area.
     *
     * This method can be called multiple times.
     *
     * @param dataSnapshot The associated dataSnapshot that moved within the search area
     * @param location The location for this dataSnapshot as a GeoLocation object
     */
    void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location);

    /**
     * Called if a dataSnapshot changed within the search area.
     *
     * An onDataMoved() is always followed by onDataChanged() but it is be possible to see
     * onDataChanged() without an preceding onDataMoved().
     *
     * This method can be called multiple times for a single location change, due to the way
     * the Realtime Database handles floating point numbers.
     *
     * Note: this method is not related to ValueEventListener#onDataChange(DataSnapshot).
     *
     * @param dataSnapshot The associated dataSnapshot that moved within the search area
     * @param location The location for this dataSnapshot as a GeoLocation object
     */
    void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location);

    /**
     * Called once all initial GeoFire data has been loaded and the relevant events have been fired for this query.
     * Every time the query criteria is updated, this observer will be called after the updated query has fired the
     * appropriate dataSnapshot entered or dataSnapshot exited events.
     */
    void onGeoQueryReady();

    /**
     * Called in case an error occurred while retrieving locations for a query, e.g. violating security rules.
     * @param error The error that occurred while retrieving the query
     */
    void onGeoQueryError(DatabaseError error);

}
