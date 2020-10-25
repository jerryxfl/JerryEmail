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
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import static android.view.animation.Animation.INFINITE;

public class SearchAnimationButton extends View {
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
    private boolean isFirst = true;

    //搜索完成
    private Paint endPaint;
    private Path endPath;


    private SearchListener searchListener;

    //NORMAL
    private int status = Status.NORMAL;
    private ValueAnimator valueAnimator;
    private ValueAnimator searchValueAnimator;
    private ValueAnimator endValueAnimator;

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
        normalPaint = p;
        searchPaint = p;
        searchPaint.setStrokeCap(Paint.Cap.ROUND);
        endPaint = p;
        endPaint.setStrokeCap(Paint.Cap.ROUND);
        endPaint.setStrokeJoin(Paint.Join.ROUND);

        normalPath = new Path();
        searchPath = new Path();
        endPath = new Path();

    }


    public void startSearch() {
        if (status == Status.NORMAL) {
            if (valueAnimator == null) {
                valueAnimator = ValueAnimator.ofFloat(1, 0);
                searchValueAnimator = ValueAnimator.ofFloat(1, 0);
                endValueAnimator = ValueAnimator.ofFloat(0, 1);
                initializeAnimator();
            }
            status = Status.TOSEARCH;
            valueAnimator.start();
        }
    }


    private void initializeAnimator() {
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                status = Status.SEARCHING;
                if (searchListener != null) searchListener.onSearching();
                searchValueAnimator.start();
            }
        });


        searchValueAnimator.setDuration(1000);
        searchValueAnimator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        searchValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (status == Status.SEARCHING) {
                    isFirst = false;
                    searchValueAnimator.start();
                }
                if (status == Status.END) {
                    isFirst = true;
                    endValueAnimator.start();
                }
            }
        });


        endValueAnimator.setDuration(500);
        endValueAnimator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        endValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    status = Status.NORMAL;
                    invalidate();
                    if (searchListener != null) searchListener.onSearchComplete();
                }, 2000);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        this.strokeWidth = w / 14;
        normalPath.reset();
        normalPath.moveTo(w / 2 + (float) Math.cos(Math.toRadians(45)) * w / 4, w / 2 + (float) Math.sin(Math.toRadians(45)) * w / 4);
        normalPath.lineTo(w / 2 + (float) Math.cos(Math.toRadians(45)) * w / 2, w / 2 + (float) Math.sin(Math.toRadians(45)) * w / 2);
        normalPath.close();


        searchPath.reset();
        searchPath.addCircle(w / 2, h / 2, w / 4 - strokeWidth, Path.Direction.CW);
        searchPath.close();

        endPath.reset();
        endPath.moveTo(w / 4, h / 2);
        endPath.lineTo(w / 2, h * 3 / 4);
        endPath.lineTo(w * 7 / 8, h * 3 / 8);


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
        specWidth = width;

        specHeight = specWidth;
        setMeasuredDimension(specWidth, specHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (status) {
            case Status.NORMAL:
                canvas.drawPath(normalPath, normalPaint);
                canvas.drawPath(searchPath, normalPaint);
                break;
            case Status.TOSEARCH:
                canvas.drawPath(searchPath, normalPaint);
                PathMeasure pathMeasure = new PathMeasure();
                pathMeasure.setPath(normalPath, true);
                Path p = new Path();
                float stop = pathMeasure.getLength() * progress;
                pathMeasure.getSegment(0, stop, p, true);
                canvas.drawPath(p, normalPaint);
                break;
            case Status.SEARCHING:
                pathMeasure = new PathMeasure();
                pathMeasure.setPath(searchPath, true);
                p = new Path();
                stop = pathMeasure.getLength() * progress;
                float start = 0;
                if (!isFirst)
                    start = (float) (stop - ((0.5 - Math.abs(progress - 0.5)) * pathMeasure.getLength()));
                pathMeasure.getSegment(start, stop, p, true);
                canvas.drawPath(p, searchPaint);
                break;
            case Status.END:
                pathMeasure = new PathMeasure();
                pathMeasure.setPath(endPath, false);
                p = new Path();
                stop = pathMeasure.getLength() * progress;
                pathMeasure.getSegment(0, stop, p, true);
                canvas.drawPath(p, endPaint);
                break;
        }
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public void setLoadingComplete() {
        if (status == Status.SEARCHING) status = Status.END;
        else if(status == Status.TOSEARCH){
            new Handler(Looper.getMainLooper()).postDelayed(()->{
                status = Status.END;
            },1000);
        }
    }

    public void reset() {
        status = Status.NORMAL;
        if(valueAnimator!=null){
            if (valueAnimator.isRunning()) valueAnimator.cancel();
            if (searchValueAnimator.isRunning()) searchValueAnimator.cancel();
            if (endValueAnimator.isRunning()) endValueAnimator.cancel();
        }
        invalidate();
    }


    public void setSearchListener(SearchListener searchListener) {
        this.searchListener = searchListener;
    }


    public interface SearchListener {
        void onSearching();

        void onSearchComplete();
    }
}
