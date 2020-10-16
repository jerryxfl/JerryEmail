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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.edu.cdp.R;

import java.util.ArrayList;
import java.util.List;

public class FurtherAvatar extends View {
    private int maxLength = 3;
    private List<Bitmap> mAvatars;
    private Paint mPaint;
    private float radius = 20f;
    private AvatarListener avatarListener;

    public FurtherAvatar(Context context) {
        this(context, null);
    }

    public FurtherAvatar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FurtherAvatar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mAvatars = new ArrayList<>();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FurtherAvatar);
            maxLength = a.getInteger(R.styleable.FurtherAvatar_length, 3);
            radius = a.getDimension(R.styleable.FurtherAvatar_radius, 20.0f);
            a.recycle();
        }
        createPaint();
    }

    private void createPaint() {
        Paint p = new Paint();
        p.setDither(true);
        p.setAntiAlias(true);
        mPaint = p;
    }

    private BitmapShader createBitmapShader(Bitmap bitmap) {
        if (bitmap == null) return null;
        int minBitMap = Math.min(bitmap.getHeight(), bitmap.getHeight());
        //取view宽高中的小值 尽量保证图片内容的显示
        int minValue = Math.min(getWidth(), getHeight());
        //设置半径
        //计算缩放比例  一定要*1.0f 因为int之间的计算结果会四舍五入0或1 效果就不美丽了
        float scale = minValue * 1.0f / minBitMap;
        //设置缩放比例
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        /**
         * 创建着色器 设置着色模式
         * TileMode的取值有三种：
         *  CLAMP 拉伸  REPEAT 重复   MIRROR 镜像
         */
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //设置矩阵
        bitmapShader.setLocalMatrix(matrix);
        return bitmapShader;
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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width;
        int height;


        //wrap_content  == at_most
        //match_content,10dp  == exactly
        //unspecified  == unspecified

        width = (int) ((radius*2-radius/3)*4+radius/3);
        height = (int) (radius*2);
        setMeasuredDimension(width, height);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int centerY = height / 2;

//        计算每个头像的X中心坐标
        if (mAvatars.size() <= maxLength) {
            //小于
            for (int i = 0; i < mAvatars.size(); i++) {
                int centerX = (int) (radius+((5*radius)/3)*i);
                mPaint.setShader(null);
                mPaint.setColor(Color.WHITE);
                canvas.drawCircle(centerX, centerY,radius+radius/6,mPaint);

                BitmapShader bitmapShader = createBitmapShader(mAvatars.get(i));
                mPaint.setShader(bitmapShader);
                canvas.drawCircle(centerX,centerY,radius,mPaint);
            }
        } else {
            //大于
            for (int i = 0; i < maxLength-1; i++) {
                int centerX = (int) (radius+((5*radius)/3)*i);
                mPaint.setShader(null);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.WHITE);
                canvas.drawCircle(centerX, centerY,radius+radius/6,mPaint);

                BitmapShader bitmapShader = createBitmapShader(mAvatars.get(i));
                mPaint.setShader(bitmapShader);
                canvas.drawCircle(centerX,centerY,radius,mPaint);
            }

            //最后一个圈
            int centerX = (int) (radius+((5*radius)/3)*3);
            mPaint.setShader(null);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(radius/6);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
            mPaint.setColor(Color.parseColor("#CCD1D7"));
            canvas.drawArc(new RectF(
                centerX-radius+radius/12,
                    radius/12,centerX+radius-radius/12,
                    height-radius/12
            ),0,360,false,mPaint);

            int textSize = (int) (radius/1.3);
            mPaint.setTextSize(textSize);
            mPaint.setStrokeWidth(1);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setColor(Color.parseColor("#CCD1D7"));
            String text = "+"+ (mAvatars.size() - maxLength);
//            float textWidth = mPaint.measureText(text);

            canvas.drawText(text,centerX,centerY+textSize/2,mPaint);

            //第三个圈
            centerX = (int) (radius+((5*radius)/3)*2);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShader(null);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(centerX, centerY,radius+radius/6,mPaint);

            BitmapShader bitmapShader = createBitmapShader(mAvatars.get(2));
            mPaint.setShader(bitmapShader);
            canvas.drawCircle(centerX,centerY,radius,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("further 被点击");
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                System.out.println("点击位置 X:"+x+"  Y:"+y);
                float baseX = radius+(2*radius)/3;

                if(avatarListener!=null){
                    if(x<=baseX && mAvatars.size()>=1){
                        avatarListener.AvatarOnclick(0);
                    }else if(x>baseX && x<=baseX*2 && mAvatars.size()>=2){
                        avatarListener.AvatarOnclick(1);
                    }else if(x>baseX*2 && x<=baseX*3  && mAvatars.size()>=3){
                        avatarListener.AvatarOnclick(2);
                    }else if(mAvatars.size()>=4){
                        avatarListener.MoreOnclick();
                    }
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    public interface AvatarListener {
        void AvatarOnclick(int position);
        void MoreOnclick();
    }

    public void setOnAvatarClickListener(AvatarListener listener) {
        this.avatarListener = listener;
    }

    public void setBitmap(int bitmap){
        mAvatars.add(BitmapFactory.decodeResource(getResources(),bitmap));
    }

    public void setBitmap(int[] bitmap){
        for(int i = 0; i < bitmap.length; i++){
            mAvatars.add(BitmapFactory.decodeResource(getResources(),bitmap[i]));
        }
    }

    public void setBitmap(List<Integer> bitmap){
        for(int i = 0; i < bitmap.size(); i++){
            mAvatars.add(BitmapFactory.decodeResource(getResources(),bitmap.get(i)));
        }
    }

    public void setDrawable(Drawable drawable){
        mAvatars.add(drawableToBitamp(drawable));
    }

    public void setDrawable(Drawable[] drawable){
        for(int i = 0; i < drawable.length; i++){
            mAvatars.add(drawableToBitamp(drawable[i]));
        }
    }

    public void setDrawable(List<Drawable> drawable){
        for(int i = 0; i < drawable.size(); i++){
            mAvatars.add(drawableToBitamp(drawable.get(i)));
        }
    }


    private Bitmap drawableToBitamp(Drawable drawable)
    {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

}
