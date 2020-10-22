package com.edu.cdp.ui.popupwindow;

import android.content.Context;
import android.view.View;

import com.edu.cdp.base.BasePopupWindow;

public class UserInformationPop extends BasePopupWindow {
    public UserInformationPop(Context context) {
        super(context);
    }

    @Override
    public View setContentView() {
        return null;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public boolean setCanceledOnTouchOutside() {
        return false;
    }

    @Override
    public int setAnimationStyle() {
        return 0;
    }
}
