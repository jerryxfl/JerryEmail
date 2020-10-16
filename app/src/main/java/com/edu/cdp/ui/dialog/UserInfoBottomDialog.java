package com.edu.cdp.ui.dialog;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.base.BaseDialog;
import com.edu.cdp.bean.Contact;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.utils.AndroidUtils;

public class UserInfoBottomDialog extends BaseDialog {
    private Contact contact;
    private CircleOnlineAvatar avatar;
    private RelativeLayout contentPanel;
    private View outside;
    private RelativeLayout line;
    private float startY = 0;
    private float moveY = 0;
    private int ContentPanelHeight;
    private ValueAnimator valueAnimator;
    private int DialogHeight;


    public UserInfoBottomDialog(@NonNull Context context, Contact contact) {
        super(context);
        this.contact = contact;
    }

    @Override
    protected int setCustomContentView() {
        return R.layout.user_info_bottom_dialog;
    }

    @Override
    protected boolean setCanceledOnTouchOutside() {
        return true;
    }

    @Override
    protected boolean setCancelable() {
        return true;
    }

    @Override
    protected void initWindow(Window window) {
        //设置动画
        window.setWindowAnimations(R.style.b_dialog_animate);
        //设置显示位置
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        DialogHeight = height - dip2px(context, 40);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        lp.width = width; // 宽度
        lp.height = height;
        window.setAttributes(lp);

    }

    @Override
    protected void initView() {
        contentPanel = findViewById(R.id.contentPanel);
        avatar = findViewById(R.id.avatar);
        outside = findViewById(R.id.outside);
        line = findViewById(R.id.line);

        Glide.with(context)
                .load(contact.getLocalUser().getAvatar())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        avatar.setDrawable(resource);
                    }
                });


        ViewTreeObserver vto = contentPanel.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentPanel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ContentPanelHeight = contentPanel.getHeight();
                System.out.println("内容面板高度：" + ContentPanelHeight);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initEvent() {
        outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

        line.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = motionEvent.getY();
                        System.out.println("action_down , startY:" + startY);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        moveY = motionEvent.getY() - startY;
                        int height = (int) (ContentPanelHeight - moveY);
                        if (height < DialogHeight) updateHeight(height);
                        System.out.println("action_move , moveY:" + moveY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (ContentPanelHeight < 0) dismissDialog();
                        else ScaleAnimation(ContentPanelHeight);
                        System.out.println("action_up");
                        return true;
                }

                return false;
            }
        });
    }

    private void ScaleAnimation(int nowHeight) {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(nowHeight, DialogHeight);
            valueAnimator.setDuration(200);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    updateHeight((int) value);
                }
            });
        } else {
            valueAnimator.pause();
            valueAnimator = ValueAnimator.ofFloat(nowHeight, DialogHeight);
            valueAnimator.setDuration(2000);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    updateHeight((int) value);
                }
            });
        }
        valueAnimator.start();
    }

    private void updateHeight(int height) {
        ContentPanelHeight = height;
        System.out.println("当前BottomDialog高度：" + ContentPanelHeight);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentPanel.getLayoutParams();
        params.height = height;
        contentPanel.setLayoutParams(params);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
