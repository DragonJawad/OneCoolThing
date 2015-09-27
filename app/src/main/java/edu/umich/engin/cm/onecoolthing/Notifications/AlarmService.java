package edu.umich.engin.cm.onecoolthing.Notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import edu.umich.engin.cm.onecoolthing.Core.ActivityMain;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 27/09/15.
 */
public class AlarmService extends IntentService {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager alarmNotificationManager;
    private PendingIntent pendingIntent;

    public AlarmService() {
        super("AlarmService");
    }

    public AlarmService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // don't notify if they've played in last 24 hr
        Context context = this.getApplicationContext();
        alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, ActivityMain.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(res.getString(R.string.notification_title))
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.notification_title))
                .setContentText(res.getString(R.string.notification_subject));

        alarmNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}