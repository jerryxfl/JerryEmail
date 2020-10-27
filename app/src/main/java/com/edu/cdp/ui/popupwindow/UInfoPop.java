package com.edu.cdp.ui.popupwindow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.base.BasePopupWindow;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.response.User;

public class UInfoPop extends BasePopupWindow {
    private TextView nickName;
    private TextView username;
    private CircleOnlineAvatar avatar;

    public UInfoPop(Context context) {
        super(context);
    }

    @Override
    public View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.u_info_pop_layout, null, false);
    }

    @Override
    public void initView(View view) {
        nickName = view.findViewById(R.id.nickname);
        username = view.findViewById(R.id.username);
        avatar = view.findViewById(R.id.avatar);

    }

    public void setView(User user) {
        nickName.setText(user.getNickname());
        username.setText(user.getUsername());
        Glide.with(mContext)
                .load(user.getAvatar())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        avatar.setDrawable(resource);
                    }
                });
    }

    @Override
    public boolean setCanceledOnTouchOutside() {
        return true;
    }

    @Override
    public int setAnimationStyle() {
        return R.style.anim_menu_bottombar;
    }
}
