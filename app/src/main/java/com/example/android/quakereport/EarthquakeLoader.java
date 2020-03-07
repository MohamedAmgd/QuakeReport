package com.example.android.quakereport;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {
    private String url;

    final String LOG_TAG = EarthquakeLoader.class.getName();
    public EarthquakeLoader(Context context , String url) {
        super(context);
        this.url = url;
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        Log.i(LOG_TAG , "loadInBackground");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (url == null || url.isEmpty()) {
            return null;
        }
        return new QueryUtils(url).extractEarthquakes();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.i(LOG_TAG,"onStartLoading");
    }
}
