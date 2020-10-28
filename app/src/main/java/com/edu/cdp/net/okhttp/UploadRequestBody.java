package com.edu.cdp.net.okhttp;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

public class UploadRequestBody extends RequestBody {
    private final RequestBody mRequestBody;
    private int mCurrentLength;
    private final JUploadCallback2 mProgressListener;

    public interface JUploadCallback2{
        void onUploadStart(long max,long progress);

        void onUploadUpdate(long max,long progress);

        void onUploadComplete(long max,long progress);
    }

    public UploadRequestBody(RequestBody mRequestBody, JUploadCallback2 mProgressListener) {
        this.mRequestBody = mRequestBody;
        this.mProgressListener = mProgressListener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        final long contentLength = contentLength();
        // 获取当前写了多少数据？BufferedSink Sink(okio 就是 io )就是一个 服务器的 输出流，我还是不知道写了多少数据

        // 又来一个代理 ForwardingSink
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(@NotNull Buffer source, long byteCount) throws IOException {
                // 每次写都会来这里
                mCurrentLength += byteCount;
                if(mProgressListener!=null){
                    mProgressListener.onUploadUpdate(contentLength,mCurrentLength);
                }
                Log.e("TAG",contentLength+" : "+mCurrentLength);
                super.write(source, byteCount);
            }
        };
        // 转一把
        BufferedSink bufferedSink = Okio.buffer(forwardingSink);
        mRequestBody.writeTo(bufferedSink);
        // 刷新，RealConnection 连接池
        bufferedSink.flush();

    }
}
