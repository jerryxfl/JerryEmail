package com.edu.cdp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.bean.LoginWay;
import com.edu.cdp.databinding.ActivityLoginwayBinding;

import java.util.ArrayList;
import java.util.List;

public class LoginWayActivity extends BaseActivity<ActivityLoginwayBinding> {
    private List<LoginWay> loginWays;

    @Override
    protected int setContentView() {
        return R.layout.activity_loginway;
    }

    @Override
    protected void setData(ActivityLoginwayBinding binding) {
        loginWays = new ArrayList<>();
        loginWays.add(new LoginWay(R.drawable.email, getString(R.string.app_name), Color.WHITE, Color.BLACK, () -> {
            startActivity(new Intent(LoginWayActivity.this,LoginActivity.class));
            finish();
        }));
        loginWays.add(new LoginWay(R.drawable.facebook, "Facebook", Color.BLUE, Color.WHITE, new LoginWay.ILoginWay() {
            @Override
            public void click() {
                Toast.makeText(LoginWayActivity.this,"Facebook",Toast.LENGTH_SHORT).show();
            }
        }));

    }

    @Override
    protected void initViews(ActivityLoginwayBinding binding) {
        binding.loginwayRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.loginwayRecyclerView.setLayoutManager(layoutManager);
        JAdapter<LoginWay> loginWayJAdapter = new JAdapter<>(this, binding.loginwayRecyclerView, new int[]{R.layout.loginway_layout}, new JAdapter.DataListener<LoginWay>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void initItem(BaseViewHolder holder, int position, List<LoginWay> data) {
                final LoginWay loginWay = data.get(position);
                ImageView img = holder.findViewById(R.id.img);
                TextView name = holder.findViewById(R.id.name);
                RelativeLayout container = holder.findViewById(R.id.container);

                container.setBackgroundColor(loginWay.getBackgroundColor());
                img.setImageBitmap(BitmapFactory.decodeResource(getResources(),loginWay.getImg()));
                name.setTextColor(loginWay.getForegroundColor());
                name.setText("Login with "+loginWay.getName());
                container.setOnClickListener(view -> loginWay.getiLoginWay().click());
            }

            @Override
            public void updateItem(BaseViewHolder holder, int position, List<LoginWay> data, String tag) {

            }

            @Override
            public int getItemViewType(int position, List<LoginWay> data) {
                return 0;
            }
        });

        loginWayJAdapter.adapter.setData(loginWays);
    }

    @Override
    protected void setListeners(ActivityLoginwayBinding binding) {

    }
}