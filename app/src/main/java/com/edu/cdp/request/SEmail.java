package com.edu.cdp.request;


import java.sql.Timestamp;
import java.util.Objects;

public class SEmail {
    private int id;
    private int senduserid;
    private String receiveuserid;
    private String title;
    private String content;
    private String accessory;

    public SEmail() {
    }

    public SEmail(int id,int senduserid, String receiveuserid, String title, String content, String accessory) {
        this.id = id;
        this.senduserid = senduserid;
        this.receiveuserid = receiveuserid;
        this.title = title;
        this.content = content;
        this.accessory = accessory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenduserid() {
        return senduserid;
    }

    public void setSenduserid(int senduserid) {
        this.senduserid = senduserid;
    }

    public String getReceiveuserid() {
        return receiveuserid;
    }

    public void setReceiveuserid(String receiveuserid) {
        this.receiveuserid = receiveuserid;
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
}
