package com.robotfuzz.al.aledxander;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.Integer.parseInt;

public class CallReceiver extends BroadcastReceiver {

    NotificationManager mNotificationManager;
    Method method;


    public void reflect(Context context) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            method = mNotificationManager.getClass().getMethod("openLed", int.class, int.class, int.class, int.class, int.class);
        } catch (SecurityException e) {
            Log.d("ALEDxander", "Security prevents reflection");
        } catch (NoSuchMethodException e) {
            Log.d("ALEDxander", "No method");
        }

    }

    public void openLed(int led, int r, int g, int b, int z) {

        try {
            method.invoke(mNotificationManager, led, r, g, b, z);
        } catch (IllegalAccessException e) {
            Log.d("ALEDxander", "IllegalAccessException");
        } catch (InvocationTargetException e) {
            Log.d("ALEDxander", "InvocationTargetException");
        }

    }

    public void onReceive(Context context, Intent intent) {

        reflect(context);
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        String stripped_number = number.replaceAll("[^\\d]", "");
        int num = parseInt(stripped_number);
        if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            try {
                for (int i = 0; i < 5; i++)
                    openLed(5-i, (num>>((i*3)+0))&1, (num>>((i*3)+1))&1, (num>>((i*3)+2))&1, 0);
            } catch (NoSuchMethodError e) {
                Log.d("ALEDxander", "No HW leds");
            }
        } else {
            try {
                for (int i = 0; i < 5; i++)
                    openLed(5-i, 0, 0, 0, 0);
            } catch (NoSuchMethodError e) {
                Log.d("ALEDxander", "No HW leds");
            }
        }

    }

}
