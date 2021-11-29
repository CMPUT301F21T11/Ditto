package com.team11.ditto.interfaces;

import com.google.android.gms.maps.model.LatLng;

import javax.annotation.Nullable;

/**
 * Interface for handling map data
 * @author Matthew Asgari
 */
public interface MapHandler {

    /**
     * Changes the location
     * @param location location value
     */
    void handleLocationChange(@Nullable LatLng location);
}
