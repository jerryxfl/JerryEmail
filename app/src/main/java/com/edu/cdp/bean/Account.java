package com.edu.cdp.bean;

import com.edu.cdp.database.bean.LocalUser;

import java.io.Serializable;

public class Account implements Serializable {
    private LocalUser LocalUser;
    private int EmailNum;
    private boolean isOnline;

    public Account() {
    }

    public Account(LocalUser localUser, int emailNum, boolean isOnline) {
        LocalUser = localUser;
        EmailNum = emailNum;
        this.isOnline = isOnline;
    }

    public LocalUser getLocalUser() {
        return LocalUser;
    }

    public void setLocalUser(LocalUser localUser) {
        LocalUser = localUser;
    }

    public int getEmailNum() {
        return EmailNum;
    }

    public void setEmailNum(int emailNum) {
        EmailNum = emailNum;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
