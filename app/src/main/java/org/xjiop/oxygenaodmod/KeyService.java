package org.xjiop.oxygenaodmod;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import static org.xjiop.oxygenaodmod.Application.SINGLE_TAP;
import static org.xjiop.oxygenaodmod.Application.VIBRATION;
import static org.xjiop.oxygenaodmod.Application.isScreenON;

import androidx.annotation.NonNull;

public class KeyService extends AccessibilityService {

    private final String TAG = "DBG | KeyService";

    private final int CLICK_THRESHOLD = 250;
    private long CLICK_DELAY;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        //Log.d(TAG, "onCreate");
        super.onCreate();

        Application.getAppContext().registerScreenPowerReceiver();

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, getPackageName() + ":double_tap");
            wakeLock.setReferenceCounted(false);
        }
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy");

        if (wakeLock != null && wakeLock.isHeld())
            wakeLock.release();

        if (!Helper.isNotificationPermission())
            Application.getAppContext().unregisterScreenPowerReceiver();

        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.d(TAG, "onAccessibilityEvent: " + event);
    }

    @Override
    public void onInterrupt() {
        //Log.d(TAG, "onInterrupt");
    }

    @Override
    public boolean onGesture(@NonNull AccessibilityGestureEvent gestureEvent) {
        //Log.d(TAG, "onGesture: " + gestureEvent);
        return super.onGesture(gestureEvent);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        //Log.d(TAG, "onKeyEvent: " + event);

        boolean result = false;

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_F4 && isScreenOff()) {
            result = (SINGLE_TAP || doubleClick());
            if (result) {

                if (wakeLock != null && !wakeLock.isHeld()) {

                    if (VIBRATION) {
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null) {
                            vibrator.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                    }

                    try {
                        wakeLock.acquire(1);
                    }
                    finally {
                        wakeLock.release();
                    }
                }
            }
        }

        return result || super.onKeyEvent(event);
    }

    private boolean doubleClick() {

        boolean result = false;

        long thisTime = System.currentTimeMillis();
        if ((thisTime - CLICK_DELAY) < CLICK_THRESHOLD) {
            //Log.d(TAG, "doubleClick");

            CLICK_DELAY = -1;
            result = true;
        }
        else {
            CLICK_DELAY = thisTime;
        }

        return result;
    }

    private boolean isScreenOff() {
        return !isScreenON && powerManager != null && !powerManager.isInteractive();
    }
}
