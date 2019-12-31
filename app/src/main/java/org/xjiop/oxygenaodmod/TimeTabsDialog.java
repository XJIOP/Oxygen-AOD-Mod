package org.xjiop.oxygenaodmod;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.time.LocalTime;

import static org.xjiop.oxygenaodmod.Application.ANY_TIME;
import static org.xjiop.oxygenaodmod.Application.END_TIME;
import static org.xjiop.oxygenaodmod.Application.START_TIME;

public class TimeTabsDialog extends DialogFragment {

    private static final String TAG = "DBG | TimeTabsDialog";

    private TimePicker timePicker1;
    private TimePicker timePicker2;
    private String timeValues1;
    private String timeValues2;

    private SharedPreferences settings;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(mContext);

        timeValues1 = settings.getString("start_time", "08:00");
        timeValues2 = settings.getString("end_time", "23:00");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        final View view = createTimeViews(dialog.getLayoutInflater());
        dialog.setView(view);

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String[] result = getResult();
                        boolean anyTime = ((CheckBox) view.findViewById(R.id.checkbox)).isChecked();

                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString("start_time", result[0]);
                        edit.putString("end_time", result[1]);
                        edit.putBoolean("any_time", anyTime);
                        edit.apply();

                        START_TIME = LocalTime.parse(result[0]);
                        END_TIME = LocalTime.parse(result[1]);
                        ANY_TIME = anyTime;

                        String summary;
                        if(anyTime) {
                            summary = getString(R.string.any_time);
                        }
                        else {
                            String time1 = Helper.localTimeFormat(mContext, result[0]);
                            String time2 = Helper.localTimeFormat(mContext, result[1]);
                            summary = time1 + " - " + time2;
                        }

                        Fragment targetFragment = getTargetFragment();
                        if(targetFragment != null) {
                            Intent intent = new Intent();
                            intent.putExtra("summary", summary);
                            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context;
    }

    private View createTimeViews(LayoutInflater layoutInflater) {

        View view = layoutInflater.inflate(R.layout.dialog_time_tabs, null);

        TabHost tabHost = view.findViewById(R.id.tab_host);
        tabHost.setup();

        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator(getString(R.string.start));
        tabSpec1.setContent(R.id.tab1);
        tabHost.addTab(tabSpec1);

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator(getString(R.string.end));
        tabSpec2.setContent(R.id.tab2);
        tabHost.addTab(tabSpec2);

        timePicker1 = view.findViewById(R.id.timePicker1);
        timePicker2 = view.findViewById(R.id.timePicker2);

        if(DateFormat.is24HourFormat(mContext)) {
            timePicker1.setIs24HourView(true);
            timePicker2.setIs24HourView(true);
        }

        String parse1 = Helper.parseTime(timeValues1);
        String parse2 = Helper.parseTime(timeValues2);

        String[] time1 = parse1.split(":");
        String[] time2 = parse2.split(":");

        timePicker1.setHour(Integer.valueOf(time1[0]));
        timePicker1.setMinute(Integer.valueOf(time1[1]));

        timePicker2.setHour(Integer.valueOf(time2[0]));
        timePicker2.setMinute(Integer.valueOf(time2[1]));

        ((CheckBox) view.findViewById(R.id.checkbox)).setChecked(settings.getBoolean("any_time", true));

        return view;
    }

    private String[] getResult() {

        String[] result = new String[2];

        timeValues1 = timePicker1.getHour() + ":" + timePicker1.getMinute();
        timeValues2 = timePicker2.getHour() + ":" + timePicker2.getMinute();

        result[0] = Helper.parseTime(timeValues1);
        result[1] = Helper.parseTime(timeValues2);

        return result;
    }
}
