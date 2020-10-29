package com.edu.cdp.utils;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.edu.cdp.adapter.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class AdapterList<T> extends ArrayList<T> {
    private BaseAdapter<T> adapter;

    //关联适配器
    public void relevantAdapter(BaseAdapter<T> adapter) {
        this.adapter = adapter;
        adapter.setData(this);
    }


    @Override
    public T set(int index, T element) {
        super.set(index, element);
        adapter.notifyItemChanged(index);
        return element;
    }

    @Override
    public boolean add(T t) {
        super.add(t);
        adapter.notifyItemInserted(0);
        return true;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        adapter.notifyItemInserted(index);
    }

    @Override
    public T remove(int index) {
        T element = get(index);
        super.remove(index);
        adapter.notifyItemRemoved(index);
        adapter.notifyItemRangeChanged(0,size()-1);
        return element;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        int index = indexOf(o);
        super.remove(o);
        adapter.notifyItemRemoved(index);
        adapter.notifyItemRangeChanged(0,size()-1);
        return true;
    }

    @Override
    public void clear() {
        super.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        super.addAll(c);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        super.addAll(index, c);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        adapter.notifyItemRangeRemoved(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        super.removeAll(c);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        super.retainAll(c);
        adapter.notifyDataSetChanged();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean removeIf(@NonNull Predicate<? super T> filter) {
        super.removeIf(filter);
        adapter.notifyDataSetChanged();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void replaceAll(@NonNull UnaryOperator<T> operator) {
        super.replaceAll(operator);
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sort(@Nullable Comparator<? super T> c) {
        super.sort(c);
        adapter.notifyDataSetChanged();
    }
}
