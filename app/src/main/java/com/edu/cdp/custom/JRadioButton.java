package com.edu.cdp.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;

import androidx.annotation.Nullable;

//动效选择控件
public class JRadioButton extends View implements View.OnTouchListener {
    private Context mContext;
    private Paint outlinePaint;
    private Paint centerPaint;
    private int outlineWidth;
    private int w ,h;

    private int  selected= Color.BLUE,unSelected = Color.GRAY;

    private boolean onChange = false;

    private float progress = 0;

    private CheckListener checkListener;

    public JRadioButton(Context context) {
        this(context,null);
    }

    public JRadioButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public JRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        if(attrs != null){

        }


        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        outlinePaint = p;
        outlinePaint.setStyle(Paint.Style.STROKE);


        p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        centerPaint = p;
        centerPaint.setStyle(Paint.Style.FILL);

        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            changeStatus();
            return true;
        }
        return false;
    }


    private void changeStatus() {
        if(!onChange){
            if(checkListener!=null)checkListener.onCLick();
            AnimatorSet set = new AnimatorSet();

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this,"scaleX",1,0.8f,1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this,"scaleY",1,0.8f,1);

            ValueAnimator valueAnimator = null;
            if(progress==0){
                valueAnimator = ValueAnimator.ofFloat(progress,w/2-outlineWidth*2);
                valueAnimator.addUpdateListener(animation -> {
                    progress = (float) animation.getAnimatedValue();
                    invalidate();
                });

                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        progress = w/2-outlineWidth*2;
                        if(checkListener!=null)checkListener.onUnCheck();
                    }
                });
            }else{
                valueAnimator = ValueAnimator.ofFloat(progress,0);
                valueAnimator.addUpdateListener(animation -> {
                    progress = (float) animation.getAnimatedValue();
                    invalidate();
                });

                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        progress = 0;
                        if(checkListener!=null)checkListener.onCheck();
                    }
                });
            }

            set.playTogether(scaleX,scaleY,valueAnimator);
            set.setDuration(200);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    onChange = false;
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    onChange = true;
                }
            });
            set.start();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int specWidth = 0,specHeight = 0;
        //at_most wrap_content
        //exactly match_parent  65dp
        //unexpect 未指定
        if(widthMode == MeasureSpec.AT_MOST){
            Log.i("MEASURE","AT_MOST");
            specWidth = Math.max(width,dip2px(10));
        }else if(widthMode == MeasureSpec.EXACTLY){
            Log.i("MEASURE","EXACTLY");
            specWidth = Math.max(width,dip2px(10));
        }else if(widthMode == MeasureSpec.UNSPECIFIED){
            Log.i("MEASURE","UNSPECIFIED");
            specWidth = dip2px(10);
        }

        specHeight = specWidth;
        setMeasuredDimension(specWidth, specHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        this.outlineWidth = w/10;
        outlinePaint.setStrokeWidth(outlineWidth);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(progress>(w/2-outlineWidth*2)/2){
            outlinePaint.setColor(unSelected);
            centerPaint.setColor(unSelected);
        }else{
            outlinePaint.setColor(selected);
            centerPaint.setColor(selected);
        }

        canvas.drawCircle(w/2,h/2,w/2-outlineWidth,outlinePaint);


        canvas.drawCircle(w/2,h/2,w/2-outlineWidth*2-progress,centerPaint);

    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public boolean isCheck(){
        return  progress<(w/2-outlineWidth*2)/2;
    }


    public void setCheck(boolean check){
        if(check&&!isCheck()){
            changeStatus();
        }else if(!check&&isCheck()){
            changeStatus();
        }
    }

    public  void  setCheckListener(CheckListener checkListener){
        this.checkListener = checkListener;
    }

    public  void  setColor(int selectColor,int unSelectColor){
        this.selected = selectColor;
        this.unSelected = unSelectColor;
        invalidate();
    }


    public interface CheckListener{
        void onCLick();

        void onCheck();

        void onUnCheck();
    }

}
