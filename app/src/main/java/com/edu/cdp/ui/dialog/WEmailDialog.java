package com.edu.cdp.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BaseDialog;
import com.edu.cdp.base.BasePopupWindow;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Constants;
import com.edu.cdp.bean.Contact;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.custom.SearchAnimationButton;
import com.edu.cdp.model.manager.ModelManager;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.request.SEmail;
import com.edu.cdp.response.User;
import com.edu.cdp.ui.popupwindow.SearchUserPop;
import com.edu.cdp.ui.popupwindow.UInfoPop;
import com.edu.cdp.utils.AdapterList;
import com.edu.cdp.utils.GsonUtil;
import com.edu.cdp.utils.SoftKeyBoardListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WEmailDialog extends BaseDialog {
    private Account account;
    private CircleOnlineAvatar avatar;
    private RecyclerView recyclerView;
    private CardView controlBar;
    private EditText title;
    private EditText content;
    private ImageButton html;
    private ImageButton accessory;
    private ImageButton voice;
    private ImageButton add;
    private AdapterList<Contact> contacts;
    private boolean AddOnClick = false;
    private boolean AddOpen = false;
    private SEmail sEmail;
    private LoadingDialog loadingDialog;

    public WEmailDialog(@NonNull Context context, Account account) {
        super(context);
        this.account = account;
    }

    @Override
    protected int setCustomContentView() {
        return R.layout.write_email_dialog;
    }

    @Override
    protected boolean setCanceledOnTouchOutside() {
        return false;
    }

    @Override
    protected boolean setCancelable() {
        return false;
    }

    @Override
    protected void initWindow(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.b_dialog_animate);
    }

    @Override
    protected void initView() {
        sEmail = new SEmail();
        sEmail.setSenduserid(account.getLocalUser().getId());
        sEmail.setTitle("");
        sEmail.setContent("{\"text\":\"\"}");
        sEmail.setAccessory("");
        sEmail.setReceiveuserid("");


        recyclerView = findViewById(R.id.receiveRecycler);
        controlBar = findViewById(R.id.controlBar);
        avatar = findViewById(R.id.avatar);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        html = findViewById(R.id.html);
        accessory = findViewById(R.id.accessory);
        voice = findViewById(R.id.voice);
        add = findViewById(R.id.add);

        Glide.with(context)
                .load(account.getLocalUser().getAvatar())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        avatar.setDrawable(resource);
                    }
                });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        JAdapter<Contact> contactJAdapter = new JAdapter<>(context, recyclerView, new int[]{R.layout.write_email_contact_item_layout, R.layout.contact_add_item_layout}, new JAdapter.DataListener<Contact>() {
            @Override
            public void initItem(BaseViewHolder holder, int position, List<Contact> data) {
                Contact contact = data.get(position);
                CircleOnlineAvatar avatar = holder.findViewById(R.id.avatar);
                if (contact.getLocalUser() != null) {
                    Glide.with(context)
                            .load(contact.getLocalUser().getAvatar())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    avatar.setDrawable(resource);
                                }
                            });
                    avatar.setOnLongClickListener(v -> {
                        //显示用户信息
                        UInfoPop uInfoPop = new UInfoPop(context);
                        uInfoPop.setView(contact.getLocalUser());
                        uInfoPop.showPopUpWindow(avatar, 0, -dip2px(100));
                        return true;
                    });
                } else {
                    EditText username = holder.findViewById(R.id.username_edt);
                    SearchAnimationButton search = holder.findViewById(R.id.search);
                    LinearLayout container = holder.findViewById(R.id.container);

                    avatar.setOnClickListener(v -> {
                        //添加新的接收方
                        if (!AddOnClick) {
                            if (AddOpen) {
                                AddOpen = false;

                                ValueAnimator valueAnimator = ValueAnimator.ofInt(dip2px(50), 0);
                                valueAnimator.addUpdateListener(animation -> {
                                    int progress = (int) animation.getAnimatedValue();

                                    LinearLayout.LayoutParams EdtParams = (LinearLayout.LayoutParams) username.getLayoutParams();
                                    EdtParams.width = progress;
                                    username.setLayoutParams(EdtParams);

                                    if (progress < dip2px(30)) {
                                        LinearLayout.LayoutParams SearchParams = (LinearLayout.LayoutParams) search.getLayoutParams();
                                        SearchParams.width = progress;
                                        search.setLayoutParams(SearchParams);
                                    }
                                });

                                valueAnimator.setDuration(200);
                                valueAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        AddOnClick = false;
                                        container.setBackground(null);
                                        search.reset();
                                        username.setText("");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        AddOnClick = true;
                                    }
                                });
                                valueAnimator.start();

                            } else {
                                AddOpen = true;

                                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, dip2px(50));
                                valueAnimator.addUpdateListener(animation -> {
                                    int progress = (int) animation.getAnimatedValue();

                                    LinearLayout.LayoutParams EdtParams = (LinearLayout.LayoutParams) username.getLayoutParams();
                                    EdtParams.width = progress;
                                    username.setLayoutParams(EdtParams);
                                    if (progress < dip2px(30)) {
                                        LinearLayout.LayoutParams SearchParams = (LinearLayout.LayoutParams) search.getLayoutParams();
                                        SearchParams.width = progress;
                                        search.setLayoutParams(SearchParams);
                                    }
                                });

                                valueAnimator.setDuration(200);
                                valueAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        AddOnClick = false;
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        AddOnClick = true;
                                        container.setBackground(ContextCompat.getDrawable(context, R.drawable.contact_add_item_outline));
                                    }
                                });
                                valueAnimator.start();


                            }
                        }
                    });


                    search.setOnClickListener(v -> {
                        String uNameText = username.getText().toString().trim();
                        if (uNameText.equals("")) {
                            Toast.makeText(context, "空值", Toast.LENGTH_SHORT).show();
                        } else {
                            search.startSearch();
                            SearchUserPop searchUserPop = new SearchUserPop(context);
                            searchUserPop.showPopUpWindow(avatar, -dip2px(5), -dip2px(70));
                            searchUserPop.searchContactInfo(uNameText);
                            searchUserPop.setContactOnSelectionListener(new SearchUserPop.ContactOnSelectionListener() {
                                @Override
                                public void onSelect(Contact contact) {
                                    searchUserPop.dismissPopUpWindow();
                                    boolean add = true;
                                    for (int i = 0; i < contacts.size(); i++) {
                                        if (contacts.get(i).getLocalUser() != null) {
                                            if (contacts.get(i).getLocalUser().getUsername().equals(contact.getLocalUser().getUsername()))
                                                add = false;
                                        }
                                    }
                                    if (add) addReceivers(contact);
                                    else
                                        Toast.makeText(context, "用户已添加", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSearchComplete() {
                                    search.setLoadingComplete();
                                }
                            });
                            searchUserPop.setPopupWindowListener(new BasePopupWindow.popupWindowListener() {
                                @Override
                                public void onShow() {

                                }

                                @Override
                                public void onDismiss() {
                                    avatar.callOnClick();
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void updateItem(BaseViewHolder holder, int position, List<Contact> data, String tag) {

            }

            @Override
            public int getItemViewType(int position, List<Contact> data) {
                if (data.get(position).getLocalUser() != null) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        contacts = new AdapterList<>();
        contacts.relevantAdapter(contactJAdapter.adapter);
        contacts.add(new Contact(null, false));
    }


    public void addReceivers(Contact contact) {
        contacts.add(contacts.size() - 1, contact);
        if (sEmail.getReceiveuserid() == null || sEmail.getReceiveuserid().equals("")) {
            sEmail.setReceiveuserid(contact.getLocalUser().getId() + "");
        } else {
            sEmail.setReceiveuserid(sEmail.getReceiveuserid() + "," + contact.getLocalUser().getId());
        }
    }

    @Override
    protected void initEvent() {
        avatar.setOnLongClickListener(v -> {
            User user = new User(
                    account.getLocalUser().getId(),
                    account.getLocalUser().getUsername(),
                    account.getLocalUser().getPassword(),
                    account.getLocalUser().getNickname(),
                    account.getLocalUser().getAvatar()
            );

            UInfoPop uInfoPop = new UInfoPop(context);
            uInfoPop.setView(user);
            uInfoPop.showPopUpWindow(avatar, 0, -dip2px(100));
            return true;
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sEmail.setTitle(title.getText().toString().trim());
            }
        });
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                JSONObject json = JSONObject.parseObject(sEmail.getContent());
                String text = content.getText().toString().trim();
                json.put("text", text);
                sEmail.setContent(json.toJSONString());
            }
        });


        html.setOnClickListener(v -> {
        });
        accessory.setOnClickListener(v -> {
        });
        voice.setOnClickListener(v -> {
        });
        add.setOnClickListener(v -> {
        });
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && sEmail.getContent() != null || sEmail.getTitle() != null) {
            ConfirmDialog confirmDialog = new ConfirmDialog(context);
            confirmDialog.showDialog();
            confirmDialog.setTitle("提示");
            confirmDialog.setContent("是否要保存草稿");
            confirmDialog.setCancelable(true);
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.setCancelClickListener(confirmDialog12 -> {
                confirmDialog12.dismissDialog();
                dismissDialog();
            });
            confirmDialog.setConfirmClickListener(confirmDialog1 -> {
                confirmDialog.dismissDialog();
                saveDraft();
            });
        } else {
            dismissDialog();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void saveDraft() {
//
//        {
//            "accessory": "string",
//                "content": "string",
//                "id": 0,
//                "receiveuserid": "string",
//                "senduserid": 0,
//                "title": "string"
//        }
        if(loadingDialog==null)loadingDialog = new LoadingDialog(context);
        loadingDialog.showDialog();
        //处理内容格式
        dealContentFormat();
    }

    private void dealContentFormat() {
        JSONObject json = JSONObject.parseObject(sEmail.getContent());
        String voicePath = json.getString("voice");
        if (voicePath != null && !voicePath.equals("")) {
            Map<String, String> headers = new HashMap<>();
            headers.put("uuid", account.getLocalUser().getUUID());


            handler.sendEmptyMessage(0);
        }
    }

    //上传草稿
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("uuid", account.getLocalUser().getUUID());
            OkHttpUtils.POST(Constants.SAVE_DRAFT, headers, sEmail, new OkHttpUtils.Jcallback() {
                @Override
                public void onFailure() {
                    loadingDialog.dismissDialog();
                }

                @Override
                public boolean onResponseAsync(JSONObject response) {
                    int code = response.getInteger("code");
                    if (code == 400) {
                        System.out.println(response.getString("data"));
                        return true;
                    }
                    return false;
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                    dismissDialog();
                }
            });
        }
    };
}
