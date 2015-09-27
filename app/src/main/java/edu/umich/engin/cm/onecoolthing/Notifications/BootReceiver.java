package edu.umich.engin.cm.onecoolthing.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import edu.umich.engin.cm.onecoolthing.Util.Constants;

/**
 * Created by jawad on 27/09/15.
 *
 * Called when the phone restarts, for reimplementing
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "MD/BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(LOGTAG, "Setting up the alarm again");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            // If the user doesn't want notifications, then nothing to do!
            if(!sharedPreferences.getBoolean(Constants.KEY_ENABLEDAILYDOSE, false)) {
                return;
            }

            // Get the hours and minutes to set the alarm at
            int hours = sharedPreferences.getInt(Constants.KEY_DAILYNOTIFTIME_HOUR, 8);
            int minutes = sharedPreferences.getInt(Constants.KEY_DAILYNOTIFTIME_MINUTE, 0);

            // Finally, set up the Alarm itself so the beautiful notifications can run once again
            AlarmNotificationManager.setNotificationAlarm(context, hours, minutes);
        }
    }
}
