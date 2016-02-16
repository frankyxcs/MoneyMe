package com.devmoroz.moneyme.models;


import android.os.Parcel;
import android.os.Parcelable;

public class TempLocation implements Parcelable {

    public static final Parcelable.Creator<TempLocation> CREATOR = new Parcelable.Creator<TempLocation>() {

        public TempLocation createFromParcel(Parcel in) {
            return new TempLocation(in);
        }


        public TempLocation[] newArray(int size) {
            return new TempLocation[size];
        }
    };

    private Double latitude;
    private Double longitude;
    private String address;

    private TempLocation(Parcel in) {
        setLatitude(in.readString());
        setLongitude(in.readString());
        setAddress(in.readString());
    }

    public TempLocation() {
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(String.valueOf(getLatitude()));
        parcel.writeString(String.valueOf(getLongitude()));
        parcel.writeString(getAddress());
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
}
