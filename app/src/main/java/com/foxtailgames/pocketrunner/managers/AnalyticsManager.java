package com.foxtailgames.pocketrunner.managers;

import android.content.Context;

import com.foxtailgames.pocketrunner.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by rdelfin on 1/9/15.
 */
public class AnalyticsManager {

    private static AnalyticsManager instance = null;

    public static AnalyticsManager getInstance(Context context) {
        if(instance == null) {
            instance = new AnalyticsManager(context);
        }
        return instance;
    }

    private Context context;

    private AnalyticsManager(Context context) {
        this.context = context;
    }

    private static final String PROPERTY_ID = "UA-58385726-1";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
