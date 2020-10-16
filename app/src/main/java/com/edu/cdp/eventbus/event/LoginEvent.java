package com.edu.cdp.eventbus.event;

import com.edu.cdp.bean.Account;

import java.util.List;

public class LoginEvent {
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LoginEvent(String tag) {
        this.tag = tag;
    }
}
