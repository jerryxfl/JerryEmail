package com.edu.cdp.bean;

import android.view.View;

public class Setting2 {
    private String name;
    private ClickListener clickListener;
    private init init;

    private int type;

    public Setting2(int type) {
        this.type = type;
    }

    public Setting2(String name, int type,ClickListener clickListener) {
        this.name = name;
        this.type = type;
        this.clickListener = clickListener;
    }

    public Setting2(String name, int type,ClickListener clickListener,init init) {
        this.name = name;
        this.type = type;
        this.clickListener = clickListener;
        this.init = init;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public Setting2.init getInit() {
        return init;
    }

    public void setInit(Setting2.init init) {
        this.init = init;
    }

    public interface ClickListener {

        void onCLick();
    }

    public interface init{
        void initialize(View view);
    }
}
