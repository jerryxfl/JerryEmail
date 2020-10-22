package com.edu.cdp.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.application.JApplication;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Setting;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.database.dao.UserDao;
import com.edu.cdp.databinding.ActivityManagerAccountBinding;
import com.edu.cdp.model.manager.ModelManager;
import com.edu.cdp.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

public class ManagerAccountActivity extends BaseActivity<ActivityManagerAccountBinding> {
    private UserDao userDao;
    private List<Setting> settings;


    @Override
    protected int setContentView() {
        return R.layout.activity_manager_account;
    }

    @Override
    protected void setData(ActivityManagerAccountBinding binding) {
        settings = new ArrayList<>();
        settings.add(new Setting("通用", () -> {
            startActivity(new Intent(ManagerAccountActivity.this,GeneralSettingsActivity.class));
        }));
    }

    @Override
    protected void initViews(ActivityManagerAccountBinding binding) {
        View topBar = binding.topbar;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topBar.getLayoutParams();
        params.topMargin = AndroidUtils.getStatusBarHeight(this);
        topBar.setLayoutParams(params);

        //获得数据库操作对象
        userDao = JApplication.getInstance().getDb().userDao();

        initAccountRecyclerView();

        initSettingRecyclerView();
    }

    private void initAccountRecyclerView() {
        binding.accountRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.accountRecyclerView.setLayoutManager(layoutManager);

        final JAdapter<Account> accountJAdapter = new JAdapter<>(this, binding.accountRecyclerView, new int[]{R.layout.add_account_layout,R.layout.account_layout}, new JAdapter.DataListener<Account>() {
            @Override
            public void initItem(BaseViewHolder holder, int position, List<Account> data) {
                Account account = data.get(position);
                if(account.getLocalUser()==null){
                    RelativeLayout container = holder.findViewById(R.id.container);
                    container.setOnClickListener(view -> {
                        Toast.makeText(ManagerAccountActivity.this,"添加新用户",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ManagerAccountActivity.this, LoginWayActivity.class));
                    });

                }else{
                    final CircleOnlineAvatar avatar = holder.findViewById(R.id.avatar);
                    TextView nickname = holder.findViewById(R.id.nickname);
                    TextView username = holder.findViewById(R.id.username);
                    TextView mainaccount = holder.findViewById(R.id.mainaccount);

                    Glide.with(ManagerAccountActivity.this)
                            .load(account.getLocalUser().getAvatar())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    avatar.setDrawable(resource);
                                }
                            });
                    nickname.setText(account.getLocalUser().getNickname());
                    username.setText(account.getLocalUser().getUsername());
                    if(position==0)mainaccount.setText("主账户");
                }
            }

            @Override
            public void updateItem(BaseViewHolder holder, int position, List<Account> data, String tag) {

            }

            @Override
            public int getItemViewType(int position, List<Account> data) {
                if(data.get(position).getLocalUser()==null){
                    return 0;
                }else return 1;
            }
        });

        ModelManager.getManager().getAccountModel().getAccounts().observe(this, accounts -> {
            System.out.println("manager 账户变化");
            accountJAdapter.adapter.setData(accounts);
        });
    }

    private void initSettingRecyclerView() {
        binding.settingRecyclerView.setNestedScrollingEnabled(false);
        binding.settingRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.settingRecyclerView.setLayoutManager(layoutManager);

        JAdapter<Setting> settingJAdapter = new JAdapter<>(this, binding.settingRecyclerView, new int[]{R.layout.setting_layout}, new JAdapter.DataListener<Setting>() {
            @Override
            public void initItem(BaseViewHolder holder, int position, List<Setting> data) {
                final Setting setting = data.get(position);

                RelativeLayout container = holder.findViewById(R.id.container);
                TextView name = holder.findViewById(R.id.name);
                name.setText(setting.getName());
                container.setOnClickListener(view -> setting.getClick().click());
            }

            @Override
            public void updateItem(BaseViewHolder holder, int position, List<Setting> data, String tag) {

            }

            @Override
            public int getItemViewType(int position, List<Setting> data) {
                return 0;
            }
        });
        settingJAdapter.adapter.setData(settings);

    }
    @Override
    protected void setListeners(ActivityManagerAccountBinding binding) {
        binding.back.setOnClickListener(v->{
            finish();
        });
    }
}