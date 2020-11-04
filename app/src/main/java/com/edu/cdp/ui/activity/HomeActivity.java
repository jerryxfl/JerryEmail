package com.edu.cdp.ui.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.edu.cdp.bean.Contact;
import com.edu.cdp.custom.AvatarView;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.custom.FurtherAvatar;
import com.edu.cdp.database.bean.Email;
import com.edu.cdp.database.bean.LocalUser;
import com.edu.cdp.database.bean.Recent;
import com.edu.cdp.database.dao.EmailDao;
import com.edu.cdp.database.dao.UserDao;
import com.edu.cdp.databinding.ActivityHomeBinding;
import com.edu.cdp.eventbus.event.LoginEvent;
import com.edu.cdp.flutter.channel.FlutterEventChannel;
import com.edu.cdp.model.manager.ModelManager;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.net.websocket.CommandType;
import com.edu.cdp.net.websocket.JWebSocketListener2;
import com.edu.cdp.net.websocket.WebSocketManager;
import com.edu.cdp.net.websocket.bean.ServerRequest;
import com.edu.cdp.request.Login;
import com.edu.cdp.response.User;
import com.edu.cdp.ui.dialog.WEmailDialog;
import com.edu.cdp.ui.popupwindow.PopMenu;
import com.edu.cdp.ui.popupwindow.EmailPopMenu;
import com.edu.cdp.utils.AndroidUtils;
import com.edu.cdp.utils.GsonUtil;
import com.edu.cdp.utils.VibrationUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterActivityLaunchConfigs;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    private UserDao userDao;
    private EmailDao emailDao;

    @Override
    protected int setContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void setData(ActivityHomeBinding binding) {

    }

    @Override
    protected void initViews(ActivityHomeBinding binding) {
        RelativeLayout topPanel = binding.topPanel;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topPanel.getLayoutParams();
        params.topMargin = AndroidUtils.getStatusBarHeight(this);
        topPanel.setLayoutParams(params);

        SignEventBus();

        userDao = JApplication.getInstance().getDb().userDao();
        emailDao = JApplication.getInstance().getDb().EmailDao();

        //初始化联系人列表
        initContactAdapter();
        //初始化最近记录
        initRecentlyAdapter();
        //初始化我的收件箱
        initMyInboxAdapter();

        Auto(true);
    }

    @Override
    protected void setListeners(final ActivityHomeBinding binding) {
        binding.avatar.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ManagerAccountActivity.class)));

        ModelManager.getManager().getMainAccountModel().getUser().observe(this, account -> {
            Glide.with(HomeActivity.this)
                    .load(account.getLocalUser().getAvatar())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            binding.avatar.setDrawable(resource);
                        }
                    });

            binding.avatar.setOnline(account.isOnline());
        });

        binding.menu.setOnLongClickListener(v -> {
            EmailPopMenu wEmailPopMenu = new EmailPopMenu(HomeActivity.this);
            wEmailPopMenu.showPopUpWindow(binding.menu, -10, 10);
            return true;
        });
    }


    @Override
    protected void onDestroy() {
        WebSocketClient client = WebSocketManager.getInstance().getClient();
        if (client != null) {
            if (client.isOpen()) client.close();
        }
        super.onDestroy();
    }

    //联系人
    private void initContactAdapter() {
        binding.contactRecyclerview.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.contactRecyclerview.setLayoutManager(layoutManager);
        final JAdapter<Contact> contactJAdapter = new JAdapter<>(
                this,
                binding.contactRecyclerview,
                new int[]{R.layout.contact_head_layout, R.layout.contact_layout},
                new JAdapter.DataListener<Contact>() {
                    @Override
                    public void initItem(BaseViewHolder holder, int position, List<Contact> data) {
                        final Contact contact = data.get(position);
                        if (contact.getLocalUser() == null) {
                            ImageView avatarView = holder.findViewById(R.id.avatar);
                            TextView contactName = holder.findViewById(R.id.name);

                            avatarView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.add));
                            contactName.setText("Add");
                            avatarView.setOnClickListener(v -> {
                                //添加新联系人

                            });
                        } else {
                            final AvatarView avatarView = holder.findViewById(R.id.avatar);
                            TextView contactName = holder.findViewById(R.id.name);

                            avatarView.setOnline(contact.isOnline());
                            Glide.with(HomeActivity.this)
                                    .load(contact.getLocalUser().getAvatar())
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            avatarView.setDrawable(resource);
                                        }
                                    });

                            contactName.setText(contact.getLocalUser().getNickname());
                            avatarView.setOnClickListener(v -> {
                                //发送邮件
                                WEmailDialog wEmailDialog = new WEmailDialog(HomeActivity.this, ModelManager.getManager().getMainAccountModel().getUser().getValue());
                                wEmailDialog.showDialog();
                                wEmailDialog.addReceivers(contact);


                            });
                            avatarView.setOnLongClickListener(view -> {
                                PopMenu popMenu = new PopMenu(HomeActivity.this,
                                        ModelManager.getManager().getMainAccountModel().getUser().getValue(),
                                        contact,
                                        avatarView);
                                popMenu.showPopUpWindow(avatarView, -60, 0);
                                return true;
                            });
                        }
                    }

                    @Override
                    public void updateItem(BaseViewHolder holder, int position, List<Contact> data, String tag) {
                        Contact contact = data.get(position);
                        final AvatarView avatarView = holder.findViewById(R.id.avatar);
                        TextView contactName = holder.findViewById(R.id.name);

                        if ("onlineChange".equals(tag)) {
                            avatarView.setOnline(contact.isOnline());
                            Glide.with(HomeActivity.this)
                                    .load(contact.getLocalUser().getAvatar())
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            avatarView.setDrawable(resource);
                                        }
                                    });
                            contactName.setText(contact.getLocalUser().getNickname());
                        }
                    }

                    @Override
                    public int getItemViewType(int position, List<Contact> data) {
                        if (data.get(position).getLocalUser() == null) return 0;
                        else return 1;
                    }
                });

        ModelManager.getManager().getContactModel().getContacts().observe(this, contacts -> contactJAdapter.adapter.setData(contacts));
    }

    //最近
    private void initRecentlyAdapter() {
        binding.recentRecyclerview.setNestedScrollingEnabled(false);
        binding.recentRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recentRecyclerview.setLayoutManager(layoutManager);
        JAdapter<com.edu.cdp.database.bean.Email> emailAdapter = new JAdapter<>(this,
                binding.recentRecyclerview, new int[]{R.layout.single_mail_layout, R.layout.mail_layout}, new JAdapter.DataListener<com.edu.cdp.database.bean.Email>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void initItem(BaseViewHolder holder, int position, List<com.edu.cdp.database.bean.Email> data) {
                com.edu.cdp.database.bean.Email email = data.get(position);
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
                        Glide.with(HomeActivity.this)
                                .load(user.getAvatar())
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        furtherAvatar.setDrawable(resource);
                                    }
                                });
                    }

                    User user = GsonUtil.parserJsonToArrayBean(email.getSenduserinfo(), User.class);
                    Glide.with(HomeActivity.this)
                            .load(user.getAvatar())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    circleOnlineAvatar.setDrawable(resource);
                                }
                            });

                    title.setText(email.getTitle());
                    time.setText(CalculateTimeDifference(email.getTime()+""));
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

                    Glide.with(HomeActivity.this)
                            .load(user.getAvatar())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    avatar.setDrawable(resource);
                                }
                            });
                    title.setText(email.getTitle());
                    time.setText(CalculateTimeDifference(email.getTime()+""));
                    JSONObject json = JSONObject.parseObject(email.getContent());
                    String text = !json.containsKey("text")||json.getString("text").equals("")?"进入查看":json.getString("text");
                    content.setText(text);
                }
                LinearLayout container = holder.findViewById(R.id.container);
                container.setOnClickListener(view -> {
                    Intent intent = new Intent(HomeActivity.this,EmailActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("email",email);
                    intent.putExtras(args);
                    startActivity(intent);
                });

            }

            @Override
            public void updateItem(BaseViewHolder holder, int position, List<com.edu.cdp.database.bean.Email> data, String tag) {

            }

            @Override
            public int getItemViewType(int position, List<com.edu.cdp.database.bean.Email> data) {
                if (data.get(position).getTag() == 4) return 1;
                else return 0;
            }
        });
        ModelManager.getManager().getRecentModel().getRecent().observe(this, emails -> {
            emailAdapter.adapter.setData(ModelManager.getManager().getRecentModel().getRecent().getValue());
        });
    }

    //我的收件箱
    private void initMyInboxAdapter() {
        binding.myInboxRecyclerview.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.myInboxRecyclerview.setLayoutManager(layoutManager);
        final JAdapter<Account> accountJAdapter = new JAdapter<>(
                this, binding.myInboxRecyclerview, new int[]{R.layout.add_account_layout, R.layout.inbox_layout},
                new JAdapter.DataListener<Account>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void initItem(BaseViewHolder holder, int position, List<Account> data) {
                        final Account account = data.get(position);
                        if (account.getLocalUser() != null) {
                            final CircleOnlineAvatar avatar = holder.findViewById(R.id.avatar);
                            RelativeLayout container = holder.findViewById(R.id.container);
                            TextView name = holder.findViewById(R.id.name);
                            TextView mailNum = holder.findViewById(R.id.mail_num);

                            Glide.with(HomeActivity.this)
                                    .load(account.getLocalUser().getAvatar())
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            avatar.setDrawable(resource);
                                        }
                                    });
                            name.setText(account.getLocalUser().getNickname() + "的收件箱");
                            if (account.getLocalUser().getUUID() == null) {
                                //账号未登陆状态
                                mailNum.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.unlogin_sl));
                                mailNum.setTextColor(Color.WHITE);
                                mailNum.setText("请重新登录");
                                container.setOnClickListener(view -> {
//                                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable("login", account.getLocalUser());
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);
//
//                                    startActivity(FlutterActivity.withCachedEngine("JENGINE")
//                                            .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
//                                            .build(HomeActivity.this));
////
//
//                                    FlutterEventChannel flutterEventChannel =
//                                            new FlutterEventChannel(FlutterEngineCache.getInstance().get("JENGINE").getDartExecutor().getBinaryMessenger(), "CHANGE_ROUTE");
//                                    flutterEventChannel.sendEvent("logout");

                                    Intent intent = new Intent(HomeActivity.this,SingleLoginActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("LOGIN", account.getLocalUser());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                });
                            } else {
                                //账号登陆状态
                                //设置消息数量
                                mailNum.setBackground(null);

                                int num = emailDao.loadAllInbox(account.getLocalUser().getId()).size();
                                if (account.getEmailNum() > num) {
                                    mailNum.setTextColor(Color.parseColor("#3498db"));
                                } else {
                                    mailNum.setTextColor(Color.GRAY);
                                }

                                mailNum.setText(account.getEmailNum() + "");
                                container.setOnClickListener(view -> {
                                    Intent intent = new Intent(HomeActivity.this, EmailManagerActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("account", account);
                                    intent.putExtras(bundle);
                                    HomeActivity.this.startActivity(intent);
                                });
                            }
                        } else {
                            RelativeLayout container = holder.findViewById(R.id.container);
                            container.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, LoginWayActivity.class)));
                        }
                    }

                    @Override
                    public void updateItem(BaseViewHolder holder, int position, List<Account> data, String tag) {

                    }

                    @Override
                    public int getItemViewType(int position, List<Account> data) {
                        if (data.get(position).getLocalUser() == null) {
                            return 0;
                        } else return 1;
                    }
                }
        );

        ModelManager.getManager().getAccountModel().getAccounts().observe(this, accounts -> {
            System.out.println("我的邮箱有数据变化");
            accountJAdapter.adapter.setData(accounts);
        });
    }


    private String CalculateTimeDifference(String timeStr) {
        StringBuffer sb = new StringBuffer();
        long t = Long.parseLong(timeStr);
        long time = System.currentTimeMillis() - (t * 1000);
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0) {
            sb.append(day + "天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚")) {
            sb.append("前");
        }
        return sb.toString();
    }


    private void Auto(boolean autoLogin) {
        List<Account> accounts = ModelManager.getManager().getAccountModel().getAccounts().getValue();
        assert accounts != null;
        if (accounts.isEmpty()) return;
        for (Account account : accounts) {
            if (account.getLocalUser() != null && account.getLocalUser().getUUID() != null) {
                //加载本地数量
                account.setEmailNum(mmkv.decodeInt(account.getLocalUser().getId() + "inbox"));
                ModelManager.getManager().getAccountModel().updateMegNum(account);

                if (autoLogin) AutoLogin(account);
                else {
                    if (account.getLocalUser().isMainAccount()) GetContacts(account.getLocalUser());
                    getMessageNums(account);
                }
            }
        }
        ConnectServer();
    }


    private synchronized void AutoLogin(final Account account) {
        Map<String, String> headers = null;
        if (account.getLocalUser().getUUID() != null) {
            headers = new HashMap<>();
            headers.put("uuid", account.getLocalUser().getUUID());
        }
        OkHttpUtils.POST(Constants.LOGIN_URL, headers, new Login(account.getLocalUser().getUsername(), account.getLocalUser().getPassword()), new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {

            }


            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    String data = response.getString("data");
                    JSONObject json = JSONObject.parseObject(data);
                    LocalUser user = new LocalUser(
                            account.getLocalUser().getId(),
                            json.getString("username"),
                            json.getString("password"),
                            json.getString("nickname"),
                            json.getString("avatar"),
                            json.getString("uuid"),
                            account.getLocalUser().isMainAccount()
                    );
                    userDao.updateUsers(user);
                    return true;
                }

                System.out.println("登录失败信息：" + response.getString("msg") + account.getLocalUser().getUUID());
                return false;
            }

            @Override
            public void onSuccess() {
                if (account.getLocalUser().isMainAccount()) GetContacts(account.getLocalUser());
                getMessageNums(account);
            }
        });
    }

    //获得联系人
    private synchronized void GetContacts(LocalUser localUser) {
        Map<String, String> headers = new HashMap<>();
        if (localUser.getUUID() != null) headers.put("uuid", localUser.getUUID());

        OkHttpUtils.GET(Constants.CONTACTS, headers, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {
            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    List<User> data = GsonUtil.jsonToList(response.getString("data"), User.class);
                    final List<Contact> contacts = new ArrayList<Contact>();
                    for (User user : data) {
                        System.out.println("*********************************************************************" + user.toString());
                        contacts.add(new Contact(user, false));
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        ModelManager.getManager().getContactModel().addContacts(contacts);
                        SubscribeUserStatus(contacts);
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

    //自动连接服务器
    private void ConnectServer() {
        List<LocalUser> allLocalUser = userDao.getAllUser();
        if (allLocalUser.isEmpty()) return;

        StringBuilder uuid = null;
        for (LocalUser localUser : allLocalUser) {
            if (localUser.getUUID() == null) continue;
            if (uuid == null || uuid.toString().equals("")) {
                uuid = new StringBuilder(localUser.getUUID());
            } else {
                uuid.append(",").append(localUser.getUUID());
            }
        }
        System.out.println("开始自动登录 uuid:" + uuid);

        if (uuid != null)
            WebSocketManager.getInstance().JavaWebSocketClient(Constants.WS_URL(uuid.toString()), new JWebSocketListener2() {
                @Override
                public void onOpen(ServerHandshake handshakedata, WebSocket webSocket) {
                    Log.d("JERRY", "onOpen");
                    ModelManager.getManager().getMainAccountModel().setUserOnline(true);
                    //发送订阅请求
                    //接受 {"command":"MESSAGE","send":"3","target":"1","content":"你好啊"}
                    //返回 {"commend":"SUCCESS","content":"","target":3}

                    //拼接订阅用户id
                }

                @Override
                public void onMessage(String message) {
                    Log.d("JERRY", message);
                    JSONObject json = JSONObject.parseObject(message);
                    String commend = json.getString("commend");
                    String content = json.getString("content");
                    int target = json.getInteger("target");
                    dealMessage(commend, target, content);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("JERRY", "onClose");
                    ModelManager.getManager().getMainAccountModel().setUserOnline(false);
                    ModelManager.getManager().getContactModel().setOnline(false);
                }

                @Override
                public void onError(Exception ex) {
                    Log.d("JERRY", "onError");
                    ex.printStackTrace();
                }
            });
    }


    private void SubscribeUserStatus(List<Contact> contacts) {
        String str = "";
        for (Contact c : contacts) {

            if (c.getLocalUser() != null) {
                if (str.equals("")) str = c.getLocalUser().getId() + "";
                else str = str + "," + c.getLocalUser().getId();
            }
        }

        System.out.println("订阅用户的id:  " + str);

        ServerRequest serverRequest = new ServerRequest(CommandType.SUBSCRIBE,
                Objects.requireNonNull(ModelManager.getManager().getMainAccountModel().getUser().getValue()).getLocalUser().getId() + "",
                str,
                "");
        WebSocketClient client = WebSocketManager.getInstance().getClient();

        if (client != null) {
            if (client.isOpen()) {
                client.send(JSONObject.toJSONString(serverRequest));
            }
        }


    }

    //获得邮箱邮件数量
    private synchronized void getMessageNums(final Account account) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uuid", account.getLocalUser().getUUID());

        OkHttpUtils.GET(Constants.INBOX_MESSAGE_COUNT, headers, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {
            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 400) {
                    int num = response.getInteger("data");
                    account.setEmailNum(num);
                    mmkv.encode(account.getLocalUser().getId() + "inbox", num);
                    return true;
                }
                return false;
            }

            @Override
            public void onSuccess() {
                ModelManager.getManager().getAccountModel().updateMegNum(account);
            }
        });
    }

    //处理websocket命令
    private synchronized void dealMessage(String commend, final int target, final String content) {
        System.out.println("command:" + commend + " target:" + target + " content:" + content);

        switch (commend) {
            case CommandType.DISINVAD:
                //uuid失效
                List<LocalUser> allUser = userDao.getAllUser();
                for (LocalUser u : allUser) {
                    System.out.println("*-*-*-*-*-*-*:   u:" + u.getId() + "," + u.getUUID() + "    ,uuid:" + content);
                    if (u.getUUID() == null) continue;
                    if (u.getUUID().equals(content)) {
                        System.out.println(u.getNickname() + "  , ID:  " + u.getId() + ",uuid失效");
                        u.setUUID(null);
                        userDao.updateUsers(u);
                        new Handler(Looper.getMainLooper()).post(() -> ModelManager.getManager().refreshAccountModel());
                    }
                }
                break;
            case CommandType.MESSAGE:
                //收到消息
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(HomeActivity.this, "目标：" + target + ",内容：" + content, Toast.LENGTH_SHORT).show());
                break;
            case CommandType.CONNECT:
//                用户上线
                ModelManager.getManager().getContactModel().setOnline(target, true);
                break;
            case CommandType.DISCONNECT:
//                用户下线
                ModelManager.getManager().getContactModel().setOnline(target, false);
                break;
            case CommandType.VIBRATION:
                VibrationUtils.Vibrator(this, new long[]{100, 200, 100, 200}, -1);
                break;


        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginEvent(LoginEvent loginEvent) {
        Auto(false);
    }

}