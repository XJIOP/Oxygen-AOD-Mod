package org.xjiop.oxygenaodmod;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static org.xjiop.oxygenaodmod.Application.COLOR;
import static org.xjiop.oxygenaodmod.Application.ICON;
import static org.xjiop.oxygenaodmod.Application.REMIND_AMOUNT;
import static org.xjiop.oxygenaodmod.NotificationService.NOTIFICATION_COUNT;
import static org.xjiop.oxygenaodmod.NotificationService.REMINDER_COUNT;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "DBG | NReceiver";

    public static final int REMINDER_NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG, "onReceive");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager == null)
            return;

        String amount = context.getString(R.string.reminder);
        if(REMIND_AMOUNT > 0)
            amount += " (" + REMINDER_COUNT + " " + context.getString(R.string.of) + " " + REMIND_AMOUNT + ")";
        else
            amount += " (" + REMINDER_COUNT + ")";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Channel-1");
        mBuilder.setSmallIcon(ICON)
                .setContentTitle(amount)
                .setContentText(context.getString(R.string.new_notifications) + ": " + NOTIFICATION_COUNT)
                .setSound(null)
                .setAutoCancel(true)
                .setTimeoutAfter(1000)
                .setColorized(COLOR > 0)
                .setColor(COLOR)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notificationManager.notify(REMINDER_NOTIFICATION_ID, mBuilder.build());
    }
}
