package com.edu.cdp.jni;

public class TestJni {
    static {
        System.loadLibrary("Test");
    }

    public  native  String JniGetString();
}
