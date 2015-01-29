PocketRunner
============
[![forthebadge](http://forthebadge.com/images/badges/fuck-it-ship-it.svg)](http://forthebadge.com)

Running App used to manage lap, times, and keep activities on track. Contains four activities to interact with the user: a `RunActivity` where most of the user interaction will take place, a `SettingsActivity` where the user will set up the run so that it works acording to the user specifications, a `StatisticsActivity` where all run statistics will be displayed (currently, it only lists each run and displays some basic information about each) and a `MainActivity`, where the user can navigate to any of these other activities.

The app also contains a protocol for communication with the joint pebble app. Basically, the app starts out sending the initial data for the run settings when the data starts, when the settings are changed in the settings window, and when the pebble watch requests it, through the message key `REQUEST_INITIAL` (All message keys can be found in the class [`com.foxtailgames.pocketrunner.managers.PebbleManager`](https://github.com/rickyman20/PocketRunner/blob/master/app/src/main/java/com/foxtailgames/pocketrunner/managers/PebbleManager.java)). Afterwards, the pebble app can manage a run independently.

Once the user presses the finish button on the Pebble, it will begin by sending a `RUN_OPEN` request. The Android app will create a run and return a UUID to the pebble with the message key `RUN_UUID_DEFINE`. The pebble then proceeds to barage the phone with all the run data, sending the time (`RUN_TIME`), number of laps, (`RUN_LAPS`), the individual lap times, each in a separate package (`RUN_LAP_TIME`) and a close to acknowledge the end of the datastream (`RUN_CLOSE`). Each package sent contains the run UUID, which is confirmed by this app (`RUN_UUID_ACK`).

Links
-----
* [App on the Google Play Store](https://play.google.com/store/apps/details?id=com.foxtailgames.pocketrunner)
* [Pebble App on Github](https://github.com/rickyman20/PocketRunnerPebble)
* [Published Pebble App](https://apps.getpebble.com/applications/54ad8b9d32203eb1200000b3)
