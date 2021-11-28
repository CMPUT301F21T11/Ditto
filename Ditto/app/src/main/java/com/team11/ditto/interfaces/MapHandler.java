package com.team11.ditto.interfaces;

import com.google.android.gms.maps.model.LatLng;

import javax.annotation.Nullable;

public interface MapHandler {

    void handleLocationChange(@Nullable LatLng location);
}
