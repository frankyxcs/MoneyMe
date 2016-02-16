package com.devmoroz.moneyme.helpers;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.eventBus.OnGeoUtilResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;
import io.nlopez.smartlocation.rx.ObservableFactory;
import rx.Observable;
import rx.Subscriber;


public class GeocodeHelper implements LocationListener {


    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDGww6BVqsgVCMBlqUexcVeCK_Apqf4CBw";

    private static GeocodeHelper instance;
    private static LocationManager locationManager;


    private GeocodeHelper() {
        instance = this;
        locationManager = (LocationManager) MoneyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public void onLocationChanged(Location newLocation) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onProviderDisabled(String provider) {
    }


    public static void getLocation(OnGeoUtilResultListener onGeoUtilResultListener) {
        SmartLocation.LocationControl bod = SmartLocation.with(MoneyApplication.getAppContext()).location(new
                LocationGooglePlayServicesWithFallbackProvider(MoneyApplication.getAppContext())).config(LocationParams
                .NAVIGATION);

        Observable<Location> locations = ObservableFactory.from(bod).timeout(30, TimeUnit.SECONDS);
        locations.subscribe(new Subscriber<Location>() {
            @Override
            public void onNext(Location location) {
                onGeoUtilResultListener.onLocationRetrieved(location);
                unsubscribe();
            }


            @Override
            public void onCompleted() {
            }


            @Override
            public void onError(Throwable e) {
                onGeoUtilResultListener.onLocationUnavailable();
                unsubscribe();
            }
        });
    }


    public static void stop() {
        SmartLocation.with(MoneyApplication.getAppContext()).location().stop();
        if (Geocoder.isPresent()) {
            SmartLocation.with(MoneyApplication.getAppContext()).geocoding().stop();
        }
    }


    public static String getAddressFromCoordinates(Context mContext, double latitude,
                                                   double longitude) throws IOException {
        String addressString = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            if (address != null) {
                addressString = address.getThoroughfare() + ", " + address.getLocality();
            }
        }
        return addressString;
    }


    public static void getAddressFromCoordinates(Location location,
                                                 final OnGeoUtilResultListener onGeoUtilResultListener) {
        if (!Geocoder.isPresent()) {
            onGeoUtilResultListener.onAddressResolved("");
        } else {
            SmartLocation.with(MoneyApplication.getAppContext()).geocoding().reverse(location, (location1, list) -> {
                String address = list.size() > 0 ? list.get(0).getAddressLine(0) : null;
                onGeoUtilResultListener.onAddressResolved(address);
            });
        }
    }


    public static double[] getCoordinatesFromAddress(Context mContext, String address)
            throws IOException {
        double[] result = new double[2];
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocationName(address, 1);
        if (addresses.size() > 0) {
            double latitude = addresses.get(0).getLatitude();
            double longitude = addresses.get(0).getLongitude();
            result[0] = latitude;
            result[1] = longitude;
        }
        return result;
    }


    public static void getCoordinatesFromAddress(String address, final OnGeoUtilResultListener
            listener) {
        SmartLocation.with(MoneyApplication.getAppContext()).geocoding().direct(address, (name, results) -> {
            if (results.size() > 0) {
                listener.onCoordinatesResolved(results.get(0).getLocation(), address);
            }
        });
    }


    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?key=" + API_KEY + "&input=" +
                    URLEncoder.encode(input, "utf8"));
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
        }

        return resultList;
    }


    public static boolean areCoordinates(String string) {
        Pattern p = Pattern.compile("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|" +
                "([1-9]?\\d))(\\.\\d+)?)$");
        Matcher m = p.matcher(string);
        return m.matches();
    }
}
