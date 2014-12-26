package com.foxtailgames.pocketrunner;

import android.content.Context;
import android.util.Log;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Iterator;
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

    private final static int LAP_ADD_TIME_PEBBLE_KEY = 5;
    private final static int LAP_ADD_MSG_PHONE_KEY = 6;
    private final static int PAUSE_TIME_PEBBLE_KEY = 7;
    private final static int PAUSE_TIME_PHONE_KEY = 8;

    private final static int INITIAL_DATA_TRANSACTION_ID = 42;
    private final static int LAP_ADD_PHONE_TRANSACTION_ID = 43;
    private final static int PAUSE_PHONE_TRANSACTION_ID = 44;

    private final static int MAX_RESENDS = 10;
    private final static int WAIT_TIME = 100;

    protected int resendCount;
    protected Context context;
    protected RunManager runManager;

    protected double lapLength;
    protected String units;
    protected boolean useDistanceForAlarm;
    protected double distanceForAlarm;
    protected long endTime;

    public PebbleManager(Context context, RunManager runManager, double lapLength, String units, boolean useDistanceForAlarm, double distanceForAlarm, Time endTime) {
        this.context = context;
        this.runManager = runManager;
        this.resendCount = 0;

        this.lapLength = lapLength;
        this.units = units;
        this.useDistanceForAlarm = useDistanceForAlarm;
        this.distanceForAlarm = distanceForAlarm;
        this.endTime = endTime.getTotalMilliseconds();

        //Setup Ack's and Nack's and all that
        setupReceiveHandlers();
        PebbleKit.startAppOnPebble(context, PEBBLE_APP_UUID);
        if(PebbleKit.isWatchConnected(context) && PebbleKit.areAppMessagesSupported(context)) {
            sendData(lapLength, units, useDistanceForAlarm, distanceForAlarm, endTime.getTotalMilliseconds());
        }
    }

    public void setupReceiveHandlers() {
        /*PebbleKit.registerPebbleConnectedReceiver(context, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("PEBBLE_MANAGER", "Pebble connected!");

                //Open Pebble app
                PebbleKit.startAppOnPebble(context, PEBBLE_APP_UUID);

                sendData();
            }
        });
        PebbleKit.registerPebbleDisconnectedReceiver(context, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("PEBBLE_MANAGER", "Pebble disconected!");
            }
        });*/

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
                    Log.i("PEBBLE_INIT", "Received acknowledge properly");
                }
            }
        });

        PebbleKit.registerReceivedNackHandler(context, new PebbleKit.PebbleNackReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveNack(Context context, int transactionId) {
                //Only check for the Initial data transaction
                if (transactionId == INITIAL_DATA_TRANSACTION_ID) {
                    resendCount++;
                    Log.i("PEBBLE_INIT", "Received not acknowledge. ERROR." + ((resendCount <= MAX_RESENDS) ? " Resending..." : ""));

                    //Only resend if its been sent less than MAX_RESENDS times
                    if(resendCount <= MAX_RESENDS) {
                        try { Thread.sleep(WAIT_TIME); } catch (InterruptedException e) { System.err.println(e.toString()); }

                        sendData(lapLength, units, useDistanceForAlarm, distanceForAlarm, endTime);
                    }
                }
            }
        });

        PebbleKit.registerReceivedDataHandler(context, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveData(Context context, int transactionId, PebbleDictionary pebbleTuples) {
                Log.d("PebbleManager", "Recieved data from pebble");

                if(pebbleTuples.contains(LAP_ADD_TIME_PEBBLE_KEY)) {
                    Log.d("PebbleManager", "Lap recieved from pebble. Time val: " + pebbleTuples.getInteger(PAUSE_TIME_PEBBLE_KEY));
                    long time = pebbleTuples.getInteger(LAP_ADD_TIME_PEBBLE_KEY);
                    runManager.increaseLap(time);
                }
                if(pebbleTuples.contains(PAUSE_TIME_PEBBLE_KEY)) {
                    Log.d("PebbleManager", "Pause recieved from pebble. Time val: " + pebbleTuples.getInteger(PAUSE_TIME_PEBBLE_KEY));
                    long time = pebbleTuples.getInteger(PAUSE_TIME_PEBBLE_KEY);
                    runManager.stopClicked();

                    if(runManager.isPaused())
                        runManager.setChronometerTime(time);
                }
            }
        });
    }

    public void sendData(double lapLength, String units, boolean useDistanceForAlarm, double distanceForAlarm, long endTime) {
        //Send information to app
        PebbleDictionary data = new PebbleDictionary();
        data.addString(LAP_LENGTH_KEY, String.valueOf(lapLength));
        data.addString(UNITS_KEY, units);
        data.addUint8(USE_DISTANCE_ALARM_KEY, (byte) (useDistanceForAlarm ? 1 : 0));
        data.addString(END_DISTANCE_KEY, String.valueOf(distanceForAlarm));
        data.addInt32(END_TIME_KEY, (int) endTime);

        PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, data, INITIAL_DATA_TRANSACTION_ID);
    }

    public void sendLap() {
        PebbleDictionary data = new PebbleDictionary();
        data.addInt8(LAP_ADD_MSG_PHONE_KEY, (byte)1);

        PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, data, LAP_ADD_PHONE_TRANSACTION_ID);
    }

    public void sendPause(int time) {
        PebbleDictionary data = new PebbleDictionary();
        data.addInt32(PAUSE_TIME_PHONE_KEY, time);

        PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, data, PAUSE_PHONE_TRANSACTION_ID);
    }
}
