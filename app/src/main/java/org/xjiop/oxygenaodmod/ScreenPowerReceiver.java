package org.xjiop.oxygenaodmod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static org.xjiop.oxygenaodmod.NotificationService.notificationService;

public class ScreenPowerReceiver extends BroadcastReceiver {

    private static final String TAG = "DBG | SPReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(action != null) {
            switch (action) {
                case Intent.ACTION_SCREEN_ON:
                    //Log.d(TAG, "ACTION_SCREEN_ON");

                    if(notificationService != null)
                        notificationService.stopReminder();

                    break;

                case Intent.ACTION_SCREEN_OFF:
                    //Log.d(TAG, "ACTION_SCREEN_OFF");

                    if(notificationService != null)
                        notificationService.startReminder();

                    break;
            }
        }
    }
}