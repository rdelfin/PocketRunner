package com.foxtailgames.pocketrunner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.foxtailgames.pocketrunner.databases.Run;
import com.foxtailgames.pocketrunner.databases.RunReaderDbHelper;
import com.foxtailgames.pocketrunner.utilities.ConversionValues;
import com.foxtailgames.pocketrunner.utilities.UnitDoesNotExistException;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    /*==============================================================================
      ========================== OVERRIDEN METHODS =================================
      ==============================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTotalDistance();
    }


    //When returning from back, we need to update the screen
    @Override
    protected void onResume() {
        super.onResume();

        setTotalDistance();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_start_run) {
            startRun(null);
        } else if(id == R.id.action_statistics) {
            openStatistics(null);
        }

        return super.onOptionsItemSelected(item);
    }

    /*==============================================================================
      =================================== SETUP ====================================
      ==============================================================================*/
    void setTotalDistance()
    {
        String units;
        double total = 0;
        String text;
        ConversionValues converter = new ConversionValues(getApplicationContext());
        TextView totalDistance = (TextView)findViewById(R.id.total_distance_textview);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Open database, get list of runs and close database
        RunReaderDbHelper dbHelper = new RunReaderDbHelper(getApplicationContext());
        List<Run> runs = dbHelper.getAllRuns();
        dbHelper.close();

        //Always get units as selected from the preferences menu. Default to kilometers
        String kmUnits = getResources().getStringArray(R.array.units_list_vals)[2];
        units = sharedPreferences.getString(getString(R.string.units_list_key), kmUnits);

        /*
         * Quick review of how the text-setting works: if there is at least one run, then the text
         * will be displayed accordingly (show the distance ran plus units, which are km if none have
         * been selected). Otherwise, if there are no runs, one of two things can happen. If the
         * units have been set, then a message saying "Go out for a run!" will be displayed instead.
         * Otherwise, if the units have not been set, "Go to settings!" will be displayed.
         */
        if(runs.size() == 0) {
            //Hide the title
            findViewById(R.id.total_distance_title).setVisibility(View.INVISIBLE);

            // Settings not set
            if(sharedPreferences.getString(getString(R.string.units_list_key), null) == null)
                text = getString(R.string.go_to_settings);
            else
                text = getString(R.string.go_for_a_run);


        } else {
            //Show the title
            findViewById(R.id.total_distance_title).setVisibility(View.VISIBLE);

            for(int i = 0; i < runs.size(); i++) {
                Run run = runs.get(i);
                try {
                    total += converter.convert(run.getDistance(), run.getUnits(), units);
                } catch(UnitDoesNotExistException e) {
                    //If the exception is called, do not add the value and log the error
                    Log.e("MainActivity", "EXCEPTION: " + e.getMessage());
                }
            }

            text = String.format("%.2f %s", total, units);
        }

        //Always set the appropriate text at the end
        totalDistance.setText(text);
    }



    /*=============================================================================
      ========================== EVENT LISTENERS ==================================
      =============================================================================*/

    public void startRun(View view) {
        Intent intent = new Intent(getApplicationContext(), RunActivity.class);
        startActivity(intent);
    }

    public void openStatistics(View view) {
        Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
        startActivity(intent);
    }
}
