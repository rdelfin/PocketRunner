package com.foxtailgames.pocketrunner;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.foxtailgames.pocketrunner.databases.Run;
import com.foxtailgames.pocketrunner.databases.RunArrayAdapter;
import com.foxtailgames.pocketrunner.databases.RunReaderDbHelper;

import java.util.List;


public class StatisticsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        loadRunList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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

    private void loadRunList()
    {
        ListView runsView = (ListView)findViewById(R.id.runs_list_view);
        RunReaderDbHelper dbHelper = new RunReaderDbHelper(getApplicationContext());

        List<Run> runList = dbHelper.getAllRuns();

        if(runList.size() == 0)
        {
            Log.d("StatisticsActivity", "RunList is empty!");
            runsView.setVisibility(View.GONE);
        }
        else
        {
            Log.d("StatisticsActivity", "RunList has " + runList.size() + " items!");
            runsView.setVisibility(View.VISIBLE);

            //Use array adapter
            RunArrayAdapter runAdapter = new RunArrayAdapter(this,R.layout.run_listview_item, runList);
            //bookDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            runsView.setAdapter(runAdapter);
        }
    }
}
