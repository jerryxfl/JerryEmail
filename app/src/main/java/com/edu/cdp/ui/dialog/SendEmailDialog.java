package com.edu.cdp.ui.dialog;

import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.edu.cdp.base.BaseDialog;

public class SendEmailDialog extends BaseDialog {
    public SendEmailDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setCustomContentView() {

        return 0;
    }

    @Override
    protected boolean setCanceledOnTouchOutside() {
        return false;
    }

    @Override
    protected boolean setCancelable() {
        return false;
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
