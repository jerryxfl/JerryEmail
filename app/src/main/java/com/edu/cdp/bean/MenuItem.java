package com.edu.cdp.bean;

public class MenuItem {
    private int icon;
    private String name;
    private MenuOnClickListener menuOnClickListener;

    public MenuItem() {
    }

    public MenuItem(int icon, String name, MenuOnClickListener menuOnClickListener) {
        this.icon = icon;
        this.name = name;
        this.menuOnClickListener = menuOnClickListener;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuOnClickListener getMenuOnClickListener() {
        return menuOnClickListener;
    }

    public void setMenuOnClickListener(MenuOnClickListener menuOnClickListener) {
        this.menuOnClickListener = menuOnClickListener;
    }

    public interface MenuOnClickListener {
        void onClick();
    }
}
