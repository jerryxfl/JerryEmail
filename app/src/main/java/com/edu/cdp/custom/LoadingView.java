package com.edu.cdp.custom;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.edu.cdp.R;

public class LoadingView extends View {
    private int loadingStyle = LoadingStyle.bubbles;
    private int indicatorColor = Color.parseColor("#2ed573");//指示器颜色
    private int indicatorBottomColor = Color.parseColor("#bdc3c7");//指示器底部颜色
    private Paint indicatorPaint;//指示器画笔
    private int strokeWidth = 15;
    private ValueAnimator valueAnimator;
    private float loadingProgress=0;



    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
            indicatorColor = a.getColor(R.styleable.LoadingView_indicatorColor,Color.parseColor("#2ed573"));
            indicatorBottomColor = a.getColor(R.styleable.LoadingView_indicatorBottomColor,Color.parseColor("#bdc3c7"));
            strokeWidth = (int) a.getDimension(R.styleable.LoadingView_indicatorWidth,15);
            loadingStyle = a.getInt(R.styleable.LoadingView_loadingStyle,4);
            a.recycle();
        }

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(indicatorColor);
        indicatorPaint = p;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //at_most wrap_content
        //exactly match_parent
        //unexpect 未指定

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int distance = Math.min(width,height);

        int specWidth = 0, specHeight = 0;

        if (widthMode == MeasureSpec.AT_MOST) {
            specWidth = distance;
        } else if (widthMode == MeasureSpec.EXACTLY) {
            specWidth = distance;
        } else if (width == MeasureSpec.UNSPECIFIED) {
            specWidth = dip2px(getContext(), 50);
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            specHeight = distance;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            specHeight = distance;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            specHeight = dip2px(getContext(), 50);
        }

        setMeasuredDimension(specWidth,specHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();


        @SuppressLint("DrawAllocation") RectF rectF = new RectF(
                strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth
        );
        switch (loadingStyle) {
            case LoadingStyle.point:
                indicatorPaint.setStyle(Paint.Style.STROKE);
                indicatorPaint.setStrokeWidth(strokeWidth);
                indicatorPaint.setStrokeCap(Paint.Cap.ROUND);
                indicatorPaint.setColor(indicatorBottomColor);
                canvas.drawCircle(width/2,height/2,(width-strokeWidth*2)/2,indicatorPaint);
                indicatorPaint.setColor(indicatorColor);
                canvas.drawArc(rectF, loadingProgress, strokeWidth, false, indicatorPaint);
                break;
            case LoadingStyle.circle:
                indicatorPaint.setStyle(Paint.Style.STROKE);
                indicatorPaint.setStrokeWidth(strokeWidth);
                indicatorPaint.setStrokeCap(Paint.Cap.ROUND);
                indicatorPaint.setColor(indicatorColor);
                canvas.drawArc(rectF, loadingProgress, 210, false, indicatorPaint);
                break;
            case LoadingStyle.shutters:
                indicatorPaint.setStyle(Paint.Style.STROKE);
                indicatorPaint.setStrokeWidth(strokeWidth);
                indicatorPaint.setStrokeCap(Paint.Cap.ROUND);
                indicatorPaint.setColor(indicatorColor);
                for (int i = 0; i < 8; i++) {
                    canvas.drawArc(rectF, loadingProgress+strokeWidth*3*i, strokeWidth, false, indicatorPaint);
                }
                break;
            case LoadingStyle.bubbles:
                indicatorPaint.setStyle(Paint.Style.FILL);
                indicatorPaint.setStrokeCap(Paint.Cap.ROUND);
                indicatorPaint.setColor(indicatorColor);
//                (x-100)²+（y-100）²=100    PT{100,100}

                //  (X-centerX)^2 + (y-centerY)^2 = radius^2  O{centerX,centerY}
                //假设圆心的坐标为(a,b)，那么圆的方程是(x-a)^2＋(y-b)^2＝r^2  已知角度m，圆上点的坐标分别是（r*cosm+a，r*sinm+b）
                int centerX = width/2;
                int centerY = height/2;

                int radius = centerX-strokeWidth;

                canvas.save();
                canvas.rotate(loadingProgress,centerX,centerY);
                for (int i = 1; i <=12; i++) {
                    canvas.drawCircle(
                            (float) (radius*Math.cos(Math.toRadians(30*i))+centerX),
                            (float) (radius*Math.sin(Math.toRadians(30*i))+centerY),
                            strokeWidth*i/18,
                            indicatorPaint);
                }
                canvas.restore();
                break;
        }
        LaunchAnimation();
    }

    private  void  LaunchAnimation(){
        if(valueAnimator==null){
            valueAnimator = ValueAnimator.ofFloat(0,360);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    loadingProgress = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
            valueAnimator.start();
        }

    }

    public void setLoadingStyle(int loadingStyle) {
        this.loadingStyle = loadingStyle;
        invalidate();
    }

    public void setIndicatorColor(int indicatorColor){
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorBottomColor(int indicatorBottomColor){
        this.indicatorBottomColor = indicatorBottomColor;
        invalidate();
    }

    public void setIndicatorWidth(int width){
        this.strokeWidth = width;
        invalidate();
    }



    public class LoadingStyle{
        public static  final  int point =1;
        public static  final  int circle =2;
        public static  final  int shutters =3;
        public static  final  int bubbles =4;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
