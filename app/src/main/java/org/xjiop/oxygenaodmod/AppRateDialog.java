package org.xjiop.oxygenaodmod;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

public class AppRateDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getContext();

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(getString(R.string.app_name));
        dialog.setMessage(getString(R.string.rate_app_text));

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.rate),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                        settings.edit().putInt("app_rate_count", -1).apply();
                        Helper.openLink(context, getString(R.string.app_google_play_link));
                    }
                });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no_thanks),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });

        return dialog;
    }
}
