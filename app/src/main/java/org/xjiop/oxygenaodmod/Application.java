package org.xjiop.oxygenaodmod;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Application extends android.app.Application {

    public static boolean SINGLE_TAP;
    public static boolean VIBRATION;
    public static boolean RESET_WHEN_SCREEN_TURN_ON;
    public static int INTERVAL;
    public static boolean WAKE_LOCK;
    public static int AMOUNT;
    public static LocalTime START_TIME;
    public static LocalTime END_TIME;
    public static boolean ANY_TIME;
    public static int COLOR;
    public static int ICON;
    public static boolean SHOW_NOTIFICATION_COUNTER;
    public static boolean TURN_ON_SCREEN;
    public static List<String> ALLOWED_CATEGORY = new ArrayList<>();

    public static boolean isScreenON;
    private static ScreenPowerReceiver screenPowerReceiver;

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

        boolean updateCH = false;

        /* TODO: SET DEFAULT SETTINGS */

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor edit = settings.edit();

        int version = settings.getInt("version_code", -1);
        if (version != BuildConfig.VERSION_CODE) {

            if (version != -1) {

                if (version < 14) {

                    updateCH = true;

                    try {
                        Integer.parseInt(settings.getString("amount", "0"));
                    }
                    catch (NumberFormatException e) {
                        edit.putString("amount", "0");
                    }
                }
            }

            edit.putInt("version_code", BuildConfig.VERSION_CODE);
        }

        if (!settings.contains("categories"))
            edit.putStringSet("categories", new HashSet<>(Helper.categoryList));

        if (!settings.contains("start_time"))
            edit.putString("start_time", "08:00");

        if (!settings.contains("end_time"))
            edit.putString("end_time", "23:00");

        if (!settings.contains("any_time"))
            edit.putBoolean("any_time", true);

        if (!settings.contains("language"))  {

            // get system language
            String lang = Resources.getSystem().getConfiguration().getLocales().get(0).toString();
            String[] fixLang = lang.split("_");
            lang = fixLang[0];

            // set existing language
            String[] langList = getResources().getStringArray(R.array.listLangValues);
            if (!Arrays.asList(langList).contains(lang))
                lang = "en";

            edit.putString("language", lang);
        }

        edit.apply();

        /* TODO: SET NOTIFICATION CHANNELS */

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {

            String channelId = "Channel-1";
            String channelName = getString(R.string.notification_indicator);

            if (notificationManager.getNotificationChannel(channelId) == null || updateCH) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);
                notificationChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            channelId = "Channel-2";
            channelName = getString(R.string.test_notification);

            if (notificationManager.getNotificationChannel(channelId) == null || updateCH) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        /* TODO: PARAMS */

        SINGLE_TAP = settings.getBoolean("single_tap", false);
        VIBRATION = settings.getBoolean("vibration", false);
        RESET_WHEN_SCREEN_TURN_ON = settings.getBoolean("reset_when_screen_turn_on", true);
        INTERVAL = Integer.parseInt(settings.getString("interval", "15"));
        WAKE_LOCK = settings.getBoolean("wake_lock", false);
        AMOUNT = Integer.parseInt(settings.getString("amount", "0"));
        START_TIME = LocalTime.parse(settings.getString("start_time", "08:00"));
        END_TIME = LocalTime.parse(settings.getString("end_time", "23:00"));
        ANY_TIME = settings.getBoolean("any_time", true);
        COLOR = Helper.myColor(settings.getString("color", null));
        ICON = Helper.myIcon(settings.getString("icon", null));
        SHOW_NOTIFICATION_COUNTER = settings.getBoolean("show_notification_counter", false);
        TURN_ON_SCREEN = settings.getBoolean("turn_on_screen", false);
        ALLOWED_CATEGORY = new ArrayList<>(settings.getStringSet("categories", new HashSet<>(Helper.categoryList)));

        /* TODO: OTHERS */

        boolean enable = !BuildConfig.DEBUG && settings.getBoolean("bug_tracking", true);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(enable);
    }

    public void registerScreenPowerReceiver() {
        if (screenPowerReceiver == null) {
            IntentFilter screenStateFilter = new IntentFilter();
            screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
            screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
            screenPowerReceiver = new ScreenPowerReceiver();
            registerReceiver(screenPowerReceiver, screenStateFilter);
        }
    }

    public void unregisterScreenPowerReceiver() {
        if (screenPowerReceiver != null) {
            unregisterReceiver(screenPowerReceiver);
            screenPowerReceiver = null;
        }
    }
}
