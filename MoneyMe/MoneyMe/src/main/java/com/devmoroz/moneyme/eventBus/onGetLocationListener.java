package com.devmoroz.moneyme.eventBus;



import com.google.android.gms.location.places.Place;

public interface onGetLocationListener {
    void onCurrentLocationRetrieved(Place place);
}
