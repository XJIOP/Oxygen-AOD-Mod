package org.xjiop.oxygenaodmod;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

import static org.xjiop.oxygenaodmod.Application.REMIND_INTERVAL;
import static org.xjiop.oxygenaodmod.Application.RESET_WHEN_SCREEN_TURN_ON;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "DBG | MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Log.d(TAG, "onSharedPreferenceChanged | key: " + key);

        switch (key) {
            case "reset_when_screen_turn_on":
                RESET_WHEN_SCREEN_TURN_ON = sharedPreferences.getBoolean("reset_when_screen_turn_on", true);
                break;

            case "bug_tracking":

                try {
                    Fabric.with(this, new Crashlytics.Builder()
                            .core(new CrashlyticsCore
                                    .Builder()
                                    .disabled(sharedPreferences.getBoolean("bug_tracking", true))
                                    .build())
                            .build());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Context mContext;
        private Preference doubleTapPreference;
        private Preference remindNotificationPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //Log.d(TAG, "onCreatePreferences | rootKey: " + rootKey);
            setPreferencesFromResource(R.xml.settings, rootKey);

            doubleTapPreference = findPreference("double_tap");
            remindNotificationPreference = findPreference("reminders");

            doubleTapPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, 101);

                    return false;
                }
            });

            remindNotificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivityForResult(intent, 102);

                    return false;
                }
            });

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);

            Preference remindIntervalPreference = findPreference("remind_interval");
            remindIntervalPreference.setSummary(settings.getString("remind_interval", "15") + " " + getString(R.string.sec));
            remindIntervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int value = 0;
                    try {
                        value = Integer.valueOf(newValue.toString());
                    }
                    catch(NumberFormatException ignored) {}

                    if(value < 5) {
                        Helper.showDialogFragment(mContext, MessageDialog.newInstance(mContext.getString(R.string.error), mContext.getString(R.string.min_5_sec)));
                        return false;
                    }

                    REMIND_INTERVAL = value;
                    preference.setSummary(newValue.toString() + " " + getString(R.string.sec));

                    return true;
                }
            });

            findPreference("categories").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Helper.showDialogFragment(mContext, new CategoriesDialog());
                    return false;
                }
            });

            findPreference("sound_settings").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, mContext.getPackageName());
                    startActivityForResult(intent, 103);

                    return false;
                }
            });

            findPreference("test_notification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Helper.showDialogFragment(mContext, ConfirmDialog.newInstance(
                            getString(R.string.test_reminder),
                            getString(R.string.test_reminder_confirm),
                            getString(R.string.yes),
                            getString(R.string.no),
                            new ConfirmDialog.ConfirmDialogInterface() {
                                @Override
                                public void onPositive() {

                                    Context applicationContext = mContext.getApplicationContext();

                                    Intent intent = new Intent(applicationContext, TestNotificationReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext, 321, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(ALARM_SERVICE);
                                    if(alarmManager != null) {
                                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (60 * 1000), pendingIntent);
                                        Helper.showDialogFragment(mContext, MessageDialog.newInstance(mContext.getString(R.string.confirmation), mContext.getString(R.string.test_reminder_descr)));
                                    }
                                }

                                @Override
                                public void onNegative() {}
                            }));

                    return true;
                }
            });

            findPreference("about_app").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Helper.showDialogFragment(mContext, new AboutAppDialog());
                    return true;
                }
            });
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);

            mContext = context;
        }

        @Override
        public void onResume() {
            super.onResume();

            if(doubleTapPreference != null)
                ((SwitchPreference) doubleTapPreference).setChecked(isAccessibility());

            if(remindNotificationPreference != null)
                ((SwitchPreference) remindNotificationPreference).setChecked(isNotification());
        }

        private boolean isAccessibility() {

            boolean result = false;

            int enabled = 0;
            try {
                enabled = Settings.Secure.getInt(mContext.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            }
            catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            if (enabled == 1) {
                String services = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (services != null) {
                    result = services.toLowerCase().contains(mContext.getPackageName().toLowerCase());
                }
            }

            return result;
        }

        private boolean isNotification() {
            ComponentName cn = new ComponentName(mContext, NotificationService.class);
            String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
            return flat != null && flat.contains(cn.flattenToString());
        }
    }
}