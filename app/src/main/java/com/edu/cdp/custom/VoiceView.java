package com.edu.cdp.custom;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.net.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class VoiceView<T extends Context & LifecycleOwner> extends View implements View.OnTouchListener {
    private T context;
    private BaseActivity baseActivity;
    private Paint mPathPaint, mDownloadPaint;
    private Path path;
    private int width, height;


    private String url;
    private final boolean mPlayStatus = false;
    private byte[] mFft,mLastFft;
    private MediaPlayer mMediaPlayer;
    private Visualizer visualizer;
    private Random random;
    @SuppressLint("DrawAllocation")
    private RectF downloadRectF;//后面园的坐标
    private RectF downloadRectF2;//后面园的坐标
    private boolean mFileIsDownload = false;//是否已经下载
    private boolean mIsError = false;//是否已经下载
    private boolean mIsSuccess = false;//是否已经下载
    private int mDownloadStatus = 1;//1：未开始下载  2：正在下载

    private float max = 100;
    private float progress = 0;
    private int prepareProgress = 1;
    private float errorProgress = 1;
    private float successProgress = 1;


    private float musicMax=100,musicProgress=0;

    public VoiceView(T context) {
        this(context, (AttributeSet) null);
    }

    public VoiceView(T context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceView(T context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(T context, AttributeSet attrs) {
        this.context = context;
        this.baseActivity = (BaseActivity) context;
        if (attrs != null) {
        }
        Paint p = new Paint();
        p.setDither(true);
        p.setAntiAlias(true);
        mPathPaint = p;

        mDownloadPaint = p;

        random = new Random();

        //判断文件是否已经下载
        mFileIsDownload = fileIsExists(url);

        setOnTouchListener(this);

        Lifecycle lifecycle = context.getLifecycle();
        lifecycle.addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void onResume() {
                resume();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            public void onPause() {
                pause();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                stopPlay();
                lifecycle.removeObserver(this);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();

            if (x > (width - height)) {
                if (baseActivity != null) {

                    if (
                            baseActivity.RequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    && baseActivity.RequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    && !mFileIsDownload
                    ) downloadFile(url);
                }
            } else if (x < (width - (height + dip2px(5)))) {
                if (baseActivity != null) {
                    if (baseActivity.RequestPermission(Manifest.permission.RECORD_AUDIO) && mFileIsDownload) {
                        try {
                            String fileName = url.substring(url.lastIndexOf("/") + 1);
                            String saveDir = getContext().getFilesDir().toString() + File.separator + "voice" + File.separator + fileName;
                            playAudio(saveDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
                    mIsError = true;
                    mIsSuccess = false;

                    float sweepAngle = ((360 / max) * progress);
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(sweepAngle, 0);
                    valueAnimator.setDuration(500);
                    valueAnimator.setRepeatCount(0);
                    valueAnimator.addUpdateListener(animation -> {
                        errorProgress = (float) animation.getAnimatedValue();
                        invalidate();
                    });
                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            prepareProgress = 1;
                            mIsError = false;
                            invalidate();
                        }
                    });
                    valueAnimator.start();

                }

                @Override
                public void onSuccess() {
                    mDownloadStatus = 1;
                    mIsError = false;
                    mIsSuccess = true;
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(height / 2, 0f);
                    valueAnimator.setDuration(500);
                    valueAnimator.setRepeatCount(0);
                    valueAnimator.setInterpolator(new BounceInterpolator());
                    valueAnimator.addUpdateListener(animation -> {
                        successProgress = (float) animation.getAnimatedValue();
                        invalidate();
                    });

                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mFileIsDownload = true;
                            mIsSuccess = false;
                            invalidate();
                        }
                    });
                    valueAnimator.start();
                }

                @Override
                public void onPrepare() {
                    mDownloadStatus = 2;
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 6);
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
                    System.out.println("开始下载了 max:" + VoiceView.this.max);

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

    private void pause() {
        if (mMediaPlayer != null)
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                if (visualizer != null) {
                    visualizer.setEnabled(false);
                    visualizer.release();
                    visualizer = null;
                }
                invalidate();
            }
    }

    private void resume() {
        if (mMediaPlayer != null)
            if (!mMediaPlayer.isPlaying()) {
                if (visualizer != null) {
                    visualizer.setEnabled(false);
                    visualizer.release();
                    visualizer = null;
                }
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
                        if(mMediaPlayer!=null)musicProgress = mMediaPlayer.getCurrentPosition();
                        invalidate();
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, true);
                //开启采样
                visualizer.setEnabled(true);
            }
    }


    private void playAudio(String file) throws IOException {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                pause();
            } else {
                resume();
            }
        } else {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(file);
            mMediaPlayer.prepare();
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
                    if(mMediaPlayer!=null)musicProgress = mMediaPlayer.getCurrentPosition();
                    invalidate();
                }
            }, Visualizer.getMaxCaptureRate() / 2, false, true);
            //开启采样
            visualizer.setEnabled(true);

            mMediaPlayer.setOnPreparedListener(mp -> {
                musicMax = mp.getDuration();
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                visualizer.setEnabled(false);
                if (visualizer != null) {
                    visualizer.release();
                    visualizer = null;
                }
                mFft = null;
                invalidate();
            });
        }
    }

    private void stopPlay() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
            mFft = null;
            invalidate();
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
            mDownloadPaint.setStrokeCap(Paint.Cap.ROUND);

            if (mIsError || mIsSuccess) {
                //下载出错或成功
                if (mIsError) {
                    mDownloadPaint.setStrokeWidth(h / 10);
                    mDownloadPaint.setStyle(Paint.Style.STROKE);
                    if (errorProgress <= 10) mDownloadPaint.setColor(Color.RED);
                    else mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
                    canvas.drawArc(downloadRectF, 0, 360, false, mDownloadPaint);

                    mDownloadPaint.setStyle(Paint.Style.FILL);
                    canvas.drawArc(downloadRectF2, -90, errorProgress, true, mDownloadPaint);
                } else if (mIsSuccess) {
                    mDownloadPaint.setStyle(Paint.Style.FILL);
                    mDownloadPaint.setColor(Color.parseColor("#56ee9a"));
                    canvas.drawCircle(w - h / 2, h / 2, successProgress, mDownloadPaint);
                }
            } else {
                //下载未出错
                mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
                canvas.drawArc(downloadRectF, 0, 360, false, mDownloadPaint);
                @SuppressLint("DrawAllocation") Path arrowPath = new Path();
                if (prepareProgress == 1) {
                    arrowPath.moveTo(w - h / 2, h * 2 / 5);
                    arrowPath.rLineTo(0, h / 5);
                    arrowPath.rQuadTo(-(h / 5 / 2), 0, -(h / 5 / 2), -(h / 5 / 2));
                    arrowPath.moveTo(w - h / 2, h * 3 / 5);
                    arrowPath.rQuadTo(h / 5 / 2, 0, h / 5 / 2, -(h / 5 / 2));
                    mDownloadPaint.setStrokeWidth(h / 15);
                    canvas.drawPath(arrowPath, mDownloadPaint);
                } else if (prepareProgress == 2) {
                    arrowPath.moveTo(w - h / 2, h / 5);
                    arrowPath.rLineTo(0, h * 2 / 5);
                    arrowPath.rQuadTo(-(h / 5 / 4), 0, -(h / 5 / 4), -(h / 5 / 4));
                    arrowPath.moveTo(w - h / 2, h * 3 / 5);
                    arrowPath.rQuadTo(h / 5 / 4, 0, h / 5 / 4, -(h / 5 / 4));
                    mDownloadPaint.setStrokeWidth(h / 16);
                    canvas.drawPath(arrowPath, mDownloadPaint);
                } else if (prepareProgress == 3) {
                    arrowPath.moveTo(w - h / 2, h / 5);
                    arrowPath.rLineTo(0, h * 2 / 5);
                    arrowPath.rQuadTo(-(h / 5 / 5), 0, -(h / 5 / 5), -(h / 5 / 5));
                    arrowPath.moveTo(w - h / 2, h * 3 / 5);
                    arrowPath.rQuadTo(h / 5 / 5, 0, h / 5 / 5, -(h / 5 / 5));
                    mDownloadPaint.setStrokeWidth(h / 17);
                    canvas.drawPath(arrowPath, mDownloadPaint);

                } else if (prepareProgress == 4) {
                    arrowPath.moveTo(w - h / 2, h / 5);
                    arrowPath.lineTo(w - h / 2, h / 2);
                    mDownloadPaint.setStrokeWidth(h / 18);
                    canvas.drawPath(arrowPath, mDownloadPaint);
                } else if (prepareProgress == 5) {
                    arrowPath.moveTo(w - h / 2, h / 5);
                    arrowPath.lineTo(w - h / 2, h / 2);
                    mDownloadPaint.setStrokeWidth(h / 20);
                    canvas.drawPath(arrowPath, mDownloadPaint);
                } else if (prepareProgress == 6) {
                    //开始下载动画
                    if (progress != max) {
                        mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
                        mDownloadPaint.setStyle(Paint.Style.FILL);
                        float sweepAngle = ((360 / max) * progress);
                        canvas.drawArc(downloadRectF2, -90, sweepAngle, true, mDownloadPaint);
                    } else {
                        mDownloadPaint.setColor(Color.parseColor("#ecf0f1"));
                        mDownloadPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(w - h / 2, h / 2, h / 2 - h / 5, mDownloadPaint);
                    }
                }
            }

        }

        //画柱状波形图
        if (mFft == null) {
            mPathPaint.setStyle(Paint.Style.STROKE);
            mPathPaint.setStrokeWidth(w2 / 20);
            mPathPaint.setColor(Color.WHITE);
            mPathPaint.setStrokeCap(Paint.Cap.ROUND);
            for (int i = 1; i < 13; i++) {
                if(i%2==0)canvas.drawLine(w2 * i / 13,h * 5 / 8,w2 * i / 13,h * 3 / 8, mPathPaint);
                else canvas.drawLine(w2 * i / 13,h * 3 / 4,w2 * i / 13,h * 1 / 4, mPathPaint);
            }
        } else {
            mPathPaint.setStyle(Paint.Style.STROKE);
            mPathPaint.setStrokeWidth(w2 / 20);
            mPathPaint.setStrokeCap(Paint.Cap.ROUND);

            for (int i = 1; i < 13; i++) {
                if (mFft[i] > h / 2 || mFft[i] == 0 || mFft[i] < 0)
                    mFft[i] = (byte) random.nextInt(h / 2);

                canvas.drawLine(w2 * i / 13,h * 3 / 4,w2 * i / 13,h * 3 / 4 - mFft[i], mPathPaint);
            }
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setUrl(String url) {
        this.url = url;
        mFileIsDownload = fileIsExists(url);
        invalidate();
    }
}

