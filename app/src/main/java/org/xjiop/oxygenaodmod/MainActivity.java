package org.xjiop.oxygenaodmod;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.xjiop.oxygenaodmod.category.CategoriesDialog;
import org.xjiop.oxygenaodmod.icon.IconDialog;

import io.fabric.sdk.android.Fabric;

import static org.xjiop.oxygenaodmod.Application.COLOR;
import static org.xjiop.oxygenaodmod.Application.AMOUNT;
import static org.xjiop.oxygenaodmod.Application.INTERVAL;
import static org.xjiop.oxygenaodmod.Application.SHOW_NOTIFICATION_COUNTER;
import static org.xjiop.oxygenaodmod.Application.WAKE_LOCK;
import static org.xjiop.oxygenaodmod.Application.RESET_WHEN_SCREEN_TURN_ON;
import static org.xjiop.oxygenaodmod.Application.VIBRATION;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "DBG | MainActivity";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        if(savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    int app_count = settings.getInt("app_rate_count", 0);
                    if (app_count != -1) {
                        app_count += 1;
                        if (app_count % 5 == 0) {
                            app_count = 0;
                            Helper.showDialogFragment(MainActivity.this, new AppRateDialog());
                        }
                        settings.edit().putInt("app_rate_count", app_count).apply();
                    }
                }
            });
        }
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
            case "language":

                recreate();

                break;

            case "reset_when_screen_turn_on":

                RESET_WHEN_SCREEN_TURN_ON = sharedPreferences.getBoolean("reset_when_screen_turn_on", true);

                break;

            case "show_notification_counter":

                SHOW_NOTIFICATION_COUNTER = sharedPreferences.getBoolean("show_notification_counter", false);

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

            case "wake_lock":

                WAKE_LOCK = sharedPreferences.getBoolean("wake_lock", true);

                break;

            case "vibration":

                VIBRATION = sharedPreferences.getBoolean("vibration", true);

                break;
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private static final int IGNORE_RESULT = 0;
        private static final int SCHEDULE_RESULT = 1;
        private static final int ICON_RESULT = 2;
        private static final int TEST_RESULT = 3;

        private Context mContext;

        private Preference doubleTapPreference;
        private Preference indicatorPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //Log.d(TAG, "onCreatePreferences | rootKey: " + rootKey);
            setPreferencesFromResource(R.xml.settings, rootKey);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);

            setListPreferenceSummary(findPreference("language"), settings.getString("language", "en"));

            doubleTapPreference = findPreference("double_tap");
            indicatorPreference = findPreference("indicator");

            doubleTapPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, IGNORE_RESULT);

                    return false;
                }
            });

            indicatorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivityForResult(intent, IGNORE_RESULT);

                    return false;
                }
            });

            Preference intervalPreference = findPreference("interval");
            intervalPreference.setSummary(settings.getString("interval", "15") + " " + getString(R.string.sec));
            intervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int value = 0;
                    try {
                        value = Integer.valueOf(newValue.toString());
                    }
                    catch(NumberFormatException ignored) {}

                    if(value < 10) {
                        Helper.showDialogFragment(mContext, MessageDialog.newInstance(getString(R.string.error), getString(R.string.min_interval_sec)));
                        return false;
                    }

                    INTERVAL = value;
                    preference.setSummary(newValue.toString() + " " + getString(R.string.sec));

                    Helper.showDialogFragment(mContext, MessageDialog.newInstance(getString(R.string.attention), getString(R.string.interval_info)));

                    return true;
                }
            });

            String amount = settings.getString("amount", "0");
            Preference amountPreference = findPreference("amount");
            amountPreference.setSummary(amount.equals("0") ? getString(R.string.no_limits) : amount);
            amountPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int value = -1;
                    try {
                        value = Integer.valueOf(newValue.toString());
                    }
                    catch(NumberFormatException ignored) {}

                    if(value == -1) {
                        Helper.showDialogFragment(mContext, MessageDialog.newInstance(getString(R.string.error), getString(R.string.enter_number)));
                        return false;
                    }

                    AMOUNT = value;
                    String text = value == 0 ? getString(R.string.no_limits) : newValue.toString();
                    preference.setSummary(text);

                    return true;
                }
            });

            String scheduleSummary;
            if(settings.getBoolean("any_time", true)) {
                scheduleSummary = getString(R.string.any_time);
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

            Preference colorPreference = findPreference("color");
            setListPreferenceSummary(colorPreference, settings.getString("color", null));
            colorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    setListPreferenceSummary(preference, newValue.toString());
                    COLOR = Helper.myColor(newValue.toString());

                    return true;
                }
            });

            Preference iconPreference = findPreference("icon");
            iconPreference.setSummary(Helper.iconName(mContext, settings.getString("icon", "warning")));
            iconPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    IconDialog iconDialog = new IconDialog();
                    iconDialog.setTargetFragment(SettingsFragment.this, ICON_RESULT);
                    Helper.showDialogFragment(mContext, iconDialog);

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
                            getString(R.string.notification_indicator_test),
                            getString(R.string.test_notification_confirm),
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
                ((SwitchPreference) doubleTapPreference).setChecked(Helper.isAccessibilityPermission());

            if(indicatorPreference != null)
                ((SwitchPreference) indicatorPreference).setChecked(Helper.isNotificationPermission());
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
                else if(requestCode == ICON_RESULT) {
                    String summary = data.getStringExtra("summary");
                    if(summary != null) {
                        findPreference("icon").setSummary(summary);
                    }
                }
                else if(requestCode == TEST_RESULT) {

                    if(!data.getBooleanExtra("onPositive", false))
                        return;

                    Context applicationContext = Application.getAppContext();

                    Intent intent = new Intent(applicationContext, TestNotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(ALARM_SERVICE);
                    if(alarmManager != null) {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (60 * 1000), pendingIntent);
                        Helper.showDialogFragment(mContext, MessageDialog.newInstance(getString(R.string.confirmation), getString(R.string.test_notification_descr)));
                    }
                }
            }
        }

        private void setListPreferenceSummary(Preference preference, String value) {

            if(value != null) {
                if(preference instanceof ListPreference) {

                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(value);

                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                }
            }
        }
    }
}