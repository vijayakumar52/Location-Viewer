package com.vijay.locationviewer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay-3593 on 21/11/17.
 */

public class ClusterData implements Parcelable{
    private List<LocationData> coordinates = new ArrayList<>();

    public ClusterData() {

    }

    protected ClusterData(Parcel in) {
        in.readList(coordinates, ClusterData.class.getClassLoader());
    }

    public static final Creator<ClusterData> CREATOR = new Creator<ClusterData>() {
        @Override
        public ClusterData createFromParcel(Parcel in) {
            return new ClusterData(in);
        }

        @Override
        public ClusterData[] newArray(int size) {
            return new ClusterData[size];
        }
    };

    public void setCoordinates(List<LocationData> values){
        this.coordinates = values;
    }

    public List<LocationData> getCoordinates() {
        return coordinates;
    }

    public void addCoordinate(LocationData value) {
        coordinates.add(value);
    }

    public void addAll(List<LocationData> value) {
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
