package com.edu.cdp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.databinding.ActivityGeneralSettingsBinding;
import com.edu.cdp.utils.AndroidUtils;

public class GeneralSettingsActivity extends BaseActivity<ActivityGeneralSettingsBinding> {

    @Override
    protected int setContentView() {
        return R.layout.activity_general_settings;
    }

    @Override
    protected void setData(ActivityGeneralSettingsBinding binding) {

    }

    @Override
    protected void initViews(ActivityGeneralSettingsBinding binding) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.topPanel.getLayoutParams();
        params.topMargin = AndroidUtils.getStatusBarHeight(this);
        binding.topPanel.setLayoutParams(params);
    }

    @Override
    protected void setListeners(ActivityGeneralSettingsBinding binding) {
        binding.back.setOnClickListener(v -> {
            finish();
        });

    }
}