package edu.umich.engin.cm.onecoolthing.Notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import edu.umich.engin.cm.onecoolthing.Util.Constants;

/**
 * Created by jawad on 27/09/15.
 */
public class AlarmNotificationManager {
    static public void setNotificationAlarm(Activity activity, int hours, int minutes) {
        // First cancel previously planned notifications, if any
        cancelNotificationAlarmIfNecessary(activity);

        // Start the alarm for the notification at the requested time
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hours);
        startTime.set(Calendar.MINUTE, minutes);
        startTime.set(Calendar.SECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC, startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, getAlarmIntent(activity));

        // Remember that the alarm has been set up
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_NOTIFSETYET, true);
        editor.apply();
    }

    static public void cancelNotificationAlarmIfNecessary(Activity activity) {
        // If there hasn't been a notification scheduled before, then nothing to do
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean(Constants.KEY_NOTIFSETYET, false)) {
            return;
        }

        // Simply cancel the alarm by throwing the intent at it
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getAlarmIntent(activity));

        // Finally, just make sure that everyone knows there isn't any alarms set right now
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_NOTIFSETYET, false);
        editor.commit();
    }

    static private PendingIntent getAlarmIntent(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
    }
}
