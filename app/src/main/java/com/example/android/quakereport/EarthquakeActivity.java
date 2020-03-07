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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Earthquake>>{

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    static int loaderId = 0;
    EarthquakeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of earthquakes as input
        adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setEmptyView(findViewById(R.id.emptyStateText));

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Start the AsyncTask to fetch the earthquake data
        //QueryThread task = new QueryThread();
        //task.execute(USGS_REQUEST_URL);
        if(isNetworkAvailable()) {
            getLoaderManager().initLoader(loaderId, null, this).forceLoad();
        }
        else {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.emptyStateText)).setText("No Internet Connection");
        }

        Log.v("state","Create");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("state","Restart");
        getSupportLoaderManager().destroyLoader(loaderId);
        getLoaderManager().initLoader(++loaderId, null, this).forceLoad();

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.v("state","Resume");
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
        else if(id == R.id.refresh){
            adapter.clear();
            getSupportLoaderManager().destroyLoader(loaderId);
            ((TextView) findViewById(R.id.emptyStateText)).setVisibility(View.GONE);
            if(isNetworkAvailable()) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.emptyStateText)).setText("No Earthquakes Found");
                getLoaderManager().initLoader(++loaderId, null, this).forceLoad();

            }
            else {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.emptyStateText)).setText("No Internet Connection");
            }
        }
        else if(id == R.id.exit){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"onCreateLoader");
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
        uriBuilder.appendQueryParameter("limit", "100");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        return new EarthquakeLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
        // Clear the adapter of previous earthquake data
        adapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            adapter.addAll(earthquakes);
        }

        if(isNetworkAvailable()) {
            ((TextView) findViewById(R.id.emptyStateText)).setText("No Earthquakes Found");
        }
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        Log.i(LOG_TAG,"onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        // Clear the adapter of previous earthquake data
        adapter.clear();

        Log.i(LOG_TAG,"onLoaderReset");
    }


    private class QueryThread extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls[0] == null || urls[0].length() <1) {
                return null;
            }
            return new QueryUtils(urls[0]).extractEarthquakes();
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
            // Clear the adapter of previous earthquake data
            adapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (earthquakes != null && !earthquakes.isEmpty()) {
                adapter.addAll(earthquakes);
            }
        }
    }
}
