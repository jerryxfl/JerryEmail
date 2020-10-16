package com.edu.cdp.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Email implements Serializable {
    private int Avatar;
    private String Title;
    private String Content;
    private Date Time;
    private List<Integer> OthersAvatar;
    private int MsgNumber;

    public Email() {
    }

    public Email(int avatar, String title, String content, Date time, List<Integer> othersAvatar, int msgNumber) {
        Avatar = avatar;
        Title = title;
        Content = content;
        Time = time;
        OthersAvatar = othersAvatar;
        MsgNumber = msgNumber;
    }

    public int getAvatar() {
        return Avatar;
    }

    public void setAvatar(int avatar) {
        Avatar = avatar;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Date getTime() {
        return Time;
    }

    public void setTime(Date time) {
        Time = time;
    }

    public List<Integer> getOthersAvatar() {
        return OthersAvatar;
    }

    public void setOthersAvatar(List<Integer> othersAvatar) {
        OthersAvatar = othersAvatar;
    }

    public int getMsgNumber() {
        return MsgNumber;
    }

    public void setMsgNumber(int msgNumber) {
        MsgNumber = msgNumber;
    }
}
