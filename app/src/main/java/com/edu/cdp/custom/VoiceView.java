package com.edu.cdp.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.edu.cdp.R;
import com.edu.cdp.net.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class VoiceView extends View implements View.OnTouchListener {
    private Paint mPathPaint, mDownloadPaint;
    private Path path;
    private int width, height;


    private String url = "http://47.98.223.82:8080/JerryEmail/resources/voice/undernoflag.wav";
    private final boolean mPlayStatus = false;
    private byte[] mFft;
    private MediaPlayer mMediaPlayer;
    private Visualizer visualizer;
    private Random random;
    @SuppressLint("DrawAllocation")
    private RectF downloadRectF;//后面园的坐标
    private RectF downloadRectF2;//后面园的坐标
    private boolean mFileIsDownload = false;//是否已经下载
    private int mDownloadStatus = 1;//1：未开始下载  2：正在下载

    private float max = 100;
    private float progress = 0;
    private int prepareProgress = 1;


    public VoiceView(Context context) {
        this(context, null);
    }

    public VoiceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
        }
        Paint p = new Paint();
        p.setDither(true);
        p.setAntiAlias(true);
        mPathPaint = p;

        mDownloadPaint = p;

        random = new Random();

        //判断文件是否已经下载
//        mFileIsDownload = fileIsExists(url);

        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();

            if (x > (width - height)) {
                Toast.makeText(getContext(), "下载", Toast.LENGTH_SHORT).show();
                downloadFile(url);
            } else if (x < (width - (height + dip2px(5)))) {
                Toast.makeText(getContext(), "播放", Toast.LENGTH_SHORT).show();
            }

            return false;
        }
        return false;
    }

    private void downloadFile(String url) {
        if (mFileIsDownload) return;
        if (url == null || url.equals("")) return;
        String saveDir = getContext().getFilesDir().toString() + File.separator + "voice" + File.separator;
        System.out.println("下载文件夹：" + saveDir);
        if (mDownloadStatus == 1)
            OkHttpUtils.DOWNLOAD(url, saveDir, null, new OkHttpUtils.JDownloadCallback() {
                @Override
                public void onFailure(String msg) {
                    System.out.println(msg);
                    mDownloadStatus = 1;
                }

                @Override
                public void onSuccess() {
                    mDownloadStatus = 1;
                    mFileIsDownload = true;
                }

                @Override
                public void onPrepare() {
                    mDownloadStatus = 2;
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 5);
                    valueAnimator.setDuration(200);
                    valueAnimator.setRepeatCount(0);
                    valueAnimator.addUpdateListener(animation -> {
                        prepareProgress = (int) animation.getAnimatedValue();
                        invalidate();
                    });
                    valueAnimator.start();
                }

                @Override
                public void onDownloadStart(long max, long progress, String fileName, String realPath) {
                    VoiceView.this.max = max;
                    System.out.println("开始下载了 max:" +  VoiceView.this.max);

                }

                @Override
                public void onDownloadUpdate(long max, long progress) {
                    VoiceView.this.progress = progress;
//                    System.out.println("进度跟新：" + VoiceView.this.progress);
                    invalidate();
                }

                @Override
                public void onDownloadEnd(long max, long progress) {
                    System.out.println("下载结束:" + max);
                }
            });
    }


    //判断文件是否存在
    public boolean fileIsExists(String url) {
        if (url == null || url.equals("")) return false;
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String saveDir = getContext().getFilesDir().toString() + File.separator + "voice" + File.separator + fileName;
        try {
            File f = new File(saveDir);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private void playAudio() {
        mMediaPlayer = MediaPlayer.create(getContext(), R.raw.undernoflag);
        mMediaPlayer.start();
        visualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                //length  =  1024 个数据;
                mFft = new byte[fft.length / 2 + 1];
                mFft[0] = (byte) Math.abs(fft[1]);
                int j = 1;
                for (int i = 2; i < 18; i++) {
                    mFft[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
                    i += 2;
                    j++;
                }
                invalidate();
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);
        //开启采样
        visualizer.setEnabled(true);

        mMediaPlayer.setOnCompletionListener(mp -> {
            visualizer.setEnabled(false);
            if (visualizer != null) {
                visualizer.release();
            }
            mFft = null;
            invalidate();
        });

    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int specWidth = 0, specHeight = 0;
        //at_most wrap_content
        //exactly match_parent  65dp
        //unexpect 未指定
        specWidth = Math.max(dip2px(150), width);

        specHeight = specWidth / 4;

        this.width = specWidth;
        this.height = specHeight;

        setMeasuredDimension(specWidth, specHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        downloadRectF = new RectF(
                w - h + h / 10,
                h / 10,
                w - h / 10,
                h - h / 10
        );



        downloadRectF2 = new RectF(
                w - h + h / 5,
                h / 5,
                w - h / 5,
                h - h / 5
        );

        w = w - (h + dip2px(5));

        path = new Path();
        path.reset();
        path.moveTo(w / 10, 0);
        path.rLineTo(w * 8 / 10, 0);
        path.rQuadTo(w / 10, 0, w / 10, h / 3);
        path.rLineTo(0, h / 3);
        path.rQuadTo(0, h / 3, -w / 10, h / 3);
        path.rLineTo(-(w * 8 / 10), 0);
        path.rQuadTo(-(w / 10), 0, -(w / 10), -(h / 3));
        path.rLineTo(0, -(h / 3));
        path.rQuadTo(0, -(h / 3), w / 10, -(h / 3));
        path.close();
    }

    @SuppressLint("Range")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int w2 = w - (h + dip2px(5));

        mPathPaint.setStyle(Paint.Style.FILL);
        mPathPaint.setColor(Color.parseColor("#2ecc71"));
        canvas.drawPath(path, mPathPaint);


        if (!mFileIsDownload) {
            //文件未下载
            mDownloadPaint.setStyle(Paint.Style.STROKE);
            mDownloadPaint.setStrokeWidth(h / 10);
            mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
            canvas.drawArc(downloadRectF, 0, 360, false, mDownloadPaint);

            mDownloadPaint.setStrokeCap(Paint.Cap.ROUND);
            mDownloadPaint.setStyle(Paint.Style.STROKE);
            mDownloadPaint.setStrokeWidth(h/18);
            mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
            @SuppressLint("DrawAllocation") Path arrowPath = new Path();
            if(prepareProgress==1){
                arrowPath.moveTo(w - h / 2, h * 2 / 5);
                arrowPath.rLineTo(0, h / 5);
                arrowPath.rQuadTo(-(h / 5 / 2), 0, -(h / 5 / 2), -(h / 5 / 2));
                arrowPath.moveTo(w - h / 2, h * 3 / 5);
                arrowPath.rQuadTo(h / 5 / 2, 0, h / 5 / 2, -(h / 5 / 2));
                canvas.drawPath(arrowPath, mDownloadPaint);

            }else if(prepareProgress == 2){
                arrowPath.moveTo(w - h / 2, h / 5);
                arrowPath.rLineTo(0, h *2/ 5);
                arrowPath.rQuadTo(-(h / 5 / 4), 0, -(h / 5 / 4), -(h / 5 / 4));
                arrowPath.moveTo(w - h / 2, h * 3 / 5);
                arrowPath.rQuadTo(h / 5 / 4, 0, h / 5 / 4, -(h / 5 / 4));
                canvas.drawPath(arrowPath, mDownloadPaint);

            }else if(prepareProgress==3){
                arrowPath.moveTo(w - h / 2, h / 5);
                arrowPath.rLineTo(0, h *2/ 5);
                canvas.drawPath(arrowPath, mDownloadPaint);

            }else if(prepareProgress==4){
                arrowPath.moveTo(w - h / 2, h / 5);
                arrowPath.lineTo(w - h / 2, h / 2);
                canvas.drawPath(arrowPath, mDownloadPaint);

            }else if(prepareProgress==5){
                //开始下载动画
                if(progress!=max){
                    mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
                    mDownloadPaint.setStyle(Paint.Style.FILL);
                    float sweepAngle =  ((360/max)*progress);
                    System.out.println("(360/"+max+") X " + progress +" = "+sweepAngle);

                    canvas.drawArc(downloadRectF2,-90,sweepAngle,true,mDownloadPaint);
                }else{
                    mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
                    mDownloadPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(w - h / 2,h / 2,h/2-h/5,mDownloadPaint);
                }
            }
        } else {
            //文件已经下载
            mDownloadPaint.setStyle(Paint.Style.STROKE);
            mDownloadPaint.setStrokeWidth(h / 8);
            mDownloadPaint.setColor(Color.parseColor("#2ecc71"));
            mDownloadPaint.setStrokeCap(Paint.Cap.ROUND);

            canvas.drawPoint(w2 + dip2px(5), h - h / 8, mDownloadPaint);
        }

        //画柱状波形图
        if (mFft == null) {
            mPathPaint.setStyle(Paint.Style.STROKE);
            mPathPaint.setStrokeWidth(w2 / 15);
            mPathPaint.setColor(Color.WHITE);
            mPathPaint.setStrokeCap(Paint.Cap.ROUND);
            @SuppressLint("DrawAllocation") float[] mPoints = new float[63];
            for (int i = 1; i < 13; i++) {
                mPoints[i * 4] = w2 * i / 13;
                mPoints[i * 4 + 1] = h * 3 / 4;
                mPoints[i * 4 + 2] = w2 * i / 13;
                mPoints[i * 4 + 3] = h * 3 / 4;
            }
            canvas.drawLines(mPoints, mPathPaint);
        } else {
            mPathPaint.setStyle(Paint.Style.STROKE);
            mPathPaint.setStrokeWidth(w2 / 15);
            mPathPaint.setColor(Color.WHITE);
            mPathPaint.setStrokeCap(Paint.Cap.ROUND);
            @SuppressLint("DrawAllocation") float[] mPoints = new float[mFft.length * 4];
            for (int i = 1; i < 13; i++) {
                if (mFft[i] > h / 2 || mFft[i] == 0 || mFft[i] < 0)
                    mFft[i] = (byte) random.nextInt(h / 2);
                mPoints[i * 4] = w2 * i / 13;
                mPoints[i * 4 + 1] = h * 3 / 4;
                mPoints[i * 4 + 2] = w2 * i / 13;
                mPoints[i * 4 + 3] = h * 3 / 4 - mFft[i];
            }
            canvas.drawLines(mPoints, mPathPaint);
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int px2dip(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
