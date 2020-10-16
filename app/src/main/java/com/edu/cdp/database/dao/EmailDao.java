package com.edu.cdp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.edu.cdp.database.bean.Email;

import java.util.List;

@Dao
public interface EmailDao {
    @Query("SELECT * FROM  Email")
    List<Email> getAllEmail();

    @Query("SELECT * FROM Email WHERE id = :id")
    Email loadEmailById(int id);

    @Query("SELECT * FROM Email WHERE tag = 1 and userid = :userid")
    List<Email> loadAllInbox(int userid);


    @Query("SELECT * FROM Email WHERE tag = 2 and userid = :userid")
    List<Email> loadAllOutbox(int userid);

    @Query("SELECT * FROM Email WHERE tag = 3 and userid = :userid")
    List<Email> loadAllStarbox(int userid);

    @Query("SELECT * FROM Email WHERE tag = 4 and userid = :userid")
    List<Email> loadAllGroupbox(int userid);

    @Query("SELECT * FROM Email WHERE tag = 5 and userid = :userid")
    List<Email> loadAllDraftbox(int userid);

    @Insert
    void insertOneEmail(Email Email);

    @Insert
    void insertMultiAnimes(Email... Email);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateEmails(Email... Email);

    @Delete
    void deleteEmail(Email Email);


    @Query("DELETE FROM  Email")
    void deleteAllEmail();
}
