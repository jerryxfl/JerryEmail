package com.edu.cdp.utils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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
