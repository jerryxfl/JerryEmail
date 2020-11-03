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

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

public class JApplication extends Application implements ViewModelStoreOwner{
    private static JApplication instance;
    private AppDataBase db;
    private FlutterEngine flutterEngine;

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

        //初始化FlutterActivity
        flutterEngine = new FlutterEngine(this);
        flutterEngine.getNavigationChannel().setInitialRoute("login");
        flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());
        FlutterEngineCache.getInstance().put("JENGINE",flutterEngine);

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
