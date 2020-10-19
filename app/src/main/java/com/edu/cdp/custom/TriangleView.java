package com.edu.cdp.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.edu.cdp.R;

//三角形控件
public class TriangleView extends View {
    private Paint mPaint;
    private Path path;
    private int color=Color.parseColor("#ffffff");
    public TriangleView(Context context) {
        this(context,null);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs!=null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TriangleView);
            color = a.getColor(R.styleable.TriangleView_color,Color.parseColor("#ffffff"));
            a.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        path.moveTo(w/2,0);
        path.lineTo(w,h);
        path.lineTo(0,h);
        path.lineTo(w/2,0);
        path.close();

        canvas.drawPath(path,mPaint);

    }
}
