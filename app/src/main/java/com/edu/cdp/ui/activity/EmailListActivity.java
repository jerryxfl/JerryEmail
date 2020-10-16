package com.edu.cdp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.application.JApplication;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Constants;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.custom.FurtherAvatar;
import com.edu.cdp.database.bean.Email;
import com.edu.cdp.database.dao.EmailDao;
import com.edu.cdp.databinding.ActivityEmailListBinding;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.response.User;
import com.edu.cdp.utils.AndroidUtils;
import com.edu.cdp.utils.GsonUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailListActivity extends BaseActivity<ActivityEmailListBinding> {
    private List<Email> emailList;
    private EmailDao emailDao;
    private Account account;
    private int type;
    private int num;
    private JAdapter<Email> emailJAdapter;

    @Override
    protected int setContentView() {
        return R.layout.activity_email_list;
    }

    @Override
    protected void setData(ActivityEmailListBinding binding) {

    }

    @Override
    protected void initViews(ActivityEmailListBinding binding) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.topPanel.getLayoutParams();
        params.topMargin = AndroidUtils.getStatusBarHeight(this);
        binding.topPanel.setLayoutParams(params);

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) binding.emailRecyclerview.getLayoutParams();
        params1.topMargin = AndroidUtils.getStatusBarHeight(this) + dip2px(this, 60);
        binding.emailRecyclerview.setLayoutParams(params1);

        emailDao = JApplication.getInstance().getDb().EmailDao();
        emailList = new ArrayList<>();

        binding.emailRecyclerview.setNestedScrollingEnabled(false);
        binding.emailRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.emailRecyclerview.setLayoutManager(layoutManager);
        emailJAdapter = new JAdapter<>(
                this,
                binding.emailRecyclerview,
                new int[]{R.layout.single_mail_layout, R.layout.mail_layout},
                new JAdapter.DataListener<Email>() {
                    @Override
                    public void initItem(BaseViewHolder holder, int position, List<Email> data) {
                        final Email email = data.get(position);

                        if (email.getTag() == 4) {
                            //群邮件
                            final CircleOnlineAvatar circleOnlineAvatar = holder.findViewById(R.id.avatar);
                            final FurtherAvatar furtherAvatar = holder.findViewById(R.id.FurtherAvatar);
                            TextView title = holder.findViewById(R.id.title);
                            TextView content = holder.findViewById(R.id.content);
                            TextView time = holder.findViewById(R.id.time);
                            TextView msgNumber = holder.findViewById(R.id.msg_num);

                            circleOnlineAvatar.setShowOnlineState(false);
//{\"avatar\":\"http://192.168.42.246:8080/JerryEmail/resources/1.jpg\",\"id\":1,\"nickname\":\"System\",\"password\":\"26521\",\"username\":\"1072059168@guilang.com\"}
                            List<User> users = GsonUtil.jsonToList(email.getReceiveuserinfo(), User.class);

                            for (User user :users) {
                                Glide.with(EmailListActivity.this)
                                        .load(user.getAvatar())
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                furtherAvatar.setDrawable(resource);
                                            }
                                        });
                            }

                            User user = GsonUtil.parserJsonToArrayBean(email.getSenduserinfo(), User.class);
                            Glide.with(EmailListActivity.this)
                                    .load(user.getAvatar())
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            circleOnlineAvatar.setDrawable(resource);
                                        }
                                    });

                            title.setText(email.getTitle());
                            time.setText("5分钟之前");
                            msgNumber.setText("1");

                            JSONObject json = JSONObject.parseObject(email.getContent());
                            String text = !json.containsKey("text")||json.getString("text").equals("")?"进入查看":json.getString("text");
                            content.setText(text);
                        } else {
                            User user = GsonUtil.parserJsonToArrayBean(email.getSenduserinfo(), User.class);

                            final CircleOnlineAvatar avatar = holder.findViewById(R.id.avatar);
                            TextView title = holder.findViewById(R.id.title);
                            TextView time = holder.findViewById(R.id.time);
                            TextView content = holder.findViewById(R.id.content);

                            Glide.with(EmailListActivity.this)
                                    .load(user.getAvatar())
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            avatar.setDrawable(resource);
                                        }
                                    });
                            title.setText(email.getTitle());
                            time.setText("2分钟之前");
                            JSONObject json = JSONObject.parseObject(email.getContent());
                            String text = !json.containsKey("text")||json.getString("text").equals("")?"进入查看":json.getString("text");
                            content.setText(text);
                        }
                        LinearLayout container = holder.findViewById(R.id.container);
                        container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(EmailListActivity.this,EmailActivity.class);
                                Bundle args = new Bundle();
                                args.putSerializable("email",email);
                                intent.putExtras(args);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void updateItem(BaseViewHolder holder, int position, List<Email> data, String tag) {

                    }

                    @Override
                    public int getItemViewType(int position, List<Email> data) {
                        if (data.get(position).getTag() == 4) return 1;
                        else return 0;
                    }
                }
        );


        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        type = intent.getIntExtra("type", 1);
        num = intent.getIntExtra("num", 0);
        switch (type) {
            case 1:
                binding.title.setText(account.getLocalUser().getUsername() + "的收件箱（" + num + "）");
                break;
            case 2:
                binding.title.setText(account.getLocalUser().getUsername() + "的发件箱（" + num + "）");
                break;
            case 3:
                binding.title.setText(account.getLocalUser().getUsername() + "的星标邮件（" + num + "）");
                break;
            case 4:
                binding.title.setText(account.getLocalUser().getUsername() + "的群邮件（" + num + "）");
                break;
            case 5:
                binding.title.setText(account.getLocalUser().getUsername() + "的草稿箱（" + num + "）");
                break;
        }

        loadEmailList(type);

    }

    @Override
    protected void setListeners(ActivityEmailListBinding binding) {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void loadEmailList(final int tag) {


        String url = "";

        switch (tag) {
            case 1:
                emailList.addAll(emailDao.loadAllInbox(account.getLocalUser().getId()));
                url = Constants.INBOX_MESSAGE;
                break;
            case 2:
                emailList.addAll(emailDao.loadAllOutbox(account.getLocalUser().getId()));
                url = Constants.OUTBOX_MESSAGE;
                break;
            case 3:
                emailList.addAll(emailDao.loadAllStarbox(account.getLocalUser().getId()));
                url = Constants.STARBOX_MESSAGE;
                break;
            case 4:
                emailList.addAll(emailDao.loadAllGroupbox(account.getLocalUser().getId()));
                url = Constants.GROUPBOX_MESSAGE;
                break;
            case 5:
                emailList.addAll(emailDao.loadAllDraftbox(account.getLocalUser().getId()));
                url = Constants.DRAFTBOX_MESSAGE;
                break;

        }
        emailJAdapter.adapter.setData(emailList);

        url = url + emailList.size();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("uuid", account.getLocalUser().getUUID());
        OkHttpUtils.GET(url, headers, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    System.out.println("***********-*-*-*-*:" + response.toJSONString());
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject json = data.getJSONObject(i);
//                        {"senduserid":1,"id":1,"title":"test","content":"testContent","receiveuserid":"3,2"}
                        final Email email = new Email(
                                json.getInteger("id"),
                                account.getLocalUser().getId(),
                                json.getString("senduserinfo"),
                                json.getString("receiveuserinfo"),
                                json.getString("title"),
                                json.getString("content"),
                                json.getString("accessory"),
                                json.getInteger("time"),
                                tag
                        );
                        emailDao.insertOneEmail(email);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                emailList.add(email);
                                emailJAdapter.adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onSuccess() {
            }
        });
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}