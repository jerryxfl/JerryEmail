package com.edu.cdp.net.websocket;

public class CommandType {

    //to server
    public static final String SUBSCRIBE = "SUBSCRIBE";
    public static final String UNSUBSCRIBE = "UNSUBSCRIBE";



    //together
    public static final String MESSAGE  = "MESSAGE";
    public static final String VIBRATION = "VIBRATION";



    //to client
    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String CONNECT_FAIL = "CONNECT_FAIL";
    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";
    public static final String ALREADYCONNECT = "ALREADYCONNECT";
    public static final String DISINVAD = "DISINVAD";
    public static final String NOLOGIN = "NOLOGIN";


}
