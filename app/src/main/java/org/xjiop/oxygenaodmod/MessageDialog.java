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

public class MessageDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_TEXT = "text";

    private String title;
    private String text;

    static MessageDialog newInstance(String title, String text) {
        MessageDialog fragment = new MessageDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        title = getArguments().getString(ARG_TITLE);
        text = getArguments().getString(ARG_TEXT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getContext();

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message, null);

        TextView textView = view.findViewById(R.id.message);
        textView.setText(text);

        dialog.setView(view);

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return dialog;
    }
}
