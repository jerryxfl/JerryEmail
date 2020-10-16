package com.edu.cdp.ui.dialog;

import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseDialog;

public class LoadingDialog extends BaseDialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setCustomContentView() {
        return R.layout.loading_dialog;
    }

    @Override
    protected boolean setCanceledOnTouchOutside() {
        return false;
    }

    @Override
    protected boolean setCancelable() {
        return true;
    }

    @Override
    protected void initWindow(Window window) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {

    }
}
