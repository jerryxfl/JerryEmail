package com.edu.cdp.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.base.BaseDialog;
import com.edu.cdp.base.BasePopupWindow;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Constants;
import com.edu.cdp.bean.Contact;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.custom.SearchAnimationButton;
import com.edu.cdp.custom.SendVoiceView;
import com.edu.cdp.custom.VoiceView;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.net.okhttp.UploadRequestBody;
import com.edu.cdp.request.SEmail;
import com.edu.cdp.response.User;
import com.edu.cdp.ui.activity.EmailActivity;
import com.edu.cdp.ui.popupwindow.SearchUserPop;
import com.edu.cdp.ui.popupwindow.UInfoPop;
import com.edu.cdp.utils.AdapterList;
import com.edu.cdp.utils.AudioPlayUtils;
import com.edu.cdp.utils.KeyboardUtils;
import com.edu.cdp.utils.SoftKeyBoardListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private LinearLayout bottomBar;
    private AdapterList<Contact> contacts;
    private boolean AddOnClick = false;
    private boolean AddOpen = false;
    private SEmail sEmail;
    private LoadingDialog loadingDialog;
    private ValueAnimator bottomBarValueAnimator;

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
        bottomBar = findViewById(R.id.bottomBar);

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
                        //添加新的邮件接收方
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
                                        username.clearFocus();
                                        KeyboardUtils.hideKeyboard(context, username);
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
                                        KeyboardUtils.showKeyboard(context, username);
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

                    //搜索用户
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


        //添加底部控制栏view
        //语音
        addView(R.layout.voice_d_layout, new ViewAddListener() {
            @Override
            public void InitView(View view) {
                //数据源
                AdapterList<String> filePathList = new AdapterList<>();

                SendVoiceView send_voice_view = view.findViewById(R.id.send_voice_view);
                RecyclerView filesRecycler = view.findViewById(R.id.filesRecycler);

                //设置recyclerview
                filesRecycler.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(RecyclerView.HORIZONTAL);
                filesRecycler.setLayoutManager(layoutManager);

                JAdapter<String> fileAdapter = new JAdapter<>(
                        context,
                        filesRecycler,
                        new int[]{R.layout.voice_recycler_layout},
                        new JAdapter.DataListener<String>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void initItem(BaseViewHolder holder, int position, List<String> data) {
                                String path = data.get(position);

                                RelativeLayout item = holder.findViewById(R.id.item);
                                TextView name = holder.findViewById(R.id.name);
                                ImageView delete = holder.findViewById(R.id.delete);
                                ImageView img = holder.findViewById(R.id.img);

                                if (path.equals("")) {
                                    //添加本地文件
                                    name.setText("");
                                    delete.setVisibility(View.GONE);
                                    img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.squre_add));
                                    item.setOnClickListener(v -> {
                                        //打开本地文件选择页面,添加本地文件

                                    });
                                } else {
                                    //刚录好的音频文件
                                    delete.setVisibility(View.VISIBLE);

                                    img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.mp3));
                                    String fileName = path.substring(path.lastIndexOf("/") + 1);
                                    if (fileName.length() > 8)
                                        fileName = fileName.substring(fileName.length() - 8);
                                    name.setText(fileName);
                                    item.setOnClickListener(v -> {
                                        try {
                                            AudioPlayUtils.getInstance().play(path);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    delete.setOnClickListener(v -> {
                                        //删除录音
                                        AudioPlayUtils.getInstance().stop();
                                        filePathList.remove(position);
                                        JSONObject json = JSONObject.parseObject(sEmail.getContent());

                                        Iterator<String> iterator = json.keySet().iterator();
                                        if (iterator.hasNext()) {
                                            String key = iterator.next();
                                            if (json.getString(key).equals(path)) {
                                                json.remove(key);
                                            }
                                        }
                                        sEmail.setContent(json.toJSONString());
                                    });
                                }
                            }

                            @Override
                            public void updateItem(BaseViewHolder holder, int position, List<String> data, String tag) {

                            }

                            @Override
                            public int getItemViewType(int position, List<String> data) {
                                return 0;
                            }
                        }
                );

                filePathList.relevantAdapter(fileAdapter.adapter);
                filePathList.add(0, "");
                //设置录音监听器
                send_voice_view.setListener(new SendVoiceView.Listener() {
                    @Override
                    public void recordSuccess(String path) {
                        filePathList.add(0, path);
                        JSONObject json = JSONObject.parseObject(sEmail.getContent());
                        json.put("voice" + (filePathList.size() - 1), path);
                        sEmail.setContent(json.toJSONString());
                        System.out.println("json" + json.toJSONString());
                    }

                    @Override
                    public void recordFailure() {
                    }
                });

            }

            @Override
            public void InitEvent(View view) {

            }
        });
        //html
        addView(R.layout.html_d_layout, new ViewAddListener() {
            @Override
            public void InitView(View view) {

            }

            @Override
            public void InitEvent(View view) {

            }
        });
        //附件
        addView(R.layout.accessory_d_layout, new ViewAddListener() {
            @Override
            public void InitView(View view) {

            }

            @Override
            public void InitEvent(View view) {

            }
        });
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
            KeyboardUtils.hideKeyboard(context, controlBar);
            if (!bottomBarIsOPen()) openBottomBar();
            scrollToView(1);
        });
        accessory.setOnClickListener(v -> {
            KeyboardUtils.hideKeyboard(context, controlBar);
            if (!bottomBarIsOPen()) openBottomBar();
            scrollToView(2);
        });
        voice.setOnClickListener(v -> {
            KeyboardUtils.hideKeyboard(context, controlBar);
            if (!bottomBarIsOPen()) openBottomBar();
            scrollToView(0);
        });
        add.setOnClickListener(v -> {
            KeyboardUtils.hideKeyboard(context, controlBar);
            openOrCloseBottomBar();
        });


        SoftKeyBoardListener.setListener((Activity) context, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                closeBottomBar();
            }

            @Override
            public void keyBoardHide(int height) {
                closeBottomBar();
            }
        });
    }

    //替换view时的动画
    private ValueAnimator translationAnimator;
    //每个子布局宽度
    private int width;

    /**
     * 添加view
     */
    private void addView(int layout_id, ViewAddListener viewAddListener) {
        View view = LayoutInflater.from(context).inflate(layout_id, null, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        if (width == 0) {
            //获得每个子布局的宽度
            bottomBar.post(() -> {
                width = bottomBar.getWidth();
                params.width = width;
                view.setLayoutParams(params);
                //添加试图到布局之中
                bottomBar.addView(view);
            });
        } else {
            params.width = width;
            view.setLayoutParams(params);
            //添加试图到布局之中
            bottomBar.addView(view);
        }

        //初始化view
        if (viewAddListener != null) {
            viewAddListener.InitView(view);
            viewAddListener.InitEvent(view);
        }
    }

    /**
     * 滑动到指定view
     *
     * @param position
     */
    private void scrollToView(int position) {
        //获得所有子布局个数
        int total = bottomBar.getChildCount();
        //判断total是否大于position
        if ((total - 1) < position) return;


        //真正执行动画的地方

        View view = bottomBar.getChildAt(0);
        int leftMargin = view.getLeft();//0           -1080
        int distance = width * position;  //1080        0

        if (leftMargin < distance) executeScrollToView(leftMargin, -distance);
        else executeScrollToView(leftMargin, distance);


    }

    /**
     * 执行动画
     *
     * @param start 上一个view0左边距
     * @param end   结束位置view0左边距
     */
    private void executeScrollToView(int start, int end) {
        View view = bottomBar.getChildAt(0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();

        translationAnimator = ValueAnimator.ofInt(start, end);
        translationAnimator.addUpdateListener(animation -> {
            params.leftMargin = (int) animation.getAnimatedValue();
            view.setLayoutParams(params);
        });
        translationAnimator.setDuration(200);
        translationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        if (!translationAnimator.isRunning()) translationAnimator.start();
    }


    /**
     * 打开或者关闭底部控制栏
     */
    private void openOrCloseBottomBar() {
        if (bottomBarIsOPen()) closeBottomBar();
        else openBottomBar();
    }

    /**
     * 判断底部控制栏是否已经打开
     *
     * @return
     */
    private boolean bottomBarIsOPen() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) controlBar.getLayoutParams();
        return params.bottomMargin == 0;
    }

    /**
     * 打开底部操作面板
     *
     * @return
     */
    private void openBottomBar() {
        int px = dip2px(200);
        if (!bottomBarIsOPen()) {
            if (bottomBarValueAnimator == null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) controlBar.getLayoutParams();
                bottomBarValueAnimator = ValueAnimator.ofInt(-px, 0);
                bottomBarValueAnimator.setDuration(200);
                bottomBarValueAnimator.addUpdateListener(animation -> {
                    params.bottomMargin = (int) animation.getAnimatedValue();
                    controlBar.setLayoutParams(params);
                });
            } else if (!bottomBarValueAnimator.isRunning()) {
                bottomBarValueAnimator.setIntValues(-px, 0);
            }
            bottomBarValueAnimator.start();
        }
    }

    /**
     * 关闭底部操作面板
     *
     * @return
     */
    private void closeBottomBar() {
        int px = dip2px(200);
        if (bottomBarIsOPen()) {
            if (bottomBarValueAnimator == null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) controlBar.getLayoutParams();
                bottomBarValueAnimator = ValueAnimator.ofInt(0, -px);
                bottomBarValueAnimator.setDuration(200);
                bottomBarValueAnimator.addUpdateListener(animation -> {
                    params.bottomMargin = (int) animation.getAnimatedValue();
                    controlBar.setLayoutParams(params);
                });
            } else if (!bottomBarValueAnimator.isRunning()) {
                bottomBarValueAnimator.setIntValues(0, -px);
            }
            bottomBarValueAnimator.start();
        }
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        closeBottomBar();
        JSONObject json = JSONObject.parseObject(sEmail.getContent());
        String text = json.getString("text");

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!sEmail.getTitle().equals("") || !text.equals("")) {
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
        } else {
            dismissDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveDraft() {
//        {
//            "accessory": "string",
//                "content": "string",
//                "id": 0,
//                "receiveuserid": "string",
//                "senduserid": 0,
//                "title": "string"
//        }
        if (loadingDialog == null) loadingDialog = new LoadingDialog(context);
        loadingDialog.showDialog();
        //处理内容格式
        dealContentFormat();
    }

    //保存要上传的语音地址,及是否上传成功
    private Map<String,Boolean> voiceMap = new HashMap<>();

    private void dealContentFormat() {
        JSONObject json = JSONObject.parseObject(sEmail.getContent());
        for (String key : json.keySet()) {
            if (key.startsWith("voice")) {
                voiceMap.put(key,false);
            }
        }
        if(voiceMap.isEmpty())save();
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                voiceMap.forEach((s, aBoolean) -> {
                    saveVoice(s);
                });
            }else{
                for (String key : voiceMap.keySet()) {
                    saveVoice(key);
                }
            }
        }
    }

    private void saveVoice(String voicePath) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uuid", account.getLocalUser().getUUID());
        OkHttpUtils.UPLOAD(Constants.SAVE_VOICE, voicePath, headers, "voice", new OkHttpUtils.JUploadCallback1() {
            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if(code == 400){
                    String data = response.getString("data");
                    JSONObject json = JSONObject.parseObject(sEmail.getContent());
                    Iterator<String> iterator = json.keySet().iterator();
                    if (iterator.hasNext()) {
                        String key = iterator.next();
                        if (json.getString(key).equals(voicePath)) {
                            json.put(key, data);
                        }
                    }
                    sEmail.setContent(json.toJSONString());
                    System.out.println("语音上传成功:"+JSONObject.parseObject(sEmail.getContent()).toJSONString());
                    voiceMap.put(voicePath,true);
                    return true;
                }
                return false;
            }

            @Override
            public void onFailure(String msg) {
                loadingDialog.dismissDialog();
            }

            @Override
            public void onSuccess() {
                if(!voiceMap.containsValue(false)){
                    save();
                }
            }
        }, new UploadRequestBody.JUploadCallback2() {
            @Override
            public void onUploadStart(long max, long progress) {
                System.out.println("上传开始了"+max);
            }

            @Override
            public void onUploadUpdate(long max, long progress) {
                System.out.println("上传进度"+progress);
            }

            @Override
            public void onUploadComplete(long max, long progress) {
                System.out.println("上传结束");
            }
        });
    }

    private void save() {
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


    private interface ViewAddListener {
        void InitView(View view);

        void InitEvent(View view);
    }
}
