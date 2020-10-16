package com.edu.cdp.bean;

public class EmailBox {
    private int icon;
    private int backgroundColor;
    private int msg_num;
    private String name;
    private ItemClickListener itemClickListener;

    public EmailBox() {
    }


    public EmailBox(int icon, int backgroundColor, int msg_num, String name, ItemClickListener itemClickListener) {
        this.icon = icon;
        this.backgroundColor = backgroundColor;
        this.msg_num = msg_num;
        this.name = name;
        this.itemClickListener = itemClickListener;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getMsg_num() {
        return msg_num;
    }

    public void setMsg_num(int msg_num) {
        this.msg_num = msg_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick();
    }
}
