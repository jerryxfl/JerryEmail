package com.edu.cdp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.edu.cdp.database.bean.Email;
import com.edu.cdp.database.bean.Recent;

import java.util.List;

@Dao
public interface RecentDao {
    @Query("SELECT * FROM  Recent")
    List<Recent> getAllRecent1();

    @Query("SELECT * FROM  Recent order by id desc limit 0,5")
    List<Recent> getAllRecent2();

    @Query("SELECT * FROM Recent WHERE id = :id")
    Recent loadRecentById(int id);

    @Insert
    void insertOneRecent(Recent Recent);

    @Insert
    void insertMultiAnimes(Recent... Recent);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateRecents(Recent... Recent);

    @Delete
    void deleteRecent(Recent Recent);


    @Query("DELETE FROM  Recent")
    void deleteAllRecent();
}
