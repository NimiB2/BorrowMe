package com.project1.borrowme.Utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class MySignal {
    private final long SHORT_VIB = 500;
    private final long LONG_VIB = 1000;
    private static MySignal instance = null;
    private static Vibrator vibrator;
    private Toast currentToast;

    private Context context;

    private MySignal(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        synchronized (MySignal.class) {
            if (instance == null) {
                instance = new MySignal(context);
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }
        }
    }

    public void toast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    public void vibrate(boolean isShort) {
        long miliSec;
        if (isShort) {
            miliSec = SHORT_VIB;
        } else {
            miliSec = LONG_VIB;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(miliSec, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(miliSec);
        }
    }


    public static MySignal getInstance() {
        return instance;
    }
}