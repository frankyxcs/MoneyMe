package com.devmoroz.moneyme.eventBus;

import android.location.Location;


public interface OnGeoUtilResultListener {
    public void onAddressResolved(String address);

    public void onCoordinatesResolved(Location location, String address);

    public void onLocationRetrieved(Location location);

    public void onLocationUnavailable();
}
