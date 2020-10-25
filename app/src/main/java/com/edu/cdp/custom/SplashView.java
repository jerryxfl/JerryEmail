package com.edu.cdp.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

//飞溅动画
public class SplashView extends View {
    private Context context;
    private int w, h;

    //动画
    private ValueAnimator rotateAnimator;
    private ValueAnimator scaleAnimator;

    //背景
    private Paint mBackgroundPaint;
    private int mBackgroundColor = Color.WHITE;


    //中间圆的半径
    private int centerRadius;

    //加载动画基础设置
    private float littleCircleDegree=0;
    private Paint mLoadingCirclesPaint;
    private int littleCircleRadius;
    private int[] littleCircleColors=new int[]{Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.LTGRAY,Color.GRAY};
    private float distance=0;


    //状态类
    private SplashState splashState;


    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //初始化
    private void init(Context context) {
        this.context = context;


        mLoadingCirclesPaint = new Paint();
        mLoadingCirclesPaint.setStyle(Paint.Style.FILL);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        this.centerRadius = w/8;
        this.littleCircleRadius = centerRadius/8;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (splashState == null) {
            //第一个旋转动画
            splashState = new RotateState();
        }
        splashState.drawState(canvas);

    }


    //画背景
    private void drawBackground(Canvas canvas){
        canvas.drawColor(mBackgroundColor);
    }

    //画圆
    private void drawCircle(Canvas canvas){
        float rotateAngle = (float) (Math.PI*2/littleCircleColors.length);
        for (int i = 0; i < littleCircleColors.length; i++) {
            float degree = rotateAngle*i+littleCircleDegree;
            mLoadingCirclesPaint.setColor(littleCircleColors[i]);
            canvas.drawCircle((float) (w/2+Math.cos(degree)*(centerRadius-distance)),
                    (float) (h/2+Math.sin(degree)*(centerRadius-distance)),
                    littleCircleRadius,mLoadingCirclesPaint);
        }
    }



    //动画基类
    private abstract class SplashState {
        abstract void drawState(Canvas canvas);
    }


    //旋转加载动画类
    private class RotateState extends SplashState{

        public RotateState() {
            rotateAnimator = ValueAnimator.ofFloat((float) (Math.PI*2),0);
            rotateAnimator.addUpdateListener(animation -> {
                littleCircleDegree = (float) animation.getAnimatedValue();
                invalidate();
            });
            rotateAnimator.setDuration(2000);
            rotateAnimator.setInterpolator(new LinearInterpolator());
            rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
            rotateAnimator.setRepeatCount(1);
            rotateAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    splashState = new ScaleState();
                }
            });
            rotateAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircle(canvas);
        }
    }


    //缩放
    private class ScaleState extends SplashState{

        public ScaleState() {
            scaleAnimator = ValueAnimator.ofFloat(centerRadius/2,centerRadius);
            scaleAnimator.addUpdateListener(animation -> {
                distance = (float) animation.getAnimatedValue();
                invalidate();
            });
            scaleAnimator.setDuration(1000);
            scaleAnimator.setInterpolator(new OvershootInterpolator(10f));
            scaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
            scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                }
            });
            scaleAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircle(canvas);
        }
    }





    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
