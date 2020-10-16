package com.edu.cdp.net.okhttp;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSONObject;
import com.edu.cdp.net.websocket.JWebSocketListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkHttpUtils {
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static void POST(String url, Map<String, String> headers, Object content, final Jcallback jcallback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .callTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .build();
        JSONObject json = (JSONObject) JSONObject.toJSON(content);

        RequestBody formBody = RequestBody.create(JSON, json.toJSONString());
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBody);

        //添加请求头
        if (headers != null) for (String key : headers.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(headers.get(key)));
        }
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        jcallback.onFailure();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String result = null;
                try {
                    result = Objects.requireNonNull(response.body()).string();
                    try {
                        if (jcallback.onResponseAsync(JSONObject.parseObject(result))) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    jcallback.onSuccess();
                                }
                            });
                        }else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    jcallback.onFailure();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                jcallback.onFailure();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            jcallback.onFailure();
                        }
                    });
                }
            }
        });
    }


    public static void GET(String url, Map<String, String> headers, final Jcallback jcallback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .callTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();

        //添加请求头
        for (String key : headers.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(headers.get(key)));
        }
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        jcallback.onFailure();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String result = null;
                try {
                    result = Objects.requireNonNull(response.body()).string();
                    try {
                        if (jcallback.onResponseAsync(JSONObject.parseObject(result))) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    jcallback.onSuccess();
                                }
                            });
                        }else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    jcallback.onFailure();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                jcallback.onFailure();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            jcallback.onFailure();
                        }
                    });
                }
            }
        });
    }



    public static void DOWNLOAD(final String url, final String saveDir, Map<String, String> headers, final JDownloadCallback jdownloadCallback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(500  * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(500 *1000, TimeUnit.MILLISECONDS)
                .callTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .build();

        Request.Builder builder = new Request.Builder().url(url);
        //添加请求头
        if (headers != null) for (String key : headers.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(headers.get(key)));
        }
        Request request=builder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        jdownloadCallback.onFailure("连接超时");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        jdownloadCallback.onPrepare();
                    }
                });
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //获得文件真实保存目录
                File downloadFile = new File(saveDir);
                if(!downloadFile.mkdir()){
                    downloadFile.createNewFile();
                }
                final String saveDir = downloadFile.getAbsolutePath();
                //获得文件真实保存目录 end

                //开始下载文件
                try {
                    is = response.body().byteStream();//获得输入流
                    final long max = response.body().contentLength();
                    final String fileName =  url.substring(url.lastIndexOf("/")+1);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            jdownloadCallback.onDownloadStart(max,0,fileName,saveDir);
                        }
                    });

                    //获得文件写入对象
                    File file = new File(saveDir,fileName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while((len = is.read(buf))!=-1){
                        fos.write(buf,0,len);
                        sum += len;
                        final long finalSum = sum;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                jdownloadCallback.onDownloadUpdate(max, finalSum);
                            }
                        });
                    }
                    fos.flush();
                    final long finalSum1 = sum;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            jdownloadCallback.onDownloadEnd(max, finalSum1);

                            jdownloadCallback.onSuccess();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() -> jdownloadCallback.onFailure("下载文件出错"));
                }finally{
                    try{
                        if(is!=null)
                            is.close();
                        if(fos!=null){
                            fos.close();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                //下载文件 end
            }
        });
    }


    private static void UPLOAD(final String url, final String fileDir, Map<String, String> headers, final JUploadCallback jUploadCallback){



    }


    public interface JUploadCallback{
        void onFailure(String msg);

        void onSuccess();

        void onUploadStart(long max,long progress);

        void onUploadUpdate(long max,long progress);

        void onUploadComplete(long max,long progress);
    }

    public interface JDownloadCallback{
        void onFailure(String msg);

        void onSuccess();

        void onPrepare();

        void onDownloadStart(long max,long progress,String fileName,String realPath);

        void onDownloadUpdate(long max,long progress);

        void onDownloadEnd(long max,long progress);
    }


    public interface Jcallback {
        void onFailure();

        boolean onResponseAsync(JSONObject response);

        void onSuccess();
    }
}