package com.foxtailgames.pocketrunner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.foxtailgames.pocketrunner.managers.AnalyticsManager;
import com.foxtailgames.pocketrunner.managers.RunManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import gr.antoniom.chronometer.PreciseChronometer;


public class RunActivity extends ActionBarActivity {

    boolean running, started;
    RunManager runManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        running = false;
        started = false;
        runManager = new RunManager(this, (PreciseChronometer)findViewById(R.id.chrono_label));
        updateAll();


        //Tracking for google analytics
        Tracker t = AnalyticsManager.getInstance(getApplicationContext())
                .getTracker(AnalyticsManager.TrackerName.APP_TRACKER);
        t.setScreenName(getPackageName() + "." + getLocalClassName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void lapClicked(View view) {
        runManager.lapClicked();
        updateAll();
        if(runManager.isDone())
            finish();
    }

    public void stopClicked(View view) {
        runManager.stopClicked();
        updateAll();
    }

    public void updateAll() {
        updateButtonsText();
        updateRemaining();
        updateAverageSpeed();
        updateLapCount();
    }

    private void updateAverageSpeed() {
        ((TextView)findViewById(R.id.average_speed_label)).setText(runManager.getAverageSpeed());
    }

    private void updateButtonsText() {
        if(runManager.showStop()) {
            ((Button)findViewById(R.id.stop_button)).setText(getString(R.string.stop));
            ((Button)findViewById(R.id.lap_button)).setText(getString(R.string.lap));
        } else {
            ((Button)findViewById(R.id.stop_button)).setText(getString(R.string.start));
            ((Button)findViewById(R.id.lap_button)).setText(getString(R.string.done));
        }
    }

    private void updateRemaining() {
        ((TextView)findViewById(R.id.remaining_label)).setText(runManager.getRemainingText());
    }

    private void updateLapCount() {
        int lapCount = runManager.getLapCount();
        String lapsText = getString(lapCount == 1 ? R.string.lap : R.string.laps).toLowerCase();
        ((TextView)findViewById(R.id.lap_count_label)).setText(runManager.getLapCount() + " " + lapsText);
    }
}
