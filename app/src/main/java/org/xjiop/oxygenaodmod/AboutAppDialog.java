package org.xjiop.oxygenaodmod;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AboutAppDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getContext();

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(R.string.about_app);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_about_app, null);
        dialog.setView(view);

        String ver = getString(R.string.version) + " " + BuildConfig.VERSION_NAME;
        TextView version = view.findViewById(R.id.app_version);
        version.setText(ver);

        view.findViewById(R.id.source_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.openLink(context, getString(R.string.source_code_link));
            }
        });

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        dialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, getString(R.string.share),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.shareLink(context, getString(R.string.app_google_play_link), getString(R.string.app_name));
                        dismiss();
                    }
                });

        return dialog;
    }
}
