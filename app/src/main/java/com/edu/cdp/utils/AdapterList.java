package com.edu.cdp.utils;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.edu.cdp.adapter.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public  class AdapterList<T> extends ArrayList<T> {
    private BaseAdapter<T> adapter;

    //关联适配器
    public void relevantAdapter(BaseAdapter<T> adapter){
        this.adapter = adapter;
        adapter.setData(this);
    }


    @Override
    public T set(int index, T element) {
        adapter.notifyItemChanged(index);
        return super.set(index, element);
    }

    @Override
    public boolean add(T t) {
        adapter.notifyItemChanged(0);
        return super.add(t);
    }

    @Override
    public void add(int index, T element) {
        adapter.notifyItemChanged(index);
        super.add(index, element);
    }

    @Override
    public T remove(int index) {
        adapter.notifyItemRemoved(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        adapter.notifyItemRemoved(indexOf(o));
        return super.remove(o);
    }

    @Override
    public void clear() {
        adapter.notifyDataSetChanged();
        super.clear();
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        adapter.notifyDataSetChanged();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        adapter.notifyDataSetChanged();
        return super.addAll(index, c);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        adapter.notifyItemRangeRemoved(fromIndex,toIndex);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        adapter.notifyItemRangeRemoved(0,size()-1);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        adapter.notifyDataSetChanged();
        return super.retainAll(c);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean removeIf(@NonNull Predicate<? super T> filter) {
        adapter.notifyDataSetChanged();
        return super.removeIf(filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void replaceAll(@NonNull UnaryOperator<T> operator) {
        adapter.notifyDataSetChanged();
        super.replaceAll(operator);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sort(@Nullable Comparator<? super T> c) {
        adapter.notifyDataSetChanged();
        super.sort(c);
    }
}
