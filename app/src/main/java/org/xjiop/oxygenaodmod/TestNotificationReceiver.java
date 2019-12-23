package org.xjiop.oxygenaodmod;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class TestNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "DBG | TNReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG, "onReceive");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager == null)
            return;

        Intent intentClick = new Intent(context, MainActivity.class);
        PendingIntent pendingClick = PendingIntent.getActivity(context, 1, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Channel-2");
        mBuilder.setSmallIcon(R.drawable.ic_test)
                .setContentTitle(context.getString(R.string.test))
                .setContentText(context.getString(R.string.hello) + "!")
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingClick);

        notificationManager.notify(135, mBuilder.build());
    }
}
