package com.foxtailgames.pocketrunner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;


public class RunActivity extends ActionBarActivity {

    boolean running, started;
    RunManager runManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        running = false;
        started = false;
        runManager = new RunManager(getApplicationContext(), (Chronometer)findViewById(R.id.chrono_label));
        updateButtonsText();
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
        updateButtonsText();
    }

    public void stopClicked(View view) {
        runManager.stopClicked();
        updateButtonsText();
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
}
