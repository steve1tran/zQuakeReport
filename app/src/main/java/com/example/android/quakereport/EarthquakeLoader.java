package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Steve Tran on 4/19/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {
    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        // TODO: Finish implementing this constructor
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        //Log.i(LOG_TAG, "onStartLoading");

        forceLoad();
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        //Log.i(LOG_TAG, "loadInBackground");

        if (mUrl == null) {
            return null;
        }

        ArrayList<Earthquake>  earthquakes = QueryUtils.extractEarthquakes(mUrl);
        return earthquakes;
    }
}
