package com.edu.cdp.database.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Email implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int gid;
    private int id;
    private int userid;
    private String senduserinfo;
    private String receiveuserinfo;
    private String title;
    private String content;
    private String accessory;
    private Integer time;
    private int tag;//1:inbox 2:outbox 3:starbox 4:groupbox 5:draftbox

    public Email() {
    }

    @Ignore
    public Email(int id,int userid, String senduserinfo, String receiveuserinfo, String title, String content, String accessory,Integer time, int tag) {
        this.id = id;
        this.userid = userid;
        this.senduserinfo = senduserinfo;
        this.receiveuserinfo = receiveuserinfo;
        this.title = title;
        this.content = content;
        this.accessory = accessory;
        this.time = time;
        this.tag = tag;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }


    public String getSenduserinfo() {
        return senduserinfo;
    }

    public void setSenduserinfo(String senduserinfo) {
        this.senduserinfo = senduserinfo;
    }

    public String getReceiveuserinfo() {
        return receiveuserinfo;
    }

    public void setReceiveuserinfo(String receiveuserinfo) {
        this.receiveuserinfo = receiveuserinfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getAccessory() {
        return accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }



    @Override
    public int hashCode() {
        return Objects.hash(id, senduserinfo, receiveuserinfo, title, content,time);
    }

}
