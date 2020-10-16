package com.edu.cdp.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.edu.cdp.R;

import java.io.IOException;
import java.util.Random;

public class VoiceView extends View implements View.OnClickListener {
    private Paint mPaint;
    private Path path, spectrum;


    private boolean mPlayStatus = false;
    private byte[] mFft;
    private float[] mPoints;
    private MediaPlayer mMediaPlayer;
    private Visualizer visualizer;
    private Random random;


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
        mPaint = p;

        spectrum = new Path();

        random = new Random();

        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        playAudio();

    }

    public void playAudio() {
//        if(url==null||url.equals(""))return;
        mMediaPlayer = MediaPlayer.create(getContext(), R.raw.undernoflag);
//        mMediaPlayer.setDataSource(getContext(), Uri.parse(url));
//        mMediaPlayer.prepare();
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



        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                visualizer.setEnabled(false);
                if (visualizer != null) {
                    visualizer.release();
                }
                mFft =null;
                invalidate();
            }
        });

    }


    public void stopPlay() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()){
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
        specWidth = Math.max(dip2px(100), width);

        specHeight = specWidth / 3;

        setMeasuredDimension(specWidth, specHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();


        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#2ecc71"));
        canvas.drawPath(path, mPaint);

        //画柱状波形图
        if (mFft == null) {
            spectrum.reset();
            spectrum.moveTo(w / 10, h * 2 / 3);
            spectrum.rLineTo(w * 8 / 10, 0);
            spectrum.close();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(10);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawPath(spectrum, mPaint);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(10);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);


            mPoints = new float[mFft.length * 4];
            for (int i = 1; i < 15; i++) {
                if(mFft[i]>h/2 || mFft[i] == 0 || mFft[i] < 0)
                    mFft[i] = (byte) random.nextInt(h/2);


                mPoints[i * 4] = w * i / 15;
                mPoints[i * 4 + 1] = h * 3 / 4;
                mPoints[i * 4 + 2] = w * i / 15;
                mPoints[i * 4 + 3] = h * 3 / 4 - mFft[i];
            }
            canvas.drawLines(mPoints, mPaint);
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


    public interface playListener{
        void onPlay();
    }

}
