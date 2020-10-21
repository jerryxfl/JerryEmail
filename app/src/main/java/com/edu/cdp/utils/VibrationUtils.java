package com.edu.cdp.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class VibrationUtils {
    public static void Vibrator (Activity activity,long[] l,int repeatCount){
        Vibrator vibrator = (Vibrator) activity.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(l,repeatCount);// new long[]{100,10,100,1000},-1
    }
}
