package com.edu.cdp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Constants;
import com.edu.cdp.bean.EmailBox;
import com.edu.cdp.databinding.ActivityEmailManagerBinding;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailManagerActivity extends BaseActivity<ActivityEmailManagerBinding> {
    private Account account;
    private List<EmailBox> emailBoxes;
    private JAdapter<EmailBox> emailBoxJAdapter;

    @Override
    protected int setContentView() {
        return R.layout.activity_email_manager;
    }

    @Override
    protected void setData(ActivityEmailManagerBinding binding) {

    }

    @Override
    protected void initViews(final ActivityEmailManagerBinding binding) {
        RelativeLayout topPanel = binding.topPanel;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topPanel.getLayoutParams();
        params.topMargin = AndroidUtils.getStatusBarHeight(this);
        topPanel.setLayoutParams(params);


        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        if (account == null) finish();

        //设置数据
        Glide.with(this)
                .load(account.getLocalUser().getAvatar()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.avatar.setDrawable(resource);
            }
        });
        binding.nickname.setText(account.getLocalUser().getNickname());
        binding.username.setText(account.getLocalUser().getUsername());


        //设置recyclerview
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerview.setLayoutManager(layoutManager);
        emailBoxJAdapter = new JAdapter<>(
                this,
                binding.recyclerview,
                new int[]{R.layout.mail_box_layout},
                new JAdapter.DataListener<EmailBox>() {
                    @Override
                    public void initItem(BaseViewHolder holder, final int position, List<EmailBox> data) {
                        final EmailBox emailBox = data.get(position);

                        LinearLayout click = holder.findViewById(R.id.click);
                        CardView container = holder.findViewById(R.id.container);
                        ImageView icon = holder.findViewById(R.id.icon);
                        final TextView msgNumber = holder.findViewById(R.id.msgNum);
                        TextView title = holder.findViewById(R.id.title);

                        click.setOnClickListener(view -> {
                            Intent intent1 = new Intent(EmailManagerActivity.this,EmailListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("account",account);
                            intent1.putExtras(bundle);
                            intent1.putExtra("type",position+1);
                            intent1.putExtra("num",emailBox.getMsg_num());
                            startActivity(intent1);
                            overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);

                            if(emailBox.getItemClickListener()!=null)emailBox.getItemClickListener().onItemClick();
                        });
                        container.setCardBackgroundColor(emailBox.getBackgroundColor());
                        icon.setImageBitmap(BitmapFactory.decodeResource(getResources(), emailBox.getIcon()));
                        msgNumber.setText(emailBox.getMsg_num() + "");
                        title.setText(emailBox.getName());

                    }

                    @Override
                    public void updateItem(BaseViewHolder holder, int position, List<EmailBox> data, String tag) {
                        if (tag.equals("change")) {
                            final EmailBox emailBox = data.get(position);
                            TextView msgNumber = holder.findViewById(R.id.msgNum);
                            msgNumber.setText(emailBox.getMsg_num() + "");
                        }

                    }

                    @Override
                    public int getItemViewType(int position, List<EmailBox> data) {
                        return 0;
                    }
                }
        );

        emailBoxes = new ArrayList<>();
        emailBoxes.add(new EmailBox(R.drawable.inbox, Color.parseColor("#3E5B76"), account.getEmailNum(), "收件箱", null));

        emailBoxes.add(new EmailBox(R.drawable.outbox, Color.parseColor("#222222"), mmkv.decodeInt(account.getLocalUser().getId()+"outbox"), "发件箱", null));
        emailBoxes.add(new EmailBox(R.drawable.startbox, Color.parseColor("#5086b3"), mmkv.decodeInt(account.getLocalUser().getId()+"starbox"), "星标邮件", null));
        emailBoxes.add(new EmailBox(R.drawable.morebox, Color.parseColor("#ea7c6c"), mmkv.decodeInt(account.getLocalUser().getId()+"groupbox"), "群邮件", null));
        emailBoxes.add(new EmailBox(R.drawable.draft, Color.parseColor("#a4b0be"), mmkv.decodeInt(account.getLocalUser().getId()+"draftbox"), "草稿箱", null));
        emailBoxJAdapter.adapter.setData(emailBoxes);


        getOutBoxCount(account.getLocalUser().getUUID());
        getStarBoxCount(account.getLocalUser().getUUID());
        getGroupBoxCount(account.getLocalUser().getUUID());
        getDraftBoxCount(account.getLocalUser().getUUID());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setListeners(ActivityEmailManagerBinding binding) {
        binding.back.setOnClickListener(view -> finish());

        binding.avatar.setOnClickListener(view -> {
        });
    }


    private void getOutBoxCount(String uuid) {

        Map<String, String> headers = new HashMap<>();
        headers.put("uuid", uuid);
        OkHttpUtils.GET(Constants.OUTBOX_MESSAGE_COUNT, headers, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    final int num = response.getInteger("data");
                    mmkv.encode(account.getLocalUser().getId()+"outbox",num);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        emailBoxes.get(1).setMsg_num(num);
                        emailBoxJAdapter.adapter.notifyItemChanged(1, "change");
                    });
                    return true;
                }
                return false;
            }

            @Override
            public void onSuccess() {

            }
        });
    }


    private void getStarBoxCount(String uuid) {

        Map<String, String> headers = new HashMap<>();
        headers.put("uuid", uuid);
        OkHttpUtils.GET(Constants.STARBOX_MESSAGE_COUNT, headers, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    final int num = response.getInteger("data");
                    mmkv.encode(account.getLocalUser().getId()+"starbox",num);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        emailBoxes.get(2).setMsg_num(num);
                        emailBoxJAdapter.adapter.notifyItemChanged(2, "change");
                    });
                    return true;
                }
                return false;
            }

            @Override
            public void onSuccess() {

            }
        });
    }


    private void getGroupBoxCount(String uuid) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uuid", uuid);
        OkHttpUtils.GET(Constants.GROUPBOX_MESSAGE_COUNT, headers, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    final int num = response.getInteger("data");
                    mmkv.encode(account.getLocalUser().getId()+"groupbox",num);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        emailBoxes.get(3).setMsg_num(num);
                        emailBoxJAdapter.adapter.notifyItemChanged(3, "change");
                    });
                    return true;
                }
                return false;
            }

            @Override
            public void onSuccess() {

            }
        });
    }


    private void getDraftBoxCount(String uuid) {

        Map<String, String> headers = new HashMap<>();
        headers.put("uuid", uuid);
        OkHttpUtils.GET(Constants.DRAFTBOX_MESSAGE_COUNT, headers, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    final int num = response.getInteger("data");
                    mmkv.encode(account.getLocalUser().getId()+"draftbox",num);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        emailBoxes.get(4).setMsg_num(num);
                        emailBoxJAdapter.adapter.notifyItemChanged(4, "change");
                    });
                    return true;
                }
                return false;
            }

            @Override
            public void onSuccess() {

            }
        });
    }
}