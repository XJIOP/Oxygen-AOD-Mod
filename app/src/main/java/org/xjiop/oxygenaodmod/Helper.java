package org.xjiop.oxygenaodmod;

import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Helper {

    public static final List<String> categoryList = Arrays.asList(
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

    private static final SimpleDateFormat[] timeFormat = {
            new SimpleDateFormat("hh:mm a", Locale.US),
            new SimpleDateFormat("h:mm a", Locale.US),
            new SimpleDateFormat("HH:mm", Locale.US),
            new SimpleDateFormat("H:mm", Locale.US)
    };

    public static Map<String, Integer> iconList() {
        Map<String, Integer> arr = new HashMap<>();
        arr.put("alarm", R.drawable.ic_alarm);
        arr.put("android", R.drawable.ic_android);
        arr.put("eye", R.drawable.ic_eye);
        arr.put("info", R.drawable.ic_info);
        arr.put("star", R.drawable.ic_star);
        arr.put("warning", R.drawable.ic_warning);
        return arr;
    }

    public static String categoryName(Context context, String category) {
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

    public static String iconName(Context context, String icon) {
        switch (icon) {
            case "alarm":
                return context.getString(R.string.alarm);
            case "android":
                return context.getString(R.string.android);
            case "eye":
                return context.getString(R.string.eye);
            case "info":
                return context.getString(R.string.info);
            case "star":
                return context.getString(R.string.star);
            case "warning":
                return context.getString(R.string.warning);
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

    static void openLink(Context context, String link) {

        if(link == null || link.isEmpty())
            return;

        Uri uri = Uri.parse(link);
        if(uri != null) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));

            try {
                context.startActivity(intent);
            }
            catch (Exception e) {
                Toast.makeText(context, R.string.cant_open_link, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    static void shareLink(Context context, String link, String title) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, link);

        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
        }
        catch (ActivityNotFoundException an) {
            Toast.makeText(context, R.string.cant_open_link, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    static int myColor(String color) {

        if(color == null)
            return Color.RED;

        switch (color) {
            case "0":
                return 0;
            case "2":
                return Color.GREEN;
            case "3":
                return Color.BLUE;
            case "4":
                return Color.YELLOW;
            default:
                return Color.RED;
        }
    }

    static int myIcon(String icon) {

        if(icon == null)
            return R.drawable.ic_warning;

        switch (icon) {
            case "alarm":
                return R.drawable.ic_alarm;
            case "android":
                return R.drawable.ic_android;
            case "eye":
                return R.drawable.ic_eye;
            case "info":
                return R.drawable.ic_info;
            case "star":
                return R.drawable.ic_star;
            default:
                return R.drawable.ic_warning;
        }
    }
}
