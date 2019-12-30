package org.xjiop.oxygenaodmod;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import static org.xjiop.oxygenaodmod.Application.ALLOWED_CATEGORY;
import static org.xjiop.oxygenaodmod.Application.REMIND_AMOUNT;
import static org.xjiop.oxygenaodmod.Application.REMIND_INTERVAL;
import static org.xjiop.oxygenaodmod.Application.RESET_WHEN_SCREEN_TURN_ON;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "DBG | NService";

    public static NotificationService notificationService;
    public static int NOTIFICATION_COUNT;
    public static int REMINDER_COUNT;

    private ScreenPowerReceiver screenPowerReceiver;
    private Handler handler;
    private PowerManager powerManager;

    @Override
    public void onCreate() {
        //Log.d(TAG, "onCreate");
        super.onCreate();

        notificationService = this;

        handler = new Handler();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);

        screenPowerReceiver = new ScreenPowerReceiver();
        registerReceiver(screenPowerReceiver, screenStateFilter);
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy");

        NOTIFICATION_COUNT = 0;
        notificationService = null;

        if(screenPowerReceiver != null) {
            unregisterReceiver(screenPowerReceiver);
            screenPowerReceiver = null;
        }

        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        super.onDestroy();
    }

    @Override
    public void onListenerConnected() {
        //Log.d(TAG, "onListenerConnected");
    }

    @Override
    public void onListenerDisconnected() {
        //Log.d(TAG, "onListenerDisconnected");
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        //Log.d(TAG, "onNotificationPosted =  " + sbn);
        //Log.d(TAG, " - notification package = " + sbn.getPackageName());
        //Log.d(TAG, " - notification content = " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
        //Log.d(TAG, " - notification is ongoing = " + sbn.isOngoing());
        //Log.d(TAG, " - notification id = " + sbn.getId());
        //Log.d(TAG, " - notification category = " + sbn.getNotification().category);
        //Log.d(TAG, " - notification flags = " + sbn.getNotification().flags);
        //Log.d(TAG, " - notification key = " + sbn.getKey());

        if(newNotification(sbn))
            startReminder();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.d(TAG, "onNotificationRemoved = " + sbn);
        //Log.d(TAG, " - notification package = " + sbn.getPackageName());
        //Log.d(TAG, " - notification content = " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
        //Log.d(TAG, " - notification is ongoing = " + sbn.isOngoing());
        //Log.d(TAG, " - notification id = " + sbn.getId());
        //Log.d(TAG, " - notification category = " + sbn.getNotification().category);
        //Log.d(TAG, " - notification flags = " + sbn.getNotification().flags);
        //Log.d(TAG, " - notification key = " + sbn.getKey());

        // own reminder
        if(sbn.getId() == 123) {
            startReminder();
            return;
        }

        // ignore ongoing (this notifications cannot be dismissed)
        if(sbn.isOngoing())
            return;

        // ignore duplicate
        if((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Log.d(TAG, " - ignore this notification");
            return;
        }

        // category filter
        if(sbn.getNotification().category != null && Helper.blackList.contains(sbn.getNotification().category))
            return;

        // blacklist filter
        //if(sbn.getNotification().category != null && !ALLOWED_CATEGORY.contains(sbn.getNotification().category))
        //    return;

        if(NOTIFICATION_COUNT > 0) {
            NOTIFICATION_COUNT--;
            if(NOTIFICATION_COUNT == 0) {
                if(handler != null)
                    handler.removeCallbacksAndMessages(null);
            }
        }
    }

    public void startReminder() {
        //Log.d(TAG, "startReminder | NOTIFICATION_COUNT: " + NOTIFICATION_COUNT + " | REMINDER_COUNT: " + REMINDER_COUNT);

        if(NOTIFICATION_COUNT == 0)
            return;

        if(isRemindAmount())
            return;

        boolean isScreenOn = powerManager != null && powerManager.isInteractive();
        if(isScreenOn)
            return;

        if(handler != null) {
            REMINDER_COUNT++;
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                public void run() {
                    sendBroadcast(new Intent(NotificationService.this, NotificationReceiver.class));
                }
            }, REMIND_INTERVAL * 1000);
        }
    }

    public void stopReminder() {
        //Log.d(TAG, "stopAlert | NOTIFICATION_COUNT: " + NOTIFICATION_COUNT);

        NOTIFICATION_COUNT = 0;
        REMINDER_COUNT = 0;

        if(handler != null)
            handler.removeCallbacksAndMessages(null);
    }

    private boolean newNotification(StatusBarNotification sbn) {

        // own reminder
        if(sbn.getId() == 123)
            return false;

        // ignore ongoing (this notifications cannot be dismissed)
        if(sbn.isOngoing())
            return false;

        // ignore duplicate
        if((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Log.d(TAG, " - ignore this notification");
            return false;
        }

        // category filter
        if(sbn.getNotification().category != null && !ALLOWED_CATEGORY.contains(sbn.getNotification().category))
            return false;

        // blacklist filter
        //if(sbn.getNotification().category != null && Helper.blackList.contains(sbn.getNotification().category))
        //    return;

        NOTIFICATION_COUNT++;
        REMINDER_COUNT = 0;

        return true;
    }

    public void recountNotifications() {
        if(!RESET_WHEN_SCREEN_TURN_ON) {
            for (StatusBarNotification sbn : getActiveNotifications()) {
                newNotification(sbn);
            }
        }
    }

    private boolean isRemindAmount() {
        return REMIND_AMOUNT > 0 && REMINDER_COUNT == REMIND_AMOUNT;
    }
}
