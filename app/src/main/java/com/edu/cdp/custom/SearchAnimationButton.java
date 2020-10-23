package com.edu.cdp.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import static android.view.animation.Animation.INFINITE;

public class SearchAnimationButton extends View implements View.OnClickListener {
    private Context mContext;
    private int w, h;
    private int strokeWidth = dip2px(10);
    private float progress;

    //普通状态
    private Paint normalPaint;
    private Path normalPath;


    //正在搜索
    private Paint searchPaint;
    private Path searchPath;

    //搜索完成
    private Paint endPaint;
    private Path endPath;


    //NORMAL
    private int status = Status.NORMAL;


    public class Status {
        public static final int NORMAL = 0;
        public static final int TOSEARCH = 1;
        public static final int SEARCHING = 2;
        public static final int END = 3;
    }


    public SearchAnimationButton(Context context) {
        this(context, null);
    }

    public SearchAnimationButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchAnimationButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        this.mContext = context;
        if (attrs != null) {

        }
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLUE);
        p.setStrokeCap(Paint.Cap.ROUND);
        normalPaint = p;
        searchPaint = p;
        endPaint = p;



        normalPath = new Path();
        searchPath = new Path();
        endPath = new Path();

        setOnClickListener(this::onClick);
    }


    @Override
    public void onClick(View v) {
        start();
    }


    public void start() {
        status = Status.TOSEARCH;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1,0);
        ValueAnimator searchValueAnimator = ValueAnimator.ofFloat(1,0);
        ValueAnimator endValueAnimator = ValueAnimator.ofFloat(0,1);


        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                status =Status.SEARCHING;
                searchValueAnimator.start();
            }
        });
        valueAnimator.start();


        searchValueAnimator.setDuration(2000);
        searchValueAnimator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        searchValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(status==Status.SEARCHING){
                    status=Status.END;
                    searchValueAnimator.start();
                }
                if(status==Status.END)endValueAnimator.start();
            }
        });


        endValueAnimator.setDuration(3000);
        endValueAnimator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        endValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    status = Status.NORMAL;
                    invalidate();
                },2000);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        this.strokeWidth = w/14;
        normalPath.reset();
        normalPath.addCircle(w / 2, h / 2, w / 4 - strokeWidth, Path.Direction.CW);
        normalPath.moveTo(w / 2 + (float) Math.cos(Math.toRadians(45)) * w / 4, w / 2 + (float) Math.sin(Math.toRadians(45)) * w / 4);
        normalPath.lineTo(w / 2 + (float) Math.cos(Math.toRadians(45)) * w / 2, w / 2 + (float) Math.sin(Math.toRadians(45)) * w / 2);
        normalPath.close();


        searchPath.reset();
        searchPath.addCircle(w / 2, h / 2, w / 4 - strokeWidth, Path.Direction.CW);
        searchPath.close();

        endPath.reset();
        endPath.moveTo(w/4,h/2);
        endPath.lineTo(w/2,h*3/4);
        endPath.lineTo(w*7/8,h*3/8);


        normalPaint.setStrokeWidth(strokeWidth);
        searchPaint.setStrokeWidth(strokeWidth);
        endPaint.setStrokeWidth(strokeWidth);
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
        if (widthMode == MeasureSpec.AT_MOST) {
            specWidth = Math.max(width, dip2px(20));
        } else if (widthMode == MeasureSpec.EXACTLY) {
            specWidth = width;
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            specWidth = Math.max(width, dip2px(20));
        }
        specHeight = specWidth;
        setMeasuredDimension(specWidth, specHeight);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (status) {
            case Status.NORMAL:
                canvas.drawPath(normalPath, normalPaint);
                break;
            case Status.TOSEARCH:
                PathMeasure pathMeasure = new PathMeasure();
                pathMeasure.setPath(normalPath, true);
                Path p = new Path();
                float stop = pathMeasure.getLength()*progress;
                float start = (float) (stop - ((0.5 - Math.abs(progress - 0.5)) * pathMeasure.getLength()));

                pathMeasure.getSegment(start,stop,p,true);
                canvas.drawPath(p, normalPaint);

                pathMeasure.nextContour();
                stop = pathMeasure.getLength()*progress;
                pathMeasure.getSegment(0,stop,p,true);
                canvas.drawPath(p, normalPaint);
                break;
            case Status.SEARCHING:
                pathMeasure = new PathMeasure();
                pathMeasure.setPath(searchPath, true);
                p = new Path();
                stop = pathMeasure.getLength()*progress;
                start = (float) (stop - ((0.5 - Math.abs(progress - 0.5)) * pathMeasure.getLength()));
                pathMeasure.getSegment(start,stop,p,true);
                canvas.drawPath(p, searchPaint);

                break;
            case Status.END:
                pathMeasure = new PathMeasure();
                pathMeasure.setPath(endPath, false);
                p = new Path();
                stop = pathMeasure.getLength()*progress;
                pathMeasure.getSegment(0,stop,p,true);
                canvas.drawPath(p, endPaint);
                break;
        }


    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
