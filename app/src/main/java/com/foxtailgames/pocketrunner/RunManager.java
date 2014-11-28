package com.foxtailgames.pocketrunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Chronometer;

import java.util.Date;

/**
 * Class in charge of managing the data from a single run. Has methods for getting strings to print,
 * saving and reading data from the run, counting time, triggering the alarm, etc.
 * @author Ricardo Delfin Garcia
 * @version 1.0
 */
public class RunManager {

    double lapLength;
    String units;
    boolean useDistanceForAlarm;
    double distanceForAlarm;
    Date endTime;
    Chronometer chronometer;
    long timeChronoStopped;
    boolean running, started;

    public RunManager(Context context, Chronometer chronometer) {
        updatePreferences(context);
        this.chronometer = chronometer;
        this.timeChronoStopped = 0;
        this.running = false;
        this.started = false;
    }

    public void updatePreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        lapLength = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.lap_length_input_key), "0"));
        units = sharedPreferences.getString(context.getString(R.string.units_list_key), "");
        useDistanceForAlarm = sharedPreferences.getBoolean(context.getString(R.string.use_distance_key), false);
        distanceForAlarm = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.distance_for_alarm_key), "0"));

        int timeHours = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.time_hours_for_alarm_key), "0"));
        int timeMinutes = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.time_minutes_for_alarm_key), "0"));
        int timeSeconds = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.time_seconds_for_alarm_key), "0"));

        endTime = new Date(0, 0, 0, timeHours, timeMinutes, timeSeconds);
    }

    public void start() {
        chronometer.setBase(SystemClock.elapsedRealtime() + timeChronoStopped);
        chronometer.start();
    }

    public void stop() {
        timeChronoStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
    }

    public void reset() {
        timeChronoStopped = 0;
    }

    public void stopClicked() {
        //If the person is running, button should say Stop! We logically stop and set running false
        if(running && started) {
            stop();
            running = false;
        }
        //Otherwise, we should start the clock. If !started, then we should reset too and set started appropriately
        else {
            if(!started) {
                started = true;
                reset();
            }

            start();
            running = true;
        }
    }

    public void lapClicked() {

    }

    boolean showStop() { return running; }
}
