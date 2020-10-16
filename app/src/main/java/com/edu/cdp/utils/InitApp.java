package com.edu.cdp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppComponentFactory;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.edu.cdp.base.BaseDialog;
import com.edu.cdp.ui.dialog.ConfirmDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class InitApp {
    private static InitApp instance;
    private List<AppCompatActivity> activities = new ArrayList<AppCompatActivity>();

    public static synchronized InitApp getInstance() {
        if (instance == null) {
            instance = new InitApp();
        }
        return instance;
    }


    public void addActivity(AppCompatActivity activity) {
        activities.add(activity);
        System.out.println("添加activity");
    }

    public void removeActivity(AppCompatActivity activity) {
        activities.remove(activity);
        System.out.println("移除activity");

    }

    public boolean isLast(){
        return activities.size()<=1;
    }

    private void removeAllActivity() {
        activities.clear();
    }


    public void finishAll(){
        for (AppCompatActivity activity:activities) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }
}
