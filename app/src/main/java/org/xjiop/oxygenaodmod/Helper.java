package org.xjiop.oxygenaodmod;

import android.app.Notification;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Helper {

    static final List<String> categoryList = Arrays.asList(
            Notification.CATEGORY_ALARM,
            Notification.CATEGORY_CALL,
            Notification.CATEGORY_EMAIL,
            Notification.CATEGORY_EVENT,
            Notification.CATEGORY_MESSAGE,
            Notification.CATEGORY_REMINDER,
            Notification.CATEGORY_SOCIAL);

    static final List<String> blackList = Arrays.asList(
            Notification.CATEGORY_ERROR,
            Notification.CATEGORY_NAVIGATION,
            Notification.CATEGORY_PROMO,
            Notification.CATEGORY_PROGRESS,
            Notification.CATEGORY_RECOMMENDATION,
            Notification.CATEGORY_SERVICE,
            Notification.CATEGORY_SYSTEM,
            Notification.CATEGORY_TRANSPORT);

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

    private static final SimpleDateFormat[] timeFormat = {
            new SimpleDateFormat("hh:mm a", Locale.US),
            new SimpleDateFormat("h:mm a", Locale.US),
            new SimpleDateFormat("HH:mm", Locale.US),
            new SimpleDateFormat("H:mm", Locale.US)
    };

    static String parseTime(String time) {

        Date date = new Date();
        for (SimpleDateFormat f : timeFormat) {
            try {
                date = f.parse(time);
                break;
            } catch (ParseException ignored) {}
        }

        return timeFormat[2].format(date);
    }

    static String localTimeFormat(Context context, String time) {

        Date date = new Date();
        for (SimpleDateFormat f : timeFormat) {
            try {
                date = f.parse(time);
                break;
            } catch (ParseException ignored) {}
        }

        return timeFormat[android.text.format.DateFormat.is24HourFormat(context) ? 2 : 1].format(date);
    }
}
