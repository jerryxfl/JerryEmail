package com.edu.cdp.net.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.system.ErrnoException;

import com.alibaba.fastjson.JSONObject;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class OkHttpUtils {
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    /**
     * @param url       网址
     * @param headers   请求头 UUID
     * @param content   内容
     * @param jcallback 回调函数
     */
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
                new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String result = null;

                if (response.isSuccessful()) {
                    try {
                        result = Objects.requireNonNull(response.body()).string();
                        try {
                            if (jcallback.onResponseAsync(JSONObject.parseObject(result))) {
                                new Handler(Looper.getMainLooper()).post(() -> jcallback.onSuccess());
                            } else {
                                new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                }
            }
        });
    }

    /**
     * @param url       网址
     * @param headers   请求头 UUID
     * @param jcallback 回调函数
     */
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
        if (headers != null) for (String key : headers.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(headers.get(key)));
        }
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String result = null;
                if (response.isSuccessful()) {
                    try {
                        result = Objects.requireNonNull(response.body()).string();
                        try {
                            if (jcallback.onResponseAsync(JSONObject.parseObject(result))) {
                                new Handler(Looper.getMainLooper()).post(() -> jcallback.onSuccess());
                            } else {
                                new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> jcallback.onFailure());
                }
            }
        });
    }


    /**
     * @param url               网址
     * @param headers           请求头 UUID
     * @param jdownloadCallback 回调函数
     */
    public static void DOWNLOAD(final String url, final String saveDir, Map<String, String> headers, final JDownloadCallback jdownloadCallback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .callTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .build();

        Request.Builder builder = new Request.Builder().url(url);
        //添加请求头
        if (headers != null) for (String key : headers.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(headers.get(key)));
        }
        Request request = builder.build();
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
                new Handler(Looper.getMainLooper()).post(() -> jdownloadCallback.onPrepare());

                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> jdownloadCallback.onFailure("链接失败"));
                } else {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;

                    //获得文件真实保存目录
                    File downloadFile = new File(saveDir);
                    if (!downloadFile.mkdir()) {
                        downloadFile.createNewFile();
                    }
                    final String saveDir = downloadFile.getAbsolutePath();
                    //获得文件真实保存目录 end

                    //开始下载文件
                    try {
                        is = response.body().byteStream();//获得输入流
                        final long max = response.body().contentLength();
                        final String fileName = url.substring(url.lastIndexOf("/") + 1);
                        new Handler(Looper.getMainLooper()).post(() -> jdownloadCallback.onDownloadStart(max, 0, fileName, saveDir));

                        //获得文件写入对象
                        File file = new File(saveDir, fileName);
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            final long finalSum = sum;
                            new Handler(Looper.getMainLooper()).post(() -> jdownloadCallback.onDownloadUpdate(max, finalSum));
                        }
                        fos.flush();
                        final long finalSum1 = sum;
                        new Handler(Looper.getMainLooper()).post(() -> {
                            jdownloadCallback.onDownloadEnd(max, finalSum1);

                            jdownloadCallback.onSuccess();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(() -> jdownloadCallback.onFailure("下载文件出错"));
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //下载文件 end
                }
            }
        });
    }


    /**
     * @param url              网址
     * @param fileDir          文件地址
     * @param headers          请求头 UUID
     * @param name             上传的名字
     * @param jUploadCallback1 回调函数
     */
    public static void UPLOAD(final String url, final String fileDir, Map<String, String> headers, String name,
                              final JUploadCallback1 jUploadCallback1,
                              UploadRequestBody.JUploadCallback2 jUploadCallback2) {
        File file = new File(fileDir);
        if (!file.exists()) {
            //如果文件不存在，抛出空指针异常
            System.out.println("file not exists");
            if (jUploadCallback2 != null) jUploadCallback1.onFailure("file not exists");
            return;
        }

        //创建okhttplient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .callTimeout(500 * 1000, TimeUnit.MILLISECONDS)
                .build();

//        创建请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(name, file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();

        //监听进度
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody, new UploadRequestBody.JUploadCallback2() {
            @Override
            public void onUploadStart(long max, long progress) {
                System.out.println("开始上传");
                if (jUploadCallback2 != null) jUploadCallback2.onUploadStart(max, progress);
            }

            @Override
            public void onUploadUpdate(long max, long progress) {
                System.out.println("当前进度：" + progress);
                if (jUploadCallback2 != null) jUploadCallback2.onUploadUpdate(max, progress);
            }

            @Override
            public void onUploadComplete(long max, long progress) {
                System.out.println("上传结束");
                if (jUploadCallback2 != null) jUploadCallback2.onUploadComplete(max, progress);
            }
        });


        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(uploadRequestBody);

        //添加请求头
        if (headers != null) for (String key : headers.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(headers.get(key)));
        }

        //        创建请求
        Request request = builder.build();


        //        发送请求
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> jUploadCallback1.onFailure("请求失败"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String result = null;
                if (response.isSuccessful()) {
                    try {
                        result = Objects.requireNonNull(response.body()).string();
                        if (jUploadCallback1.onResponseAsync(JSONObject.parseObject(result))) {
                            new Handler(Looper.getMainLooper()).post(jUploadCallback1::onSuccess);
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> jUploadCallback1.onFailure("json解析错误"));
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(() -> jUploadCallback1.onFailure("解析错误"));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> jUploadCallback1.onFailure("请求错误"));
                }
            }
        });
    }


    public interface JUploadCallback1 {
        boolean onResponseAsync(JSONObject response);

        void onFailure(String msg);

        void onSuccess();
    }


    public interface JDownloadCallback {
        void onFailure(String msg);

        void onSuccess();

        void onPrepare();

        void onDownloadStart(long max, long progress, String fileName, String realPath);

        void onDownloadUpdate(long max, long progress);

        void onDownloadEnd(long max, long progress);
    }


    public interface Jcallback {
        void onFailure();

        boolean onResponseAsync(JSONObject response);

        void onSuccess();
    }
}