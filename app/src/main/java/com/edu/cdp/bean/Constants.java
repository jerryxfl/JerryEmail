package com.edu.cdp.bean;

public class Constants {
    public static final String PORT="47.98.223.82:8080";
//    public static final String PORT="192.168.42.40:8080";
//    public static final String PORT="172.17.175.120:8080";


    public static final String BASE_URL = "http://"+PORT+"/JerryEmail/";

    public static String WS_URL(String uuid) {
        return "ws://"+PORT+"/JerryEmail/websocket/"+uuid;
    }

    //post
    public static final String LOGIN_URL = BASE_URL+"common/login";





    //get
    public static final String CONTACTS = BASE_URL+"api/contacts";
    public static final String INBOX_MESSAGE_COUNT = BASE_URL+"api/inbox_message_count";
    public static final String OUTBOX_MESSAGE_COUNT = BASE_URL+"api/outbox_message_count";
    public static final String DRAFTBOX_MESSAGE_COUNT = BASE_URL+"api/draftbox_message_count";
    public static final String STARBOX_MESSAGE_COUNT = BASE_URL+"api/starbox_message_count";
    public static final String GROUPBOX_MESSAGE_COUNT = BASE_URL+"api/groupbox_message_count";



    public static final String INBOX_MESSAGE = BASE_URL+"api/inbox_message?start=";
    public static final String OUTBOX_MESSAGE = BASE_URL+"api/outbox_message?start=";
    public static final String DRAFTBOX_MESSAGE = BASE_URL+"api/draftbox_message?start=";
    public static final String STARBOX_MESSAGE = BASE_URL+"api/starbox_message?start=";
    public static final String GROUPBOX_MESSAGE = BASE_URL+"api/groupbox_message?start=";

}
