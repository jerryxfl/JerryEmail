package com.edu.cdp.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class
KeyboardUtils {

    public static void hideKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, EditText view){
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if(!isOpen){
            imm.showSoftInput(view,0);
        }
    }

}
