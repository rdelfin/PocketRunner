package com.foxtailgames.pocketrunner.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.foxtailgames.pocketrunner.utilities.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ricardo on 02/12/2014.
 */
public class RunDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RunReader.db";

    DateFormat df;

    public RunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RunContract.SQL_CREATE_TABLE_RUN);
        db.execSQL(RunContract.SQL_CREATE_TABLE_LAP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RunContract.SQL_DELETE_TABLE_LAP);
        db.execSQL(RunContract.SQL_DELETE_TABLE_RUN);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addRun(Run run) {
        SQLiteDatabase db = getWritableDatabase();
        String runId = run.id.toString();

        ContentValues runValues = new ContentValues();
        runValues.put(RunContract.RunEntry._ID, run.id.toString());
        runValues.put(RunContract.RunEntry.COLUMN_NAME_DATE, df.format(run.date));
        runValues.put(RunContract.RunEntry.COLUMN_NAME_DISTANCE, run.distance);
        runValues.put(RunContract.RunEntry.COLUMN_NAME_UNITS, run.units);
        runValues.put(RunContract.RunEntry.COLUMN_NAME_TIME, run.time.getTotalMilliseconds());
        runValues.put(RunContract.RunEntry.COLUMN_NAME_LAPS, run.lapTimes.length);

        db.insert(RunContract.RunEntry.TABLE_NAME, null, runValues);

        int i = 0;
        //  So that the data structure can be changed to any collection
        for(long lapTime : run.lapTimes) {
            ContentValues lapValues = new ContentValues();
            lapValues.put(RunContract.LapEntry.COLUMN_NAME_LAP_NUMBER, i);
            lapValues.put(RunContract.LapEntry.COLUMN_NAME_RUN_ID, runId);
            lapValues.put(RunContract.LapEntry.COLUMN_NAME_TIME, lapTime);
            db.insert(RunContract.LapEntry.TABLE_NAME, null, lapValues);
            i++;
        }

        db.close();
    }

    public List<Run> getAllRuns() {
        ArrayList<Run> runs = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                RunContract.RunEntry._ID,
                RunContract.RunEntry.COLUMN_NAME_DATE,
                RunContract.RunEntry.COLUMN_NAME_DISTANCE,
                RunContract.RunEntry.COLUMN_NAME_UNITS,
                RunContract.RunEntry.COLUMN_NAME_TIME
        };
        String sortOrder = RunContract.RunEntry.COLUMN_NAME_DATE + " DESC";

        Cursor c = db.query(RunContract.RunEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
        boolean hasVal = c.moveToFirst();

        while(hasVal) {
            UUID id = UUID.fromString(c.getString(c.getColumnIndex(RunContract.RunEntry._ID)));
            Date date;
            try {
                date = df.parse(c.getString(c.getColumnIndex(RunContract.RunEntry.COLUMN_NAME_DATE)));
            } catch(ParseException e) {
                date = new Date();
            }
            double distance = c.getDouble(c.getColumnIndex(RunContract.RunEntry.COLUMN_NAME_DISTANCE));
            String units = c.getString(c.getColumnIndex(RunContract.RunEntry.COLUMN_NAME_UNITS));
            long time = c.getLong(c.getColumnIndex(RunContract.RunEntry.COLUMN_NAME_TIME));

            long[] laps = getLaps(db, id);

            Run run = new Run(id, date, distance, units, new Time(time), laps);

            runs.add(run);

            hasVal = c.moveToNext();
        }

        db.close();

        return runs;
    }

    private long[] getLaps(SQLiteDatabase db, UUID id) {
        long[] result;
        LinkedList<Long> linkedList = new LinkedList<>();
        String[] projection = {
            RunContract.LapEntry.COLUMN_NAME_LAP_NUMBER,
            RunContract.LapEntry.COLUMN_NAME_RUN_ID,
            RunContract.LapEntry.COLUMN_NAME_TIME
        };
        String sortOrder = RunContract.LapEntry.COLUMN_NAME_LAP_NUMBER + " ASC";
        String selection = RunContract.LapEntry.COLUMN_NAME_RUN_ID + " LIKE ?";
        String[] selectionArgs = { id.toString() };

        //Query the database for the run
        Cursor c = db.query(RunContract.LapEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        //Put the lap lengths into a long array
        boolean hasVal = c.moveToFirst();
        while(hasVal) {
            linkedList.addLast(Long.parseLong(c.getString(c.getColumnIndex(RunContract.LapEntry.COLUMN_NAME_TIME))));
            hasVal = c.moveToNext();
        }

        //Copy linked list to an array O(n)
        result = new long[linkedList.size()];
        int i = 0;
        for(Long l : linkedList) {
            result[i] = l;
            i++;
        }

        return result;

    }
}