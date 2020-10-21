package com.edu.cdp.net.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ServerHandshake;

public interface JWebSocketListener2 {
        void onOpen(ServerHandshake handshakedata, WebSocket websocket);
        void onMessage(String message);
        void onClose(int code, String reason, boolean remote);
        void onError(Exception ex);
}
