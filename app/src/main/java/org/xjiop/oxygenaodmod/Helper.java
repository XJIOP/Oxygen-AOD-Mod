package org.xjiop.oxygenaodmod;

import android.app.Notification;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Arrays;
import java.util.List;

class Helper {

    static final List<String> categoryList = Arrays.asList(
            Notification.CATEGORY_ALARM,
            Notification.CATEGORY_CALL,
            Notification.CATEGORY_EMAIL,
            Notification.CATEGORY_EVENT,
            Notification.CATEGORY_MESSAGE,
            Notification.CATEGORY_REMINDER,
            Notification.CATEGORY_SOCIAL,
            Notification.CATEGORY_SYSTEM);

    static final List<String> blackList = Arrays.asList(
            Notification.CATEGORY_NAVIGATION,
            Notification.CATEGORY_PROMO,
            Notification.CATEGORY_PROGRESS,
            Notification.CATEGORY_SERVICE,
            Notification.CATEGORY_TRANSPORT,
            Notification.CATEGORY_RECOMMENDATION,
            Notification.CATEGORY_ERROR);

    static String categoryName(Context context, String category) {
        switch (category) {
            case "alarm":
                return context.getString(R.string.alarm);
            case "call":
                return context.getString(R.string.call);
            case "email":
                return context.getString(R.string.email);
            case "event":
                return context.getString(R.string.event);
            case "msg":
                return context.getString(R.string.msg);
            case "reminder":
                return context.getString(R.string.reminder);
            case "social":
                return context.getString(R.string.social);
            case "sys":
                return context.getString(R.string.sys);
            default:
                return null;
        }
    }

    static void showDialogFragment(Context context, final DialogFragment dialog) {

        AppCompatActivity activity = (AppCompatActivity) context;
        if(activity == null || activity.isFinishing())
            return;

        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if(fragmentManager.isStateSaved() || fragmentManager.isDestroyed())
            return;

        if(dialog.isAdded())
            return;

        final String tag = dialog.getClass().getName();
        if (fragmentManager.findFragmentByTag(tag) == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        dialog.show(fragmentManager, tag);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
