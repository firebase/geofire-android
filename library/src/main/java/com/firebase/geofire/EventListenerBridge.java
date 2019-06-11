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
final class EventListenerBridge implements GeoQueryDataEventListener {
    private final GeoQueryEventListener listener;

    public EventListenerBridge(final GeoQueryEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDataEntered(final DataSnapshot dataSnapshot, final GeoLocation location) {
        listener.onKeyEntered(dataSnapshot.getKey(), location);
    }

    @Override
    public void onDataExited(final DataSnapshot dataSnapshot) {
        listener.onKeyExited(dataSnapshot.getKey());
    }

    @Override
    public void onDataMoved(final DataSnapshot dataSnapshot, final GeoLocation location) {
        listener.onKeyMoved(dataSnapshot.getKey(), location);
    }

    @Override
    public void onDataChanged(final DataSnapshot dataSnapshot, final GeoLocation location) {
        // No-op.
    }

    @Override
    public void onGeoQueryReady() {
        listener.onGeoQueryReady();
    }

    @Override
    public void onGeoQueryError(final DatabaseError error) {
        listener.onGeoQueryError(error);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventListenerBridge that = (EventListenerBridge) o;
        return listener.equals(that.listener);
    }

    @Override
    public int hashCode() {
        return listener.hashCode();
    }
}
