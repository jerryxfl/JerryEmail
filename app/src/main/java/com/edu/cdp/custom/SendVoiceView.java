package com.edu.cdp.custom;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaRecorder;
import android.os.FileUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.utils.VibrationUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 发送语音
 */
//@SuppressWarnings("all")
public class SendVoiceView extends View implements View.OnTouchListener {
    //基本
    private Context context;
    private BaseActivity baseActivity;//通过baseActivity请求权限
    private int w;


    //画话筒需要的基础，画笔，画笔颜色，话筒路径
    private Paint micPaint;
    private int micColor = Color.GRAY;
    private Bitmap micBitmap;


    //画录音圆弧需要的东西
    private int[] circleColor = new int[]{Color.parseColor("#95de64"), Color.parseColor("#d9f7be")};
    private Paint circlePaint1;
    private Paint circlePaint2;
    private int circleRadius1 = 0;
    private int circleRadius2 = 0;

    //监听器
    private Listener listener;

    public SendVoiceView(Context context) {
        this(context, null);
    }

    public SendVoiceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SendVoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 设置监听器
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * 初始化控件
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.baseActivity = (BaseActivity) context;
        //获取自定义属性  注意获取后要回收
        if (attrs != null) {

        }
        //初始化话筒画笔
        micPaint = new Paint();
        micPaint.setAntiAlias(true);
        micPaint.setDither(true);
        micPaint.setStyle(Paint.Style.FILL);
        micPaint.setColor(micColor);

        //初始化话筒bitmap
        micBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.voice);


        //初始化圆的画笔
        circlePaint1 = new Paint();
        circlePaint1.setAntiAlias(true);
        circlePaint1.setDither(true);
        circlePaint1.setStyle(Paint.Style.FILL);
        circlePaint1.setColor(circleColor[0]);

        circlePaint2 = new Paint();
        circlePaint2.setAntiAlias(true);
        circlePaint2.setDither(true);
        circlePaint2.setStyle(Paint.Style.FILL);
        circlePaint2.setColor(circleColor[1]);

        //设置onTouch事件
        setOnTouchListener(this::onTouch);
    }

    /**
     * 控件大小发生变化
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println("onSizeChanged");

        //获得宽高
        this.w = w;

        //初始化录音动画
        initRecordAnimation();
    }

    /*
     *测量控件宽高，设置控件的高等于控件的宽
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }


    /**
     * 绘制控件
     *
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画动态圆
        canvas.drawCircle(w / 2, w / 2, circleRadius1, circlePaint1);
        canvas.drawCircle(w / 2, w / 2, circleRadius2, circlePaint2);

        //画话筒
        canvas.drawBitmap(micBitmap, new Rect(
                0, 0, micBitmap.getWidth(), micBitmap.getHeight()
        ), new RectF(
                w *3/ 10, w *3/ 10, w * 7/ 10, w * 7 / 10
        ), micPaint);
    }


    //是否在播放动画
    private boolean isOnClicking = false;
    //是否在录音
    private boolean isRecording = false;
    //录音文件保存地址
    private String filePath;

    /**
     * 监听布局被点击
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //获取录音权限和读取内存权限

            if (baseActivity != null) {
                if (baseActivity.RequestPermission(Manifest.permission.RECORD_AUDIO) &&
                        baseActivity.RequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        baseActivity.RequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                    onDown();
            } else Toast.makeText(context, "获取权限异常", Toast.LENGTH_SHORT).show();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onUp();
            return true;
        }
        return false;
    }

    /**
     * 手指按下
     */
    private void onDown() {
        if (!isOnClicking) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.8f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.8f);
            set.playTogether(scaleX, scaleY);
            set.setDuration(200);
            set.setInterpolator(new BounceInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                }

                @Override
                public void onAnimationStart(Animator animation) {
                    VibrationUtils.Vibrator((Activity) context, new long[]{100, 200}, -1);
                    isOnClicking = true;
                    super.onAnimationStart(animation);
                    //播放录音动画
                    startRecordAnimation();
                    //开始录制音频
                    startRecord();
                }
            });
            set.start();
        }
    }

    /**
     * 手指抬起
     */
    private void onUp() {
        if (isOnClicking) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.8f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.8f, 1f);
            set.playTogether(scaleX, scaleY);
            set.setDuration(200);
            set.setInterpolator(new BounceInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isOnClicking = false;
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    //停止录音动画
                    stopRecordAnimation();
                    //停止录音
                    stopRecord();
                }
            });
            set.start();
        }
    }

    //录音动画对象
    private ValueAnimator animation1;
    private ValueAnimator animation2;

    /**
     * 录音动画
     */
    private void initRecordAnimation() {
        animation1 = ValueAnimator.ofInt(0, w / 2);
        animation1.setDuration(2000);
        animation1.setRepeatCount(ValueAnimator.INFINITE);
        animation1.setRepeatMode(ValueAnimator.REVERSE);
        animation1.addUpdateListener(animation -> {
            circleRadius1 = (int) animation.getAnimatedValue();
            invalidate();
        });
        animation1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                circleRadius1 = 0;
                invalidate();
            }
        });


        animation2 = ValueAnimator.ofInt(0, w / 2);
        animation2.setDuration(1000);
        animation2.setRepeatCount(ValueAnimator.INFINITE);
        animation2.setRepeatMode(ValueAnimator.REVERSE);
        animation2.addUpdateListener(animation -> {
            circleRadius2 = (int) animation.getAnimatedValue();
            invalidate();
        });
        animation2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                circleRadius2 = 0;
                invalidate();
            }
        });
    }

    /**
     * 开始录音动画
     */
    private void startRecordAnimation() {
        animation1.start();
        animation2.start();
    }

    /**
     * 停止录音动画
     */
    private void stopRecordAnimation() {
        animation1.cancel();
        animation2.cancel();
    }


    //录音对象
    private MediaRecorder mMediaRecorder;
    /**
     * 录制音频 保存到 record文件夹下面
     */
    private void startRecord() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        if (!isRecording) {
            try {
                //设置音源为麦克风
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //设置输出文件格式
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                //设置音频编码
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                String saveDir = getContext().getFilesDir().toString() + File.separator + "record" + File.separator;
                String fileName = new Date().getTime() + ".m4a";
                filePath = saveDir + fileName;
                File file = new File(saveDir);
                if (!file.mkdir()) {
                    file.createNewFile();
                }
                mMediaRecorder.setOutputFile(filePath);
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                isRecording = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 停止录音
     */

    private void stopRecord() {
        if (isRecording) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                if(listener!=null)listener.recordSuccess(filePath);
            } catch (Exception e) {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                File file = new File(filePath);
                if (file.exists()) file.delete();
                if(listener!=null)listener.recordFailure();
            }
            filePath = "";
            isRecording = false;
        }
    }


    /**
     * 录制监听器
     */
    public interface Listener {
        void recordSuccess(String path);

        void recordFailure();
    }
}
