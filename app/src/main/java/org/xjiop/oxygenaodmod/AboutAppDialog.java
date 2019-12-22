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

    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(R.string.about_app);

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_about_app, null);
        dialog.setView(view);

        String ver = getString(R.string.version)+" "+ BuildConfig.VERSION_NAME;
        TextView version = view.findViewById(R.id.app_version);
        version.setText(ver);

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.close),
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
}
