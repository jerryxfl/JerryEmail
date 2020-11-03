package com.edu.cdp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.edu.cdp.database.bean.Email;
import com.edu.cdp.database.bean.LocalUser;
import com.edu.cdp.database.bean.Recent;
import com.edu.cdp.database.dao.EmailDao;
import com.edu.cdp.database.dao.RecentDao;
import com.edu.cdp.database.dao.UserDao;

@Database(entities = {LocalUser.class, Email.class, Recent.class}, version = 5,exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract EmailDao EmailDao();
    public abstract RecentDao RecentDao();
}