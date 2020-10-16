package com.edu.cdp.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.edu.cdp.R;

public abstract class BaseDialog extends Dialog {
    protected abstract int setCustomContentView();
    protected abstract boolean setCanceledOnTouchOutside();
    protected abstract boolean setCancelable();
    protected abstract void initWindow(Window window);
    protected abstract void initView();
    protected abstract void initEvent();
    private Listener listener;
    protected Context context;
    protected Activity activity;
    protected Window window;



    public BaseDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setCustomContentView());
        //按空白处不能取消动画
        setCanceledOnTouchOutside(setCanceledOnTouchOutside());
        setCancelable(setCancelable());
        window = getWindow();
        initWindow(window);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if(listener!=null)listener.showListener(dialogInterface);
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(listener!=null)listener.dismissListener(dialogInterface);
            }
        });
    }

    public interface Listener {
        void showListener(DialogInterface dialogInterface);
        void dismissListener(DialogInterface dialogInterface);
    }

    public void setDialogListener(Listener listener){
        this.listener = listener;
    }

    public void showDialog() {
        if(!isShowing())show();
    }

    public void dismissDialog() {
        if(isShowing())dismiss();
    }
}
