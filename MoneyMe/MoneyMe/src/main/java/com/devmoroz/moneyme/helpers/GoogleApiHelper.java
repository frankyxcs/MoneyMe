package com.devmoroz.moneyme.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.devmoroz.moneyme.eventBus.onGetLocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    public GoogleApiHelper(Context context, GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.mContext = context;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(mContext,
                "Соединение прервано: Ошибка " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    public void stop() {
        mGoogleApiClient.disconnect();
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void GetCurrentPlace(onGetLocationListener listener) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(likelyPlaces -> {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    listener.onCurrentLocationRetrieved(placeLikelihood.getPlace());
                }
                likelyPlaces.release();
            });
        }
    }
}
