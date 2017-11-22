package com.vijay.locationviewer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay-3593 on 21/11/17.
 */

public class Coordinates implements Parcelable{
    private List<Double[]> coordinates = new ArrayList<>();

    public Coordinates() {

    }

    protected Coordinates(Parcel in) {
        in.readList(coordinates, Coordinates.class.getClassLoader());
    }

    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };

    public void setCoordinates(List<Double[]> values){
        this.coordinates = values;
    }

    public List<Double[]> getCoordinates() {
        return coordinates;
    }

    public void addCoordinate(Double[] value) {
        coordinates.add(value);
    }

    public void addAll(List<Double[]> value) {
        coordinates.addAll(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(coordinates);
    }
}
