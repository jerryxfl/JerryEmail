package com.edu.cdp.net.websocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public interface JWebSocketListener {
        void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason);
        void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason);
        void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response);
        void onMessage(@NotNull WebSocket webSocket, @NotNull String text);
        void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes);
        void onOpen(@NotNull WebSocket webSocket, @NotNull Response response);
}
