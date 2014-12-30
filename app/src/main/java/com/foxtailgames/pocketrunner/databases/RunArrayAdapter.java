package com.foxtailgames.pocketrunner.databases;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.foxtailgames.pocketrunner.R;
import com.foxtailgames.pocketrunner.Time;

import java.util.List;

/**
 * Created by Ricardo on 28/12/2014.
 */
public class RunArrayAdapter extends ArrayAdapter<Run> {

    Context context;
    int layoutResourceId;
    List<Run> data = null;

    public RunArrayAdapter(Context mContext, int layoutResourceId, List<Run> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RunHolder holder = null;


        Run run = data.get(position);

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RunHolder();
            holder.distance = (TextView)row.findViewById(R.id.run_text_distance);
            holder.time = (TextView)row.findViewById(R.id.run_text_duration);
            holder.laps = (TextView)row.findViewById(R.id.run_text_laps);
            holder.avgSpeed = (TextView)row.findViewById(R.id.run_text_avgspeed);
            holder.date = (TextView)row.findViewById(R.id.run_text_date);

            row.setTag(holder);
        } else {
            holder = (RunHolder)row.getTag();
        }

        int laps = run.getLapTimes().length;

        holder.distance.setText(run.getDistance() + " " + run.getUnits());
        holder.time.setText(run.getTime().toString());
        holder.laps.setText(laps + " " + context.getString(R.string.laps).toLowerCase());
        holder.avgSpeed.setText(new Time((long)(run.getTime().getTotalMilliseconds() / run.getDistance())).toString());
        holder.date.setText(run.getDate().toString());


        return row;
    }

    static class RunHolder
    {
        TextView distance;
        TextView time;
        TextView laps;
        TextView avgSpeed;
        TextView date;
    }


}
