package com.edu.cdp.net.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager {
    private static WebSocketManager instance;

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }


    /**
     * 基于 okhttpwebsocket封装
     *
     * @param url                websocket 连接地址
     * @param jWebSocketListener
     */
    @Deprecated
    public void OkHttpWebSocketClient(String url, final JWebSocketListener jWebSocketListener) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .callTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                jWebSocketListener.onClosed(webSocket, code, reason);
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                jWebSocketListener.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                jWebSocketListener.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                jWebSocketListener.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                jWebSocketListener.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                jWebSocketListener.onOpen(webSocket, response);
            }
        });
        okHttpClient.dispatcher().executorService().shutdown();
    }





    private WebSocketClient client;

    public WebSocketClient JavaWebSocketClient(String url, final JWebSocketListener2 jWebSocketListener2) {

        if (client != null) {
            client.close();
            client = null;
        }
        client = new WebSocketClient(URI.create(url)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                jWebSocketListener2.onOpen(handshakedata,client);
            }

            @Override
            public void onMessage(String message) {
                jWebSocketListener2.onMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                jWebSocketListener2.onClose(code, reason, remote);
            }

            @Override
            public void onError(Exception ex) {
                jWebSocketListener2.onError(ex);
            }
        };
        client.connect();
        return client;
    }




    //获得java-websocket 操作对象
    public WebSocketClient getClient() {
        return client;
    }
}
