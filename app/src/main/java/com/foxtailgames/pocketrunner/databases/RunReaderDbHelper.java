package com.foxtailgames.pocketrunner.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.foxtailgames.pocketrunner.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by Ricardo on 02/12/2014.
 */
public class RunReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RunReader.db";

    DateFormat df;

    public RunReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RunReaderContract.SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RunReaderContract.SQL_DELETE_TABLES);
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
        runValues.put(RunReaderContract.RunEntry._ID, run.id.toString());
        runValues.put(RunReaderContract.RunEntry.COLUMN_NAME_DATE, df.format(run.date));
        runValues.put(RunReaderContract.RunEntry.COLUMN_NAME_DISTANCE, run.distance);
        runValues.put(RunReaderContract.RunEntry.COLUMN_NAME_UNITS, run.units);
        runValues.put(RunReaderContract.RunEntry.COLUMN_NAME_TIME, run.time.getTotalMilliseconds());
        runValues.put(RunReaderContract.RunEntry.COLUMN_NAME_LAPS, run.lapTimes.length);

        db.insert(RunReaderContract.RunEntry.TABLE_NAME, null, runValues);

        int i = 0;
        //  So that the data structure can be changed to any collection
        for(long lapTime : run.lapTimes) {
            ContentValues lapValues = new ContentValues();
            lapValues.put(RunReaderContract.LapEntry.COLUMN_NAME_LAP_NUMBER, i);
            lapValues.put(RunReaderContract.LapEntry.COLUMN_NAME_RUN_ID, runId);
            lapValues.put(RunReaderContract.LapEntry.COLUMN_NAME_TIME, lapTime);
            i++;
        }

        db.close();
    }

    public Run[] getAllRuns() {
        LinkedList<Run> runs = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Run[] result;

        String[] projection = {
                RunReaderContract.RunEntry._ID,
                RunReaderContract.RunEntry.COLUMN_NAME_DATE,
                RunReaderContract.RunEntry.COLUMN_NAME_DISTANCE,
                RunReaderContract.RunEntry.COLUMN_NAME_UNITS,
                RunReaderContract.RunEntry.COLUMN_NAME_TIME
        };
        String sortOrder = RunReaderContract.RunEntry.COLUMN_NAME_DATE + " DESC";

        Cursor c = db.query(RunReaderContract.RunEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
        boolean hasVal = c.moveToFirst();

        while(hasVal) {
            UUID id = UUID.fromString(c.getString(c.getColumnIndex(RunReaderContract.RunEntry._ID)));
            Date date;
            try {
                date = df.parse(c.getString(c.getColumnIndex(RunReaderContract.RunEntry.COLUMN_NAME_DATE)));
            } catch(ParseException e) {
                date = new Date();
            }
            double distance = c.getDouble(c.getColumnIndex(RunReaderContract.RunEntry.COLUMN_NAME_DISTANCE));
            String units = c.getString(c.getColumnIndex(RunReaderContract.RunEntry.COLUMN_NAME_UNITS));
            long time = c.getLong(c.getColumnIndex(RunReaderContract.RunEntry.COLUMN_NAME_TIME));

            long[] laps = getLaps(db, id);

            Run run = new Run(id, date, distance, units, new Time(time), laps);

            runs.addLast(run);

            hasVal = c.moveToNext();
        }

        db.close();

        result = new Run[runs.size()];
        int i = 0;
        for(Run run : runs) {
            result[i] = run;
            i++;
        }

        return result;
    }

    private long[] getLaps(SQLiteDatabase db, UUID id) {
        long[] result;
        LinkedList<Long> linkedList = new LinkedList<>();
        String[] projection = {
            RunReaderContract.LapEntry.COLUMN_NAME_LAP_NUMBER,
            RunReaderContract.LapEntry.COLUMN_NAME_RUN_ID,
            RunReaderContract.LapEntry.COLUMN_NAME_TIME
        };
        String sortOrder = RunReaderContract.LapEntry.COLUMN_NAME_LAP_NUMBER + " ASC";
        String selection = RunReaderContract.LapEntry.COLUMN_NAME_RUN_ID + " LIKE ?";
        String[] selectionArgs = { id.toString() };

        //Query the database for the run
        Cursor c = db.query(RunReaderContract.LapEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        //Put the lap lengths into a long array
        boolean hasVal = c.moveToFirst();
        while(hasVal) {
            linkedList.addLast(Long.parseLong(c.getString(c.getColumnIndex(RunReaderContract.LapEntry.COLUMN_NAME_TIME))));
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