package com.edu.cdp.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JAdapter<T> implements BaseAdapter.setItems<T>{
    private DataListener<T> dataListener;
    public BaseAdapter<T> adapter;

    public JAdapter( Context context,RecyclerView recyclerView,int[] layout,DataListener<T> dataListener) {
        this.dataListener = dataListener;
        this.adapter = new BaseAdapter<T>(context,layout);
        recyclerView.setAdapter(adapter);
        adapter.setItems(this);
    }

    @Override
    public void initItem(BaseViewHolder holder, int position, List<T> data) {
        if(dataListener!=null)dataListener.initItem(holder,position,data);
    }

    @Override
    public void updateItem(BaseViewHolder holder, int position, List<T> data, String tag) {
        if(dataListener!=null)dataListener.updateItem(holder,position,data,tag);
    }

    @Override
    public int getItemViewType(int position,List<T> data) {
        if(dataListener!=null)return dataListener.getItemViewType(position,data);
        else return 0;
    }

    public interface DataListener<T> {
        void initItem(BaseViewHolder holder, int position, List<T> data);

        void updateItem(BaseViewHolder holder, int position, List<T> data, String tag);

        int getItemViewType(int position,List<T> data);
    }


}
