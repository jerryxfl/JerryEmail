package com.edu.cdp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.edu.cdp.database.bean.Email;
import com.edu.cdp.database.bean.LocalUser;
import com.edu.cdp.database.dao.EmailDao;
import com.edu.cdp.database.dao.UserDao;

@Database(entities = {LocalUser.class, Email.class}, version = 3,exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract EmailDao EmailDao();
}