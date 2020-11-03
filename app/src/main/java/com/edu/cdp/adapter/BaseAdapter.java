package com.edu.cdp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private List<T> mData = new ArrayList<T>();
    private int[] mLayouts;
    private Context mContext;
    private setItems<T> setItems;


    public BaseAdapter(Context context, int[] layouts) {
        this.mContext = context;
        this.mLayouts = layouts;
    }

    public void setData(List<T> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setHeader(List<T> data){
        Collections.reverse(data);
        for (int i = 0; i < data.size(); i++) {
            this.mData.add(0,data.get(i));
            notifyItemInserted(0);
        }
    }

    public void setHeader(T data){
        this.mData.add(0,data);
        notifyItemInserted(0);
    }

    public void setFooter(List<T> data){
        for (int i = 0; i < data.size(); i++) {
            this.mData.add(mData.size(),data.get(i));
            notifyItemInserted(mData.size());
        }
    }

    public void setFooter(T data){
        this.mData.add(mData.size(),data);
        notifyItemInserted(mData.size());
    }

    public List<T> getData(){
        return mData;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayouts[viewType], parent, false);
        return new BaseViewHolder(view, mContext);
    }

    //全部数据装配
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (setItems != null) setItems.initItem(holder, position, mData);
    }


    //局部数据刷新
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
            return;
        }
        if (setItems != null) {
            for (Object payload : payloads) {
                setItems.updateItem(holder, position, mData, (String) payload);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.isEmpty()) {
            return 0;
        } else if (setItems != null) {
            return setItems.getItemViewType(position,mData);
        }else return 0;
    }

    protected interface setItems<T> {
        void initItem(BaseViewHolder holder, int position, List<T> data);

        void updateItem(BaseViewHolder holder, int position, List<T> data, String tag);

        int getItemViewType(int position,List<T> data);
    }

    protected void setItems(setItems<T> setItems) {
        this.setItems = setItems;
    }

}
