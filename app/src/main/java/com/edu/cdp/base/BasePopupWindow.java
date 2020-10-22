package com.edu.cdp.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public abstract class BasePopupWindow {
    protected Context mContext;
    private PopupWindow popupWindow;
    private popupWindowListener popupWindowListener;


    public abstract View setContentView();
    public abstract void initView(View view);
    public abstract boolean setCanceledOnTouchOutside();
    public abstract int setAnimationStyle();

    public BasePopupWindow(Context context) {
        this.mContext = context;
        initPopupWindow();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPopupWindow() {
        View v = setContentView();
        initView(v);
        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(setAnimationStyle());
        popupWindow.getContentView().setFocusable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((view, motionEvent) -> false);
        popupWindow.getContentView().setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                popupWindow.dismiss();
                return true;
            }
            return false;
        });

        popupWindow.setOutsideTouchable(setCanceledOnTouchOutside());
    }

    public void setPopupWindowListener(final popupWindowListener popupWindowListener) {
        this.popupWindowListener = popupWindowListener;
        if(popupWindow!=null){
            popupWindow.setOnDismissListener(popupWindowListener::onDismiss);
        }else {
            throw new NullPointerException("PopUpWindow还未创建");
        }
    }


    public void showPopUpWindow(View anchorView,int offsetX,int offsetY) {
        if(popupWindow!=null){
            if(!popupWindow.isShowing()){
                popupWindow.showAsDropDown(anchorView,offsetX,offsetY);
                if(popupWindowListener!=null){
                    popupWindowListener.onShow();
                }
            }
        }else {
            throw new NullPointerException("PopUpWindow还未创建");
        }
    }

    public void showPopUpWindow(View anchorView) {
        if(popupWindow!=null){
            if(!popupWindow.isShowing()){
                popupWindow.showAsDropDown(anchorView);
                if(popupWindowListener!=null){
                    popupWindowListener.onShow();
                }
            }
        }else {
            throw new NullPointerException("PopUpWindow还未创建");
        }
    }

    public void showPopUpWindow(View anchorView, int gravity, int offsetX, int offsetY) {
        if(popupWindow!=null){
            if(!popupWindow.isShowing()){
                popupWindow.showAtLocation(anchorView,gravity,offsetX,offsetY);
                if(popupWindowListener!=null){
                    popupWindowListener.onShow();
                }
            }
        }else {
            throw new NullPointerException("PopUpWindow还未创建");
        }
    }


    public void dismissPopUpWindow(){
        if(popupWindow!=null){
            if(popupWindow.isShowing())popupWindow.dismiss();
        }else {
            throw new NullPointerException("PopUpWindow还未创建");
        }
    }


    public interface popupWindowListener{
        void onShow();
        void onDismiss();
    }
}
