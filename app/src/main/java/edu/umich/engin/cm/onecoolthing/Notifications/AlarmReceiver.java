package edu.umich.engin.cm.onecoolthing.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jawad on 27/09/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    AlarmNotificationManager alarmNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service1 = new Intent(context, AlarmService.class);
        context.startService(service1);
    }

}