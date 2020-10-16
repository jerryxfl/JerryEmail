package com.edu.cdp.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


@SuppressWarnings("all")
public class BaseViewHolder extends RecyclerView.ViewHolder {
    private Context mContext;
    private SparseArray<View> views;

    public BaseViewHolder(@NonNull View itemView,Context context) {
        super(itemView);
        this.mContext = context;
        views = new SparseArray<View>();
    }

    public BaseViewHolder getViewHolder(View itemView,Context context){
        return new BaseViewHolder(itemView,context);
    }

    public <T extends View> T findViewById(int layoutId) {
        View view= views.get(layoutId);
        if(view==null){
            view = itemView.findViewById(layoutId);
            views.put(layoutId, view);
        }
        return (T)view;
    }





}
