package com.edu.cdp.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.edu.cdp.R;

public class SwitchButton extends View implements View.OnTouchListener {
    private Context mContext;
    private int mShadowWidth;
    private int w,h,circleStockWidth;
    private int mBodyColorOpen = Color.GREEN;
    private int mBodyColorClose = Color.RED;
    private int mTouchColor = Color.WHITE;
    private boolean onChange = false;

    private Paint mBodyPaint,mTouchPaint;
    private Path mBodyPath;

    private CheckListener checkListener;

    private float progress = 0,progress1 = 0,end=0,changeLength=0,changeLength1=0;

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
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
            mBodyColorOpen = a.getColor(R.styleable.SwitchButton_bodyColorOpen,Color.GREEN);
            mBodyColorClose = a.getColor(R.styleable.SwitchButton_bodyColorClose,Color.RED);
            mTouchColor = a.getColor(R.styleable.SwitchButton_touchColor,Color.WHITE);
            a.recycle();
        }
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        mBodyPaint = p;
        mTouchPaint = p;

        mBodyPath = new Path();

        setOnTouchListener(this::onTouch);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int specWidth = 0, specHeight = 0;
        //at_most wrap_content
        //exactly match_parent  65dp
        //unexpect 未指定

        if(widthMode == MeasureSpec.AT_MOST){
            Log.i("MEASURE","AT_MOST");
            specWidth = Math.min(width,dip2px(10));
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
        this.circleStockWidth =h/6;
        this.changeLength = (float) (w*3/4-circleStockWidth*2-circleStockWidth/4);
        this.changeLength1 = (float) (circleStockWidth*2+w/4+circleStockWidth/4);
        this.end = changeLength;
        this.mShadowWidth = h / 10;

        int b = (int) (mShadowWidth*1.5);//padding

        mBodyPath.reset();
        mBodyPath.addArc(new RectF(
                b,b,w/2-b,h-b
        ),0,360);
        mBodyPath.addRect(new RectF(
                w/4,b,w*3/4,h-b
        ), Path.Direction.CW);
        mBodyPath.addArc(new RectF(
                w/2+b,b,w-b,h-b
        ),0,360);
        mBodyPath.close();

        requestLayout();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(progress<changeLength/2){
            mBodyPaint.setColor(mBodyColorClose);
            mBodyPaint.setShadowLayer(mShadowWidth,0,mShadowWidth/2,mBodyColorClose);        }
        else {
            mBodyPaint.setColor(mBodyColorOpen);
            mBodyPaint.setShadowLayer(mShadowWidth,0,mShadowWidth/2,mBodyColorOpen);
        }

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

    private void startCheck(){
        if(!onChange){
            if(checkListener!=null)checkListener.onClick();
            Toast.makeText(mContext,"onclick",Toast.LENGTH_SHORT).show();
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this,"scaleX",1,0.9f,1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this,"scaleY",1,0.9f,1);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(progress,end);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new BounceInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                progress = (float) animation.getAnimatedValue();
                System.out.println("progress: "+progress+"  w/2: "+changeLength);
                if(progress<=changeLength1){
                    progress1 = progress;
                }
                invalidate();
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(end==changeLength){
                        progress = changeLength;
                        end = 0;
                        if(checkListener!=null)checkListener.onOpen();
                    }else{
                        end = changeLength;
                        progress = 0;
                        if(checkListener!=null)checkListener.onClose();
                    }
                    onChange = false;
                }
            });

            set.playTogether(scaleX,scaleY);
            set.setDuration(200);
            set.setInterpolator(new BounceInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    onChange = true;
                    super.onAnimationStart(animation);
                }

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

    public boolean isChecked() {
        return progress>=changeLength/2;
    }

    public void setChecked(boolean checked) {
        if(checked){
            if(!isChecked()) startCheck();
        }else{
            if(isChecked()) startCheck();
        }
    }

    public void setColors(int bodyColorOpen,int bodyColorClose, int touchedColor) {
        this.mBodyColorOpen = bodyColorOpen;
        this.mBodyColorClose = bodyColorClose;
        this.mTouchColor = touchedColor;
        invalidate();
    }

    public void addCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            startCheck();
            return true;
        }
        return false;
    }


    public interface CheckListener {
        void onClick();
        
        void onOpen();

        void onClose();
    }
}
