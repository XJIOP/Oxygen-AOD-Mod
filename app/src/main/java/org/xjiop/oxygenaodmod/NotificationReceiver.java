package org.xjiop.oxygenaodmod;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import static org.xjiop.oxygenaodmod.Application.COLOR;
import static org.xjiop.oxygenaodmod.Application.ICON;
import static org.xjiop.oxygenaodmod.Application.AMOUNT;
import static org.xjiop.oxygenaodmod.Application.SHOW_NOTIFICATION_COUNTER;
import static org.xjiop.oxygenaodmod.NotificationService.NOTIFICATION_COUNT;
import static org.xjiop.oxygenaodmod.NotificationService.INDICATOR_COUNT;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "DBG | NReceiver";

    public static final int INDICATOR_NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG, "onReceive");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;

        String amount = context.getString(R.string.indicator);
        if (AMOUNT > 0)
            amount += " (" + INDICATOR_COUNT + " " + context.getString(R.string.of) + " " + AMOUNT + ")";
        else
            amount += " (" + INDICATOR_COUNT + ")";

        String text;
        if (SHOW_NOTIFICATION_COUNTER)
            text = context.getString(R.string.new_notifications) + ": " + NOTIFICATION_COUNT;
        else
            text = context.getString(R.string.you_have_new_notification);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Channel-1");
        mBuilder.setSmallIcon(ICON)
                .setContentTitle(amount)
                .setContentText(text)
                .setAutoCancel(true)
                .setTimeoutAfter(1000)
                .setColorized(COLOR > 0)
                .setColor(COLOR)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notificationManager.notify(INDICATOR_NOTIFICATION_ID, mBuilder.build());
    }
}
