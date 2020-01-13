package org.xjiop.oxygenaodmod;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {

    public static boolean VIBRATION;
    public static boolean RESET_WHEN_SCREEN_TURN_ON;
    public static int REMIND_INTERVAL;
    public static boolean REMIND_WAKE_LOCK;
    public static int REMIND_AMOUNT;
    public static LocalTime START_TIME;
    public static LocalTime END_TIME;
    public static boolean ANY_TIME;
    public static int COLOR;
    public static int ICON;
    public static List<String> ALLOWED_CATEGORY = new ArrayList<>();

    private static Application appInstance;

    public Application() {
        appInstance = this;
    }

    public static Application getAppContext() {
        return appInstance;
    }

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

        int version = settings.getInt("version_code", -1);
        if(version != BuildConfig.VERSION_CODE) {
            edit.putInt("version_code", BuildConfig.VERSION_CODE);
        }

        if(!settings.contains("categories"))
            edit.putStringSet("categories", new HashSet<>(Helper.categoryList));

        if(!settings.contains("start_time"))
            edit.putString("start_time", "08:00");

        if(!settings.contains("end_time"))
            edit.putString("end_time", "23:00");

        if(!settings.contains("any_time"))
            edit.putBoolean("any_time", true);

        edit.apply();

        /* TODO: SET NOTIFICATION CHANNELS */

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {

            String channelId = "Channel-1";
            String channelName = getString(R.string.reminders);

            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);
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

        /* TODO: PARAMS */

        VIBRATION = settings.getBoolean("vibration", false);
        RESET_WHEN_SCREEN_TURN_ON = settings.getBoolean("reset_when_screen_turn_on", true);
        REMIND_INTERVAL = Integer.parseInt(settings.getString("remind_interval", "15"));
        REMIND_WAKE_LOCK = settings.getBoolean("remind_wake_lock", false);
        REMIND_AMOUNT = Integer.parseInt(settings.getString("remind_amount", "0"));
        START_TIME = LocalTime.parse(settings.getString("start_time", "08:00"));
        END_TIME = LocalTime.parse(settings.getString("end_time", "23:00"));
        ANY_TIME = settings.getBoolean("any_time", true);
        COLOR = Helper.myColor(settings.getString("color", null));
        ICON = Helper.myIcon(settings.getString("icon", null));
        ALLOWED_CATEGORY = new ArrayList<>(settings.getStringSet("categories", new HashSet<>(Helper.categoryList)));

        /* TODO: OTHERS */

        if(!BuildConfig.DEBUG && settings.getBoolean("bug_tracking", true))
            Fabric.with(this, new Crashlytics());
    }
}
