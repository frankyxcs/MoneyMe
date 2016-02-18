package com.devmoroz.moneyme.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Parcelable {

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {

        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }


        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    private String id;
    private LatLng latlng;
    private Double latitude;
    private Double longitude;
    private String address;
    private String name;

    private Location(Parcel in) {
        setId(in.readString());
        setLatLng(in.readParcelable(LatLng.class.getClassLoader()));
        setLatitude(in.readString());
        setLongitude(in.readString());
        setAddress(in.readString());
        setName(in.readString());
    }

    public Location() {
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeParcelable(latlng, flags);
        parcel.writeString(String.valueOf(getLatitude()));
        parcel.writeString(String.valueOf(getLongitude()));
        parcel.writeString(getAddress());
        parcel.writeString(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLatitude(String latitude) {
        try {
            this.setLatitude(Double.parseDouble(latitude));
        } catch (NumberFormatException | NullPointerException var3) {
            this.latitude = null;
        }

    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLongitude(String longitude) {
        try {
            this.setLongitude(Double.parseDouble(longitude));
        } catch (NumberFormatException | NullPointerException var3) {
            this.longitude = null;
        }

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isLocationValid() {
        return this.getLatitude() != null
                && this.getLatitude() != 0
                && this.getLongitude() != null
                && this.getLongitude() != 0;
    }

    public LatLng getLatLng() {
        return latlng;
    }

    public void setLatLng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
