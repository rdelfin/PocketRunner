package com.foxtailgames.pocketrunner.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.foxtailgames.pocketrunner.R;
import com.foxtailgames.pocketrunner.databases.Run;
import com.foxtailgames.pocketrunner.databases.RunDbHelper;
import com.foxtailgames.pocketrunner.utilities.Time;
import com.foxtailgames.pocketrunner.utilities.Util;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author Ricardo Delfin Garcia
 * @version 1.0
 */
public class PebbleManager {

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("274d5bb8-b7b8-4ff9-b3f2-9b32cee39ad6");

    private final static int LAP_LENGTH_KEY = 0;
    private final static int UNITS_KEY = 1;
    private final static int USE_DISTANCE_ALARM_KEY = 2;
    private final static int END_DISTANCE_KEY = 3;
    private final static int END_TIME_KEY = 4;
    private final static int REQUEST_INITIAL = 5;


    private final static int RUN_OPEN = 6;
    private final static int RUN_UUID_DEFINE = 7;
    private final static int RUN_TIME = 8;
    private final static int RUN_LAPS = 9;
    private final static int RUN_LAP_TIME = 10;
    private final static int RUN_UUID_ACK = 11;
    private final static int RUN_CLOSE = 12;

    private final static int INITIAL_DATA_TRANSACTION_ID = 42;
    private final static int DEFINE_RUN_UUID_TRANSACTION_ID = 43;

    private final static int MAX_RESENDS = 10;
    private final static int WAIT_TIME = 100;

    protected int resendCount;
    protected Context context;

    protected double lapLength;
    protected String units;
    protected boolean useDistanceForAlarm;
    protected double distanceForAlarm;
    protected long endTime;

    protected Run sentRun;
    protected ArrayList<Time> lapTimesBuffer;
    protected long lapCountRecieved;


    //Singleton!
    private static PebbleManager instance = null;
    public static PebbleManager getInstance(Context context) {
        if(instance == null)
            instance = new PebbleManager(context);
        return instance;
    }


    private PebbleManager(Context context) {
        this.context = context;
        this.resendCount = 0;

        updateVals();

        //Setup Ack's and Nack's and all that
        setupReceiveHandlers();

        PebbleKit.startAppOnPebble(context, PEBBLE_APP_UUID);
        if(PebbleKit.isWatchConnected(context) && PebbleKit.areAppMessagesSupported(context)) {
            sendData(lapLength, units, useDistanceForAlarm, distanceForAlarm, endTime);
        }

        //Check if AppMessages are supported on android
        if (PebbleKit.areAppMessagesSupported(context)) {
            Log.i("PebbleManager", "App Message is supported!");
        } else {
            Log.i("PebbleManager", "App Message is not supported");
        }
    }

    public void updateValsAndSend() {
        updateVals();
        sendData(lapLength, units, useDistanceForAlarm, distanceForAlarm, endTime);
    }

    private void updateVals() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int hoursEndTime = Integer.parseInt(preferences.getString(context.getString(R.string.time_hours_for_alarm_key), "0"));
        int minutesEndTime = Integer.parseInt(preferences.getString(context.getString(R.string.time_minutes_for_alarm_key), "0"));
        int secondsEndTime = Integer.parseInt(preferences.getString(context.getString(R.string.time_minutes_for_alarm_key), "0"));
        Time endTime = new Time(hoursEndTime, minutesEndTime, secondsEndTime);

        this.lapLength = Double.parseDouble(preferences.getString(context.getString(R.string.lap_length_input_key), "0"));
        this.units = preferences.getString(context.getString(R.string.units_list_key), "Km");
        this.useDistanceForAlarm = preferences.getBoolean(context.getString(R.string.use_distance_key), false);
        this.distanceForAlarm = Double.parseDouble(preferences.getString(context.getString(R.string.distance_for_alarm_key), "0"));
        this.endTime = endTime.getTotalMilliseconds();
    }

    private void setupReceiveHandlers() {
       PebbleKit.registerPebbleConnectedReceiver(context, new BroadcastReceiver() {
           @Override
           public void onReceive(Context context, Intent intent) {
               Log.i("PEBBLE_MANAGER", "Pebble connected!");
           }
       });
       PebbleKit.registerPebbleDisconnectedReceiver(context, new BroadcastReceiver() {
           @Override
           public void onReceive(Context context, Intent intent) {
               Log.i("PEBBLE_MANAGER", "Pebble disconected!");
           }
       });

        PebbleKit.registerReceivedAckHandler(context, new PebbleKit.PebbleAckReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveAck(Context context, int transactionId) {
                //And when you say you won't forget me, well I can tell you that's untrue
                //'Cause every day since you left me, I've thought less and less of you
                //And I've worn out all the reasons to keep on knocking at your door
                //It could be the changing of the seasons, but I don't love you anymore

                //Only for the initial transaction id
                if(transactionId == INITIAL_DATA_TRANSACTION_ID) {
                    resendCount = 0;
                    Log.i("PebbleManager", "INIT: Received acknowledge properly");
                }
            }
        });

        PebbleKit.registerReceivedNackHandler(context, new PebbleKit.PebbleNackReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveNack(Context context, int transactionId) {
                //Only check for the Initial data transaction
                if (transactionId == INITIAL_DATA_TRANSACTION_ID) {
                    resendCount++;
                    Log.i("PebbleManager", "INIT: Received not acknowledge. ERROR." + ((resendCount <= MAX_RESENDS) ? " Resending..." : ""));

                    //Only resend if its been sent less than MAX_RESENDS times
                    if (resendCount <= MAX_RESENDS) {
                        try {
                            Thread.sleep(WAIT_TIME);
                        } catch (InterruptedException e) {
                            System.err.println(e.toString());
                        }

                        sendData(lapLength, units, useDistanceForAlarm, distanceForAlarm, endTime);
                    }
                }
            }
        });

        PebbleKit.registerReceivedDataHandler(context, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveData(Context context, int transactionId, PebbleDictionary pebbleTuples) {
                Log.i("PebbleManager", "Recieved data from pebble");

                if (pebbleTuples.contains(REQUEST_INITIAL)) {
                    //Initial data send. Only if pebble requests it
                    sendData(lapLength, units, useDistanceForAlarm, distanceForAlarm, endTime);
                } else if (pebbleTuples.contains(RUN_OPEN)) {
                    Log.i("PebbleManager", "Sending UUID...");
                    //Create run and set some values
                    sentRun = new Run();
                    sentRun.units = units;

                    lapTimesBuffer = new ArrayList<Time>();

                    //Send the UUID of the run
                    PebbleDictionary tuples = new PebbleDictionary();
                    tuples.addBytes(RUN_UUID_DEFINE, Util.byteArrayFromUUID(sentRun.id));
                    PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, tuples, DEFINE_RUN_UUID_TRANSACTION_ID);

                } else if (pebbleTuples.contains(RUN_TIME)) {
                    //Initial data
                    UUID uuid = Util.UUIDFromByteArray(pebbleTuples.getBytes(RUN_UUID_ACK));

                    if(uuid.equals(sentRun.id)) {
                        sentRun.time = new Time(pebbleTuples.getInteger(RUN_TIME));
                        lapCountRecieved = pebbleTuples.getInteger(RUN_LAPS);
                    }

                } else if (pebbleTuples.contains(RUN_LAP_TIME)) {
                    //Lap time recieved
                    byte[] uuidBytes = pebbleTuples.getBytes(RUN_UUID_ACK);
                    ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                    UUID uuid = new UUID(bb.getLong(), bb.getLong());

                    if(uuid.equals(sentRun.id)) {
                        lapTimesBuffer.add(new Time(pebbleTuples.getInteger(RUN_LAP_TIME)));
                    }

                } else if (pebbleTuples.contains(RUN_CLOSE)) {
                    //Close run: Save to db
                    UUID uuid = Util.UUIDFromByteArray(pebbleTuples.getBytes(RUN_CLOSE));

                    if(uuid.equals(sentRun.id)) {
                        sentRun.distance = lapCountRecieved * lapLength;

                        sentRun.lapTimes = new long[lapTimesBuffer.size()];
                        for(int i = 0; i < sentRun.lapTimes.length; i++) {
                            sentRun.lapTimes[i] = lapTimesBuffer.get(i).getTotalMilliseconds();
                        }

                        RunDbHelper dbHelper = new RunDbHelper(context);
                        dbHelper.addRun(sentRun);
                        dbHelper.close();
                        sentRun = null;
                    }
                }

                PebbleKit.sendAckToPebble(context, transactionId);
            }
        });
    }

    private void sendData(double lapLength, String units, boolean useDistanceForAlarm, double distanceForAlarm, long endTime) {
        //Send information to app
        PebbleDictionary data = new PebbleDictionary();
        data.addString(LAP_LENGTH_KEY, String.valueOf(lapLength));
        data.addString(UNITS_KEY, units);
        data.addUint8(USE_DISTANCE_ALARM_KEY, (byte) (useDistanceForAlarm ? 1 : 0));
        data.addString(END_DISTANCE_KEY, String.valueOf(distanceForAlarm));
        data.addInt32(END_TIME_KEY, (int) endTime);

        PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, data, INITIAL_DATA_TRANSACTION_ID);
    }
}
