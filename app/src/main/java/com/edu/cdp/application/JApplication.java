package com.edu.cdp.application;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.room.Room;

import com.edu.cdp.database.AppDataBase;
import com.edu.cdp.model.manager.ModelManager;
import com.tencent.mmkv.MMKV;

public class JApplication extends Application implements ViewModelStoreOwner{
    private static JApplication instance;
    private AppDataBase db;

    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);
        instance = this;
        db = Room.databaseBuilder(this,AppDataBase.class,"init")
                .addMigrations()
                .allowMainThreadQueries()
                .build();

        ModelManager.getManager().initModel(this,db);
    }

    public Context getContext() {
        return this;
    }

    public AppDataBase getDb() {
        return db;
    }

    public static JApplication getInstance() {
        return instance;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return new ViewModelStore();
    }
}
