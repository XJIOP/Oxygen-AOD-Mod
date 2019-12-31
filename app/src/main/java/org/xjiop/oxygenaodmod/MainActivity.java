package org.xjiop.oxygenaodmod;

import android.app.Activity;
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

import static org.xjiop.oxygenaodmod.Application.REMIND_AMOUNT;
import static org.xjiop.oxygenaodmod.Application.REMIND_INTERVAL;
import static org.xjiop.oxygenaodmod.Application.RESET_WHEN_SCREEN_TURN_ON;
import static org.xjiop.oxygenaodmod.NotificationService.NOTIFICATION_COUNT;

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

                 if(RESET_WHEN_SCREEN_TURN_ON)
                    NOTIFICATION_COUNT = 0;

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

        private static int IGNORE_RESULT = 0;
        private static int SCHEDULE_RESULT = 1;
        private static int TEST_RESULT = 2;

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
                    startActivityForResult(intent, IGNORE_RESULT);

                    return false;
                }
            });

            remindNotificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivityForResult(intent, IGNORE_RESULT);

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

                    if(value < 10) {
                        Helper.showDialogFragment(mContext, MessageDialog.newInstance(mContext.getString(R.string.error), mContext.getString(R.string.min_interval_sec)));
                        return false;
                    }

                    REMIND_INTERVAL = value;
                    preference.setSummary(newValue.toString() + " " + getString(R.string.sec));

                    return true;
                }
            });

            String remind_amount = settings.getString("remind_amount", "0");
            Preference remindAmountPreference = findPreference("remind_amount");
            remindAmountPreference.setSummary(remind_amount.equals("0") ? getString(R.string.no_limits) : remind_amount);
            remindAmountPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int value = 0;
                    try {
                        value = Integer.valueOf(newValue.toString());
                    }
                    catch(NumberFormatException ignored) {}

                    REMIND_AMOUNT = value;
                    String text = value == 0 ? getString(R.string.no_limits) : newValue.toString();
                    preference.setSummary(text);

                    return true;
                }
            });

            String scheduleSummary;
            if(settings.getBoolean("any_time", true)) {
                scheduleSummary = mContext.getString(R.string.any_time);
            }
            else {
                String time1 = Helper.localTimeFormat(mContext, settings.getString("start_time", "08:00"));
                String time2 = Helper.localTimeFormat(mContext, settings.getString("end_time", "23:00"));
                scheduleSummary = time1 + " - " + time2;
            }
            Preference schedulePreference = findPreference("schedule");
            schedulePreference.setSummary(scheduleSummary);
            schedulePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {

                    TimeTabsDialog timeTabsDialog = new TimeTabsDialog();
                    timeTabsDialog.setTargetFragment(SettingsFragment.this, SCHEDULE_RESULT);
                    Helper.showDialogFragment(mContext, timeTabsDialog);

                    return false;
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
                    startActivityForResult(intent, IGNORE_RESULT);

                    return false;
                }
            });

            findPreference("test_notification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    ConfirmDialog confirmDialog = ConfirmDialog.newInstance(
                            getString(R.string.test_reminder),
                            getString(R.string.test_reminder_confirm),
                            getString(R.string.yes),
                            getString(R.string.no));
                    confirmDialog.setTargetFragment(SettingsFragment.this, TEST_RESULT);
                    Helper.showDialogFragment(mContext, confirmDialog);

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
                ((SwitchPreference) doubleTapPreference).setChecked(isAccessibilityPermission());

            if(remindNotificationPreference != null)
                ((SwitchPreference) remindNotificationPreference).setChecked(isNotificationPermission());
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Log.d(TAG, "onActivityResult | requestCode: " + requestCode + " | resultCode: " + resultCode + " | data: "+data);
            super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == Activity.RESULT_OK) {

                if(requestCode == SCHEDULE_RESULT) {
                    String summary = data.getStringExtra("summary");
                    if(summary != null) {
                        findPreference("schedule").setSummary(summary);
                    }
                }
                else if(requestCode == TEST_RESULT) {

                    Context applicationContext = mContext.getApplicationContext();

                    Intent intent = new Intent(applicationContext, TestNotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext, 321, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(ALARM_SERVICE);
                    if(alarmManager != null) {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (60 * 1000), pendingIntent);
                        Helper.showDialogFragment(mContext, MessageDialog.newInstance(mContext.getString(R.string.confirmation), mContext.getString(R.string.test_reminder_descr)));
                    }
                }
            }
        }

        private boolean isAccessibilityPermission() {

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

        private boolean isNotificationPermission() {
            ComponentName cn = new ComponentName(mContext, NotificationService.class);
            String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
            return flat != null && flat.contains(cn.flattenToString());
        }
    }
}