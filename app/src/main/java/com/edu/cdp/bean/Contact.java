package com.edu.cdp.bean;

import com.edu.cdp.response.User;

import java.io.Serializable;

public class Contact implements Serializable {
    private User user;
    private boolean isOnline;

    public User getLocalUser() {
        return user;
    }

    public void setLocalUser(User user) {
        this.user = user;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Contact(User user, boolean isOnline) {
        this.user = user;
        this.isOnline = isOnline;
    }

    public Contact() {
    }



}
