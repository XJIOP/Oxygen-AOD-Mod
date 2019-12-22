package org.xjiop.oxygenaodmod;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {

    public static boolean RESET_WHEN_SCREEN_TURN_ON;
    public static int REMIND_INTERVAL;
    public static List<String> ALLOWED_CATEGORY = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        defaultSettings();
    }

    private void defaultSettings() {

        /* TODO: SET DEFAULT SETTINGS */

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor edit = settings.edit();

        if(settings.getInt("version_code", -1) != BuildConfig.VERSION_CODE)
            edit.putInt("version_code", BuildConfig.VERSION_CODE);

        if(!settings.contains("categories"))
            edit.putStringSet("categories", new HashSet<>(Helper.categoryList));

        edit.apply();

        /* TODO: SET NOTIFICATION CHANNELS */

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {

            String channelId = "Channel-1";
            String channelName = getString(R.string.reminders);

            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setSound(null, null);
                notificationChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            channelId = "Channel-2";
            channelName = getString(R.string.test_notification);

            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        /* TODO: OTHERS */

        RESET_WHEN_SCREEN_TURN_ON = settings.getBoolean("reset_when_screen_turn_on", true);
        REMIND_INTERVAL = Integer.parseInt(settings.getString("remind_interval", "15"));
        ALLOWED_CATEGORY = new ArrayList<>(settings.getStringSet("categories", new HashSet<>(Helper.categoryList)));

        for(String s : ALLOWED_CATEGORY) {
            System.out.println("TEST | "+s);
        }

        if(!BuildConfig.DEBUG && settings.getBoolean("bug_tracking", true))
            Fabric.with(this, new Crashlytics());
    }
}
