package edu.umich.engin.cm.onecoolthing.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import edu.umich.engin.cm.onecoolthing.Util.Constants;

/**
 * Created by jawad on 27/09/15.
 */
public class AlarmNotificationManager {
    private static final String LOGTAG = "MD/AlarmNotificationMan";

    static public void turnOnNotificationIfPossible(Context context) {
        // First get the SharedPreferences to load up all the necessary values
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // If user turned off alarms of or if an alarm already set, then nothing to do
        if(!sharedPreferences.getBoolean(Constants.KEY_ENABLEDAILYDOSE, Constants.DEFAULTVAL_ENABLEDAILYDOSE) ||
                sharedPreferences.getBoolean(Constants.KEY_NOTIFSETYET, Constants.DEFAULTVAL_NOTIFSETYET)) {
            return;
        }
        // Otherwise start the alarm with the default values
        else {
            int hours = sharedPreferences.getInt(Constants.KEY_DAILYNOTIFTIME_HOUR,
                    Constants.DEFAULTVAL_DAILYNOTIFTIME_HOUR);
            int minutes = sharedPreferences.getInt(Constants.KEY_DAILYNOTIFTIME_MINUTE,
                    Constants.DEFAULTVAL_DAILYNOTIFTIME_MINUTE);
            setNotificationAlarm(context, hours, minutes);
        }
    }

    static public void setNotificationAlarm(Context context, int hours, int minutes) {
        // First cancel previously planned notifications, if any
        cancelNotificationAlarmIfNecessary(context);

        // Start the alarm for the notification at the requested time
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hours);
        startTime.set(Calendar.MINUTE, minutes);
        startTime.set(Calendar.SECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC, startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, getAlarmIntent(context));

        // Remember that the alarm has been set up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_NOTIFSETYET, true);
        editor.apply();

        Log.d(LOGTAG, "Alarm set for hours " + hours + " and minutes " + minutes);
    }

    static public void cancelNotificationAlarmIfNecessary(Context context) {
        // If there hasn't been a notification scheduled before, then nothing to do
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPreferences.getBoolean(Constants.KEY_NOTIFSETYET,
                Constants.DEFAULTVAL_NOTIFSETYET)) {
            return;
        }

        // Simply cancel the alarm by throwing the intent at it
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getAlarmIntent(context));

        // Finally, just make sure that everyone knows there isn't any alarms set right now
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_NOTIFSETYET, false);
        editor.commit();

        Log.d(LOGTAG, "Canceled alarm in cancelNotificationIfNecessary()");
    }

    static private PendingIntent getAlarmIntent(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
    }
}
