/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.Loader;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 1;

    // Find a ref. to ListView
    //ListView mEarthquakeListView;

    private TextView mEmptyStateTextView;

    private ProgressBar mProgressBar;

    /** Adapter for the list of earthquakes */
    private EarthquakeAdapter mAdapter;

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query"; //?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        // setContentView(R.layout.earthquake_activity);


        // Create a new adapter for earthquakes
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the ListView
        // so the list can be populated in the user interface
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setAdapter(mAdapter);

        // Set empty data view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        // Set progressbar view
        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        if (hasInternetConnection(this)) {
            //Log.i(LOG_TAG, "initLoader");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Kick off an {@link AsyncTask) to perform the network request
        //TsunamiAsyncTask task = new TsunamiAsyncTask();
        //task.execute(USGS_REQUEST_URL);
    }

/*    private class TsunamiAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            ArrayList<Earthquake>  earthquake = QueryUtils.extractEarthquakes(urls[0]);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return earthquake;
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
            if (earthquakes == null) {
                return;
            }

            updateUi(earthquakes);
        }
    }*/

    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        //Log.i(LOG_TAG, "onCreateLoader");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
        mEmptyStateTextView.setText(R.string.no_earthquakes_found);
        mProgressBar.setVisibility(View.INVISIBLE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
        //updateUi(earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        //Log.i(LOG_TAG, "onLoaderReset");

        // TODO: Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    private boolean hasInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the UI with the given earthquake information.
     */
    private void updateUi(final ArrayList<Earthquake> earthquakes) {

        // Create an {@link EarthquakeAdapter}, whose data source is a list of {@link Earthquake}s. The
        // adapter knows how to create list items for each item in the list.
        EarthquakeAdapter adapter = new EarthquakeAdapter(this, earthquakes);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        // Add OnClick listener to each list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = earthquakes.get(position);
                String url = earthquake.getUrl();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

    }
}
