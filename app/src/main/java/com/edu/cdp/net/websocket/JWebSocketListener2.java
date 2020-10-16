package com.edu.cdp.net.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.Response;
import okio.ByteString;

public interface JWebSocketListener2 {
        void onOpen(ServerHandshake handshakedata, WebSocket websocket);
        void onMessage(String message);
        void onClose(int code, String reason, boolean remote);
        void onError(Exception ex);
}
