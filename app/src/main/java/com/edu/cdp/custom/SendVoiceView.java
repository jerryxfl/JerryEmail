package com.edu.cdp.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.edu.cdp.base.BaseActivity;


/**
 * 发送语音
 */
//@SuppressWarnings("all")
public class SendVoiceView extends View {
    //基本
    private Context context;
    private BaseActivity baseActivity;//通过baseActivity请求权限
    private int w;


    //画话筒需要的基础，画笔，画笔颜色，话筒路径
    private Paint micPaint;
    private Path micPath;
    private int micColor = Color.GRAY;


    public void attachBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public SendVoiceView(Context context) {
        this(context, null);
    }

    public SendVoiceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SendVoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    //初始化控件
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        //获取自定义属性  注意获取后要回收
        if (attrs != null) {

        }
        //初始化话筒画笔
        micPaint = new Paint();
        micPaint.setAntiAlias(true);
        micPaint.setDither(true);
        micPaint.setStyle(Paint.Style.FILL);
        micPaint.setColor(micColor);

        //初始化话筒路径
        micPath = new Path();

    }

    /**
     * 控件大小发生变化
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println("onSizeChanged");

        //获得宽高
        this.w = w;

        //初始化话筒路径
        micPath.reset();
        micPath.addRoundRect(
                new RectF(w*3/10,w/5,w*7/10,w*9/10),
                100,
                100,
                Path.Direction.CW);

        Path path = new Path();
        path.moveTo(w/10,w/5);
        path.lineTo(w/10,w*9/10);
        micPath.addPath(path);
    }


    /*
     *测量控件宽高，设置控件的高等于控件的宽
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }


    /**
     * 绘制控件
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        micPaint.setStrokeWidth(w/5);
        canvas.drawPath(micPath, micPaint);
    }
}
