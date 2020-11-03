package com.edu.cdp.database.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Recent {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int emailid;

    private int tag;

    public Recent(int emailid,int tag) {
        this.emailid = emailid;
        this.tag = tag;
    }

    @Ignore
    public Recent() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmailid() {
        return emailid;
    }

    public void setEmailid(int emailid) {
        this.emailid = emailid;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,emailid,tag);    }
}
