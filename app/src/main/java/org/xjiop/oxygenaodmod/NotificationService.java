package org.xjiop.oxygenaodmod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.time.LocalTime;

import static org.xjiop.oxygenaodmod.Application.ALLOWED_CATEGORY;
import static org.xjiop.oxygenaodmod.Application.AMOUNT;
import static org.xjiop.oxygenaodmod.Application.ANY_TIME;
import static org.xjiop.oxygenaodmod.Application.END_TIME;
import static org.xjiop.oxygenaodmod.Application.INTERVAL;
import static org.xjiop.oxygenaodmod.Application.RESET_WHEN_SCREEN_TURN_ON;
import static org.xjiop.oxygenaodmod.Application.START_TIME;
import static org.xjiop.oxygenaodmod.Application.TURN_ON_SCREEN;
import static org.xjiop.oxygenaodmod.Application.WAKE_LOCK;
import static org.xjiop.oxygenaodmod.Application.isScreenON;
import static org.xjiop.oxygenaodmod.NotificationReceiver.INDICATOR_NOTIFICATION_ID;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "DBG | NService";

    public static NotificationService notificationService;
    public static int NOTIFICATION_COUNT;
    public static int INDICATOR_COUNT;

    private final Handler handler = new Handler();
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private PowerManager.WakeLock wakeLockTos;
    private NotificationChannel notificationChannel;

    @Override
    public void onCreate() {
        //Log.d(TAG, "onCreate");
        super.onCreate();

        NOTIFICATION_COUNT = 0;
        INDICATOR_COUNT = 0;

        notificationService = this;

        Application.getAppContext().registerScreenPowerReceiver();

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getPackageName() + ":indicator");
            wakeLock.setReferenceCounted(false);

            wakeLockTos = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, getPackageName() + ":turn_on_screen");
            wakeLockTos.setReferenceCounted(false);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationChannel = notificationManager.getNotificationChannel("Channel-1");
        }
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy");

        NOTIFICATION_COUNT = 0;
        INDICATOR_COUNT = 0;

        handler.removeCallbacksAndMessages(null);

        notificationService = null;
        notificationChannel = null;

        if (wakeLock != null && wakeLock.isHeld())
            wakeLock.release();

        if (wakeLockTos != null && wakeLockTos.isHeld())
            wakeLockTos.release();

        if (!Helper.isAccessibilityPermission())
            Application.getAppContext().unregisterScreenPowerReceiver();

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
    public void onNotificationPosted(StatusBarNotification sbn) {
        //Log.d(TAG, "onNotificationPosted =  " + sbn);
        //Log.d(TAG, " - notification package = " + sbn.getPackageName());
        //Log.d(TAG, " - notification content = " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
        //Log.d(TAG, " - notification is ongoing = " + sbn.isOngoing());
        //Log.d(TAG, " - notification id = " + sbn.getId());
        //Log.d(TAG, " - notification category = " + sbn.getNotification().category);
        //Log.d(TAG, " - notification flags = " + sbn.getNotification().flags);
        //Log.d(TAG, " - notification key = " + sbn.getKey());

        if (newNotification(sbn))
            startIndicator();
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

        if (sbn == null)
            return;

        // prepare next indicator
        if (sbn.getId() == INDICATOR_NOTIFICATION_ID) {
            startIndicator();
            return;
        }

        // ignore ongoing (this notifications cannot be dismissed)
        if (sbn.isOngoing())
            return;

        // ignore duplicate
        if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Log.d(TAG, " - ignore this notification");
            return;
        }

        // category filter
        String cat = sbn.getNotification().category == null ? "undefined" : sbn.getNotification().category;
        if (!ALLOWED_CATEGORY.contains(cat))
            return;

        if (NOTIFICATION_COUNT > 0) {

            NOTIFICATION_COUNT--;

            if (WAKE_LOCK && wakeLock != null && wakeLock.isHeld())
                wakeLock.release();

            if (NOTIFICATION_COUNT == 0) {
                handler.removeCallbacksAndMessages(null);
            }
        }
    }

    public void startIndicator() {
        //Log.d(TAG, "startIndicator | NOTIFICATION_COUNT: " + NOTIFICATION_COUNT + " | INDICATOR_COUNT: " + INDICATOR_COUNT);
        //Log.d(TAG, " - wakeLock isHeld: " + wakeLock.isHeld());

        if (NOTIFICATION_COUNT == 0)
            return;

        if (isAmount())
            return;

        if (!ANY_TIME) {
            LocalTime now = LocalTime.now();
            if (now.isBefore(START_TIME) && now.isAfter(END_TIME)) {
                //Log.d(TAG, " - stopped by schedule");
                return;
            }
        }

        if (!isScreenOff())
            return;

        INDICATOR_COUNT++;

        if (WAKE_LOCK && wakeLock != null && !wakeLock.isHeld())
            wakeLock.acquire((INTERVAL + 5) * 1000L);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            public void run() {
                if (NOTIFICATION_COUNT > 0) {
                    if (TURN_ON_SCREEN) {

                        Uri uri = null;
                        if (notificationChannel != null) {
                            uri = notificationChannel.getSound();
                        }

                        try {
                            if (uri != null) {
                                Ringtone ringtone = RingtoneManager.getRingtone(NotificationService.this, uri);
                                ringtone.play();
                            }
                        }
                        catch (Exception ignored) {}

                        if (wakeLockTos != null && !wakeLockTos.isHeld()) {
                            try {
                                wakeLockTos.acquire(1);
                            }
                            finally {
                                wakeLockTos.release();
                            }
                        }
                    }
                    else {
                        sendBroadcast(new Intent(NotificationService.this, NotificationReceiver.class));
                    }
                }
            }
        }, INTERVAL * 1000L);
    }

    public void stopIndicator() {
        //Log.d(TAG, "stopAlert | NOTIFICATION_COUNT: " + NOTIFICATION_COUNT);
        //Log.d(TAG, " - wakeLock isHeld: " + wakeLock.isHeld());

        NOTIFICATION_COUNT = 0;
        INDICATOR_COUNT = 0;

        if (WAKE_LOCK && wakeLock != null && wakeLock.isHeld())
            wakeLock.release();

        handler.removeCallbacksAndMessages(null);
    }

    private boolean newNotification(StatusBarNotification sbn) {

        if (sbn == null)
            return false;

        // ignore indicator
        if (sbn.getId() == INDICATOR_NOTIFICATION_ID)
            return false;

        // ignore ongoing (this notifications cannot be dismissed)
        if (sbn.isOngoing())
            return false;

        // ignore duplicate
        if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Log.d(TAG, " - ignore this notification");
            return false;
        }

        // category filter
        String cat = sbn.getNotification().category == null ? "undefined" : sbn.getNotification().category;
        if (!ALLOWED_CATEGORY.contains(cat))
            return false;

        NOTIFICATION_COUNT++;
        INDICATOR_COUNT = 0;

        return true;
    }

    public void recountNotifications() {

        NOTIFICATION_COUNT = 0;
        INDICATOR_COUNT = 0;

        if (!RESET_WHEN_SCREEN_TURN_ON || TURN_ON_SCREEN) {
            for (StatusBarNotification sbn : getActiveNotifications()) {
                newNotification(sbn);
            }
        }
    }

    private boolean isAmount() {
        return AMOUNT > 0 && INDICATOR_COUNT == AMOUNT;
    }

    private boolean isScreenOff() {
        return !isScreenON && powerManager != null && !powerManager.isInteractive();
    }
}
