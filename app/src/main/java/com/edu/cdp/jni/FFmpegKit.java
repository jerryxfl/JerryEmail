package com.edu.cdp.jni;

public class FFmpegKit {
    
    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();
}
