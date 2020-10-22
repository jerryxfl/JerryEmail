package com.edu.cdp.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseDialog;

public class PermissionDialog extends BaseDialog {

    private Button confirm;

    private Listener listener;

    public PermissionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setCustomContentView() {
        return R.layout.permission_dialog;
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

    }

    @Override
    protected void initView() {
        confirm = findViewById(R.id.confirm);

    }

    @Override
    protected void initEvent() {
        confirm.setOnClickListener(v -> {
            if(listener!=null)listener.onConfirm();
        });
    }

    public void  setListener(Listener listener){
        this.listener =listener;
    }


    public interface Listener {
        void onConfirm();
    }
}
