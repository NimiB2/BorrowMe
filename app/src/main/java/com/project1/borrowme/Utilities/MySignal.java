package com.project1.borrowme.Utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class MySignal {
    private static MySignal instance=null;
    private static Vibrator vibrator;
    private Toast currentToast;
    private MediaPlayer mp;

    private Context context;

    private MySignal(Context context){
        this.context=context;
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

    public void vibrate(long miliSec) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(miliSec, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(miliSec);
        }
    }

    public  void sound(int audio){
        if(mp !=null){
            mp.stop();
            mp.release();
            mp=null;
        }
        mp = MediaPlayer.create(context,audio);
        if(mp !=null){
            mp.setVolume(1.0f,1.0f);
            mp.start();
            mp.setOnCompletionListener(MediaPlayer::pause);
        }
    }

    public static MySignal getInstance() {
        return instance;
    }
}