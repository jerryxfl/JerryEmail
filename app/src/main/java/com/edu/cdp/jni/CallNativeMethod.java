package com.edu.cdp.jni;

public class CallNativeMethod {
    static {
        System.loadLibrary("Test");
    }


    public static native void CallJniMethod();
}
