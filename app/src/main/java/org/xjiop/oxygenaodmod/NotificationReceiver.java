package org.xjiop.oxygenaodmod;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static org.xjiop.oxygenaodmod.NotificationService.NOTIFICATION_COUNT;
import static org.xjiop.oxygenaodmod.NotificationService.notificationService;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "DBG | NReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG, "onReceive");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager == null)
            return;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Channel-1");
        mBuilder.setSmallIcon(R.drawable.ic_info)
                .setContentTitle(context.getString(R.string.reminder))
                .setContentText(context.getString(R.string.new_notifications) + ": " + NOTIFICATION_COUNT)
                .setSound(null)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        notificationManager.notify(123, mBuilder.build());

        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(NOTIFICATION_COUNT > 0)
            notificationManager.cancel(123);

        if(notificationService != null)
            notificationService.startReminder();
    }
}
