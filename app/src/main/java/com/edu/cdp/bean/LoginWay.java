package com.edu.cdp.bean;

public class LoginWay {
    private int img;
    private String name;
    private int backgroundColor;
    private int foregroundColor;
    private ILoginWay iLoginWay;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public ILoginWay getiLoginWay() {
        return iLoginWay;
    }

    public void setiLoginWay(ILoginWay iLoginWay) {
        this.iLoginWay = iLoginWay;
    }

    public LoginWay(int img, String name, int backgroundColor, int foregroundColor, ILoginWay iLoginWay) {
        this.img = img;
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.iLoginWay = iLoginWay;
    }

    public LoginWay() {
    }

    public interface ILoginWay {
        void click();
    }
}
