package com.edu.cdp.custom;

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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.edu.cdp.R;

public class CircleOnlineAvatar extends View {
    private Bitmap mAvatar;
    private BitmapShader mAvatarShader;
    private int minRadius = 5;
    private Paint mPaint;

    private boolean showOnlineState = true;
    private boolean isOnline = true;


    public CircleOnlineAvatar(Context context) {
        this(context,null);
    }

    public CircleOnlineAvatar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleOnlineAvatar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int src = R.drawable.avatar;
        if(attrs!=null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleOnlineAvatar);
            src = a.getResourceId(R.styleable.CircleOnlineAvatar_c_avatar,R.drawable.avatar);
            minRadius = a.getInteger(R.styleable.CircleOnlineAvatar_c_minRadius,5);
            showOnlineState = a.getBoolean(R.styleable.CircleOnlineAvatar_showOnlineState,true);
            a.recycle();
        }
        if(src!=0){
            mAvatar = BitmapFactory.decodeResource(getResources(),src);
            createBitmapShader(mAvatar);
        }
        createPaint();
    }

    private void createPaint() {
        Paint p = new Paint();
        p.setDither(true);
        p.setAntiAlias(true);
        mPaint = p;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        //at_most wrap_content
        //exactly match_parent
        //unexpect 未指定

        if(widthMode==MeasureSpec.AT_MOST){
            width = Math.min(width, dip2px(getContext(),minRadius));
        }else if(widthMode==MeasureSpec.UNSPECIFIED){
            width = dip2px(getContext(),minRadius);
        }


        if(heightMode==MeasureSpec.AT_MOST){
            height = Math.min(height, dip2px(getContext(),minRadius));
        }else if(heightMode==MeasureSpec.EXACTLY){
            height = Math.min(height,width);
        }else if(heightMode==MeasureSpec.UNSPECIFIED){
            height = dip2px(getContext(),minRadius);
        }
        setMeasuredDimension(width,height);




    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth()+getPaddingStart()+getPaddingEnd();
        int height = getHeight()+getPaddingTop()+getPaddingBottom();


        int avatarCenterX = width/2;
        int avatarCenterY = height/2;

        int radius = width/2;
        createBitmapShader(mAvatar);
        mPaint.setShader(mAvatarShader);
        canvas.drawCircle(avatarCenterX,avatarCenterY,radius,mPaint);

        if(showOnlineState){
            int pointRadius = radius/6;

            double x = Math.sin(Math.toRadians(45))*radius;
            float pointCenterX = (float) (width/2+x);
            float pointCenterY = (float) (height/2+x);

            mPaint.setShader(null);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(pointCenterX,pointCenterY,(pointRadius*5)/3,mPaint);

            if(isOnline)mPaint.setColor(Color.parseColor("#5364EB"));
            else mPaint.setColor(Color.parseColor("#bdc3c7"));
            canvas.drawCircle(pointCenterX,pointCenterY,pointRadius,mPaint);
        }

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


    public void setBitmap(int bitmap){
        this.mAvatar = BitmapFactory.decodeResource(getResources(),bitmap);
        createBitmapShader(mAvatar);
        invalidate();
    }

    public void setBitmap(Bitmap bitmap){
        this.mAvatar = bitmap;
        createBitmapShader(mAvatar);
        invalidate();
    }
    private Bitmap drawableToBitamp(Drawable drawable)
    {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public void setDrawable(Drawable drawable){
        this.mAvatar = drawableToBitamp(drawable);
        createBitmapShader(mAvatar);
        invalidate();
    }


    public void setShowOnlineState(boolean showOnline){
        this.showOnlineState = showOnline;
        invalidate();
    }

    public void setIsOnline(boolean isOnline){
        this.isOnline = isOnline;
        invalidate();
    }

    public boolean isOnline() {
        return isOnline;
    }
}
