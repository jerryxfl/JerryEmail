package com.edu.cdp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.bean.Account;
import com.edu.cdp.databinding.ActivityUInfoBinding;
import com.edu.cdp.response.User;

/**
 * 展示用户信息
 * 必须传入用户
 */

public class UInfoActivity extends BaseActivity<ActivityUInfoBinding> {

    private User user;

    @Override
    protected int setContentView() {
        return R.layout.activity_u_info;
    }

    @Override
    protected void setData(ActivityUInfoBinding binding) {
        //获得意图
        Intent intent = getIntent();
        //从意图中获取参数
        user = (User) intent.getSerializableExtra("user");

        //传入用户为空，返回上一个界面
        if(user==null)finish();
    }

    @Override
    protected void initViews(ActivityUInfoBinding binding) {

    }

    @Override
    protected void setListeners(ActivityUInfoBinding binding) {

    }
}