package com.edu.cdp.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseFragment<DATABIND extends ViewDataBinding> extends Fragment {
    protected abstract  int setContentView();
    protected abstract  int setData(DATABIND binding);
    protected abstract void initView(DATABIND binding);
    protected abstract void setListeners(DATABIND binding);

    protected DATABIND binding;
    protected FragmentActivity activity;
    protected Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1、对布局需要绑定的内容进行加载
        binding = DataBindingUtil.inflate(inflater, setContentView(), container, false);
        // 2、获取到视图
        // 3、绑定数据
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();

        setData(binding);
        binding.setLifecycleOwner(this);

        initView(binding);
        setListeners(binding);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
