package com.edu.cdp.bean;

public class Setting {
    private String name;
    private Click click;

    public Setting(String name, Click click) {
        this.name = name;
        this.click = click;
    }

    public Setting() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Click getClick() {
        return click;
    }

    public void setClick(Click click) {
        this.click = click;
    }

    public interface Click {
        void click();
    }
}
