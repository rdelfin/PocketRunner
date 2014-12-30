package com.foxtailgames.pocketrunner.databases;

import android.provider.BaseColumns;

/**
 * Created by Ricardo on 02/12/2014.
 */
public class RunReaderContract {

    public RunReaderContract() {}

    public static abstract class RunEntry implements BaseColumns {
        public static final String TABLE_NAME = "runs";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_UNITS = "units";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_LAPS = "laps";
    }

    public static abstract class LapEntry implements BaseColumns {
        public static final String TABLE_NAME = "laps";
        public static final String COLUMN_NAME_LAP_NUMBER = "lap_number";
        public static final String COLUMN_NAME_RUN_ID = "run_id";
        public static final String COLUMN_NAME_TIME = "time";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ", ";

    public static final String SQL_CREATE_TABLE_RUN =
        "CREATE TABLE " + RunEntry.TABLE_NAME + " (" +
            RunEntry._ID + " TEXT PRIMARY KEY" + COMMA_SEP +
            RunEntry.COLUMN_NAME_DATE + INTEGER_TYPE + COMMA_SEP +
            RunEntry.COLUMN_NAME_DISTANCE + REAL_TYPE + COMMA_SEP +
            RunEntry.COLUMN_NAME_UNITS + TEXT_TYPE + COMMA_SEP +
            RunEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
            RunEntry.COLUMN_NAME_LAPS + INTEGER_TYPE + "); ";

    public static final String SQL_CREATE_TABLE_LAP =
        "CREATE TABLE " + LapEntry.TABLE_NAME + "(" +
            LapEntry.COLUMN_NAME_LAP_NUMBER + INTEGER_TYPE + COMMA_SEP +
            LapEntry.COLUMN_NAME_RUN_ID + INTEGER_TYPE + COMMA_SEP +
            LapEntry.COLUMN_NAME_TIME + INTEGER_TYPE + COMMA_SEP +
            "FOREIGN KEY(" + LapEntry.COLUMN_NAME_RUN_ID + ") REFERENCES " + RunEntry.TABLE_NAME + "(" + RunEntry._ID + ")" + COMMA_SEP +
            "PRIMARY KEY(" + LapEntry.COLUMN_NAME_LAP_NUMBER + COMMA_SEP + LapEntry.COLUMN_NAME_LAP_NUMBER + ") );";

    public static final String SQL_DELETE_TABLE_RUN =
        "DROP TABLE IF EXISTS " + RunEntry.TABLE_NAME + ";";
    public static final String SQL_DELETE_TABLE_LAP =
        "DROP TABLE IF EXISTS " + LapEntry.TABLE_NAME + ";";


}
