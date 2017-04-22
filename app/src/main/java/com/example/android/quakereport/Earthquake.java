package com.example.android.quakereport;

/**
 * Created by Steve Tran on 4/13/2017.
 */

public class Earthquake {
    private double mMagnitude = 0;
    private String mLocation = null;
    private long mTimeInMilliseconds = 0;
    private String mUrl;

    // Three inputs
    public Earthquake(double magnitude, String location, long time, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = time;
        mUrl = url;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    /**
     * Returns the time of the earthquake.
     */
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "mMagnitude='" + mMagnitude + '\'' +
                ", mLocation='" + mLocation + '\'' +
                ", mTimeInMilliseconds=" + mTimeInMilliseconds +
                '}';
    }
}
