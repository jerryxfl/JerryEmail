package com.edu.cdp.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SwitchButton extends View implements View.OnClickListener {
    private Context mContext;
    private int mShadowWidth;
    private int w,h,circleStockWidth;
    private int mBodyColor = Color.RED;
    private int mTouchColor = Color.WHITE;
    private boolean onChange = false;

    private Paint mBodyPaint,mTouchPaint;
    private Path mBodyPath;


    private int progress = 0,progress1 = 0,end=0,changeLength=0,changeLength1=0;

    public SwitchButton(Context context) {
        this(context,null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        if(attrs != null){
            //处理自定义属性


        }
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        mBodyPaint = p;
        mTouchPaint = p;

        mBodyPath = new Path();

        setOnClickListener(this::onClick);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int specWidth = 0, specHeight = 0;
        //at_most wrap_content
        //exactly match_parent  65dp
        //unexpect 未指定

        if(widthMode == MeasureSpec.AT_MOST){
            Log.i("MEASURE","AT_MOST");
            specWidth = Math.min(width,dip2px(50));
        }else if(widthMode == MeasureSpec.EXACTLY){
            Log.i("MEASURE","EXACTLY");
            specWidth = width;
        }else if(widthMode == MeasureSpec.UNSPECIFIED){
            Log.i("MEASURE","UNSPECIFIED");
            specWidth = width;
        }
        specHeight = specWidth/2;//高度为宽度的一半
        setMeasuredDimension(specWidth,specHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        this.circleStockWidth =h/7;
        this.changeLength = w*3/4-circleStockWidth*2-circleStockWidth/3;
        this.changeLength1 = circleStockWidth*2+w/4+circleStockWidth/3;
        this.end = changeLength;

        mBodyPath.reset();
        mBodyPath.addArc(new RectF(
                0,0,w/2,h
        ),-90,-180);
        mBodyPath.addRect(new RectF(
                w/4,h,w*3/4,0
        ), Path.Direction.CCW);
        mBodyPath.addArc(new RectF(
                w/2,0,w,h
        ),-90,180);
        mBodyPath.close();

        requestLayout();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(progress<changeLength/2)mBodyColor = Color.GREEN;
        else mBodyColor = Color.RED;

        mBodyPaint.setColor(mBodyColor);
        mBodyPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mBodyPath,mBodyPaint);

        mTouchPaint.setColor(mTouchColor);
        mTouchPaint.setStyle(Paint.Style.STROKE);
        mTouchPaint.setStrokeWidth(circleStockWidth);
        mTouchPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawOval(new RectF(
                circleStockWidth*2+progress,
                circleStockWidth*2,
                w/2-circleStockWidth*2+progress1,
                h-circleStockWidth*2
        ),mTouchPaint);
    }

    @Override
    public void onClick(View v) {
        if(!onChange){
            Toast.makeText(mContext,"onclick",Toast.LENGTH_SHORT).show();
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this,"scaleX",1,0.9f,1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this,"scaleY",1,0.9f,1);


            ValueAnimator valueAnimator = ValueAnimator.ofInt(progress,end);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new BounceInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                progress = (int) animation.getAnimatedValue();
                System.out.println("progress: "+progress+"  w/2: "+changeLength);
                if(progress<=changeLength1){
                    progress1 = progress;
                }
                invalidate();
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    onChange = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(end==changeLength){
                        progress = changeLength;
                        end = 0;
                    }else{
                        end = changeLength;
                        progress = 0;
                    }
                    onChange = false;
                }
            });

            set.playTogether(scaleX,scaleY);
            set.setDuration(200);
            set.setInterpolator(new BounceInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    valueAnimator.start();
                }
            });
            set.start();
        }
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
