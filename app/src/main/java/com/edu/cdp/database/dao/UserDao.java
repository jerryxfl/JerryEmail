package com.edu.cdp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.edu.cdp.database.bean.LocalUser;

import java.util.List;

@Dao
public interface UserDao{


    @Query("SELECT * FROM  LocalUser")
    List<LocalUser> getAllUser();

    @Query("SELECT * FROM LocalUser WHERE username = :username")
    LocalUser loadUserByUsername(int username);

    @Insert
    void insertOneUser(LocalUser localUser);

    @Insert
    void insertMultiAnimes(LocalUser... localUsers);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUsers(LocalUser... localUsers);

    @Delete
    void deleteUser(LocalUser localUser);


    @Query("DELETE FROM  LocalUser")
    void deleteAllUsers();
}
