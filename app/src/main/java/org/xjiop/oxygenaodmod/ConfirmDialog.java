package org.xjiop.oxygenaodmod;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE = "positive";
    private static final String ARG_NEGATIVE = "negative";

    private String title;
    private String message;
    private String positive;
    private String negative;

    private Context mContext;
    private ConfirmDialogInterface mListener;

    private ConfirmDialog(ConfirmDialogInterface listener) {
         mListener = listener;
    }

    static ConfirmDialog newInstance(String title, String message, String positive, String negative, ConfirmDialogInterface listener) {
        ConfirmDialog fragment = new ConfirmDialog(listener);
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE, positive);
        args.putString(ARG_NEGATIVE, negative);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        title = getArguments().getString(ARG_TITLE);
        message = getArguments().getString(ARG_MESSAGE);
        positive = getArguments().getString(ARG_POSITIVE);
        negative = getArguments().getString(ARG_NEGATIVE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(title);
        dialog.setMessage(message);

        dialog.setCanceledOnTouchOutside(false);

        String positiveButton = positive != null ? positive : getString(R.string.yes);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener != null)
                            mListener.onPositive();
                    }
                });

        String negativeButton = positive != null ? negative : getString(R.string.no);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener != null)
                            mListener.onNegative();
                    }
                });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

                    if(mListener != null)
                        mListener.onNegative();

                    dismiss();
                }
                return true;
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onDestroy() {
        mListener = null;

        super.onDestroy();
    }

    public interface ConfirmDialogInterface {
        void onPositive();
        void onNegative();
    }
}

