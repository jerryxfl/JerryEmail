package com.edu.cdp.custom;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.edu.cdp.R;

public class AvatarView extends View {
    private BitmapShader mAvatarShader;
    private SweepGradient mSweepGradient;
    private Paint mPaint;
    private int mBorderWidth = 1;//dp
    private boolean loadCompleted = false;
    private float mStartAngle = 0;

    private Bitmap mAvatar;
    private boolean isOnLine = false;
    private int mMinRadius = 50;

    public AvatarView(Context context) {
        this(context,null);
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int mSrc = R.drawable.avatar;
        if(attrs!=null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarView);
            mMinRadius = a.getInteger(R.styleable.AvatarView_minRadius,50);
            mSrc = a.getResourceId(R.styleable.AvatarView_avatar,R.drawable.avatar);
            a.recycle();
        }
        mAvatar = BitmapFactory.decodeResource(getResources(),mSrc);
        if(isOnLine)createSweetGradient(new int[]{Color.WHITE,Color.GREEN,Color.WHITE});
        else createSweetGradient(new int[]{Color.WHITE,Color.RED,Color.WHITE});
        createPaint();

    }

    private void createPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(dip2px(getContext(),mBorderWidth));
        mPaint = paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if(wMode==MeasureSpec.AT_MOST){
            w = Math.min(w, dip2px(getContext(),mMinRadius));
        }else if(wMode==MeasureSpec.UNSPECIFIED){
            w = dip2px(getContext(),mMinRadius);
        }


        if(hMode==MeasureSpec.AT_MOST){
            h = Math.min(h, dip2px(getContext(),mMinRadius));
        }else if(hMode==MeasureSpec.EXACTLY){
            h = Math.min(h,w);
        }else if(hMode==MeasureSpec.UNSPECIFIED){
            h = dip2px(getContext(),mMinRadius);
        }
        setMeasuredDimension(w,h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        int radius = width/2;

        int avatarCenterX = (width+getPaddingEnd()+getPaddingStart())/2;
        int avatarCenterY = (height+getPaddingTop()+getPaddingBottom())/2;

        //画圆形头像
        createBitmapShader(mAvatar);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mAvatarShader);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(avatarCenterX, avatarCenterY,radius-dip2px(getContext(),5),mPaint);

        //画在线状态
        if(isOnLine)mPaint.setColor(Color.GREEN);
        else mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(null);
        canvas.drawCircle(width-radius/10,height-radius/10,radius/10,mPaint);

        //画圆形边框
        mBorderWidth = 3*px2dip(getContext(),width)/mMinRadius;
        mPaint.setStrokeWidth(dip2px(getContext(),mBorderWidth));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(mSweepGradient);

        int margin = dip2px(getContext(),mBorderWidth)/2;
        canvas.drawArc(new RectF(margin,margin,width-margin,height-margin),mStartAngle,270,false,mPaint);

        if(!loadCompleted){
            loadCompleted = true;
            startConnecting();
        }
    }

    private void startConnecting(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,360);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.start();
    }

    private void createBitmapShader(Bitmap bitmap) {
        if (bitmap==null) return;
        int minBitMap = Math.min(bitmap.getWidth(), bitmap.getHeight());
        //取view宽高中的小值 尽量保证图片内容的显示
        int minValue=Math.min(getWidth(),getHeight());
        //设置半径
        //计算缩放比例  一定要*1.0f 因为int之间的计算结果会四舍五入0或1 效果就不美丽了
        float scale=minValue*1.0f/minBitMap;
        //设置缩放比例
        Matrix matrix = new Matrix();
        matrix.setScale(scale,scale);
        /**
         * 创建着色器 设置着色模式
         * TileMode的取值有三种：
         *  CLAMP 拉伸  REPEAT 重复   MIRROR 镜像
         */
        mAvatarShader=new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //设置矩阵
        mAvatarShader.setLocalMatrix(matrix);
    }

    private void createSweetGradient(int[] colors) {
        if(mSweepGradient!=null)mSweepGradient = null;
        mSweepGradient = new SweepGradient(0,0,colors,new float[]{0.0f,0.25f,1.0f});
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

    public void setBitmap(int drawable){
        mAvatar = BitmapFactory.decodeResource(getResources(),drawable);
        createBitmapShader(mAvatar);
        invalidate();
    }

    public void setBitmap(Bitmap bitmap){
        mAvatar = bitmap;
        createBitmapShader(mAvatar);
        invalidate();
    }

    private Bitmap drawableToBitamp(Drawable drawable)
    {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public void setDrawable(Drawable drawable){
        mAvatar = drawableToBitamp(drawable);
        createBitmapShader(mAvatar);
        invalidate();
    }

    public void setOnline(boolean online){
        this.isOnLine = online;
        if(isOnLine)createSweetGradient(new int[]{Color.WHITE,Color.GREEN,Color.WHITE});
        else createSweetGradient(new int[]{Color.WHITE,Color.RED,Color.WHITE});
        invalidate();
    }

    public boolean isOnLine() {
        return isOnLine;
    }
}
