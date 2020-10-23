package com.edu.cdp.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.response.User;
import com.edu.cdp.ui.popupwindow.SearchUserPop;
import com.edu.cdp.utils.AdapterList;
import com.edu.cdp.utils.SoftKeyBoardListener;

import java.util.List;

public class WEmailDialog extends BaseDialog {
    private Account account;
    private CircleOnlineAvatar avatar;
    private RecyclerView recyclerView;
    private CardView controlBar;
    private AdapterList<Contact> contacts;
    private boolean AddOnClick = false;
    private boolean AddOpen = false;

    public WEmailDialog(@NonNull Context context,Account account) {
        super(context);
        this.account = account;
    }

    @Override
    protected int setCustomContentView() {
        return R.layout.write_email_dialog;
    }

    @Override
    protected boolean setCanceledOnTouchOutside() {
        return true;
    }

    @Override
    protected boolean setCancelable() {
        return true;
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
        recyclerView = findViewById(R.id.receiveRecycler);
        controlBar = findViewById(R.id.controlBar);
        avatar = findViewById(R.id.avatar);

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

        JAdapter<Contact> contactJAdapter = new JAdapter<>(context, recyclerView, new int[]{R.layout.write_email_contact_item_layout,R.layout.contact_add_item_layout}, new JAdapter.DataListener<Contact>() {
            @Override
            public void initItem(BaseViewHolder holder, int position, List<Contact> data) {
                Contact contact = data.get(position);
                CircleOnlineAvatar avatar = holder.findViewById(R.id.avatar);
                if(contact.getLocalUser()!=null){
                    Glide.with(context)
                            .load(contact.getLocalUser().getAvatar())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    avatar.setDrawable(resource);
                                }
                            });
                    avatar.setOnClickListener(v->{
                        //显示用户信息

                    });
                }else{
                    EditText username = holder.findViewById(R.id.username_edt);
                    TextView search = holder.findViewById(R.id.search);
                    LinearLayout container = holder.findViewById(R.id.container);

                    avatar.setOnClickListener(v->{
                        //添加新的接收方
                        if(!AddOnClick){
                            if(AddOpen){
                                AddOpen =false;

                                AnimatorSet set = new AnimatorSet();
                                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                                float scaledDensity = dm.scaledDensity;

                                Paint p = new Paint();
                                p.setTextSize(12*scaledDensity);
                                Rect bounds=new Rect();
                                p.getTextBounds("搜索", 0, "搜索".length(), bounds);
                                //获取文本宽度
                                int textWidth=bounds.width();


                                ValueAnimator TextWidthValueAnimator = ValueAnimator.ofInt(textWidth*2,0);
                                TextWidthValueAnimator.addUpdateListener(animation -> {
                                    LinearLayout.LayoutParams TvParams = (LinearLayout.LayoutParams) search.getLayoutParams();
                                    TvParams.width = (int) animation.getAnimatedValue();
                                    search.setLayoutParams(TvParams);
                                });

                                ValueAnimator EditWidthValueAnimator = ValueAnimator.ofInt(dip2px(50),0);
                                EditWidthValueAnimator.addUpdateListener(animation -> {
                                    LinearLayout.LayoutParams EdtParams = (LinearLayout.LayoutParams) username.getLayoutParams();
                                    EdtParams.width = (int) animation.getAnimatedValue();
                                    username.setLayoutParams(EdtParams);
                                });




                                set.playTogether(EditWidthValueAnimator,TextWidthValueAnimator);
                                set.setDuration(200);
                                set.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        AddOnClick = false;
                                        container.setBackground(null);
                                        username.setText("");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        AddOnClick = true;
                                    }
                                });
                                set.start();

                            }else{
                                AddOpen =true;

                                //开启动画
                                AnimatorSet set = new AnimatorSet();
                                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                                float scaledDensity = dm.scaledDensity;

                                Paint p = new Paint();
                                p.setTextSize(12*scaledDensity);
                                Rect bounds=new Rect();
                                p.getTextBounds("搜索", 0, "搜索".length(), bounds);
                                //获取文本宽度
                                int textWidth=bounds.width();


                                ValueAnimator TextWidthValueAnimator = ValueAnimator.ofInt(0,textWidth*2);
                                TextWidthValueAnimator.addUpdateListener(animation -> {
                                    LinearLayout.LayoutParams TvParams = (LinearLayout.LayoutParams) search.getLayoutParams();
                                    TvParams.width = (int) animation.getAnimatedValue();
                                    search.setLayoutParams(TvParams);
                                });

                                ValueAnimator EditWidthValueAnimator = ValueAnimator.ofInt(0,dip2px(50));
                                EditWidthValueAnimator.addUpdateListener(animation -> {
                                    LinearLayout.LayoutParams EdtParams = (LinearLayout.LayoutParams) username.getLayoutParams();
                                    EdtParams.width = (int) animation.getAnimatedValue();
                                    username.setLayoutParams(EdtParams);
                                });




                                set.playTogether(EditWidthValueAnimator,TextWidthValueAnimator);
                                set.setDuration(200);
                                set.addListener(new AnimatorListenerAdapter() {
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
                                set.start();


                            }
                        }
                    });


                    search.setOnClickListener(v->{
                        String uNameText = username.getText().toString().trim();
                        if(uNameText.equals("")){
                            Toast.makeText(context,"空值",Toast.LENGTH_SHORT).show();
                        }else{
                            SearchUserPop searchUserPop = new SearchUserPop(context);
                            searchUserPop.showPopUpWindow(avatar,-dip2px(5),-dip2px(70));
                            searchUserPop.searchContactInfo(uNameText);
                            searchUserPop.setContactOnSelectionListener(contact1 -> {
                                searchUserPop.dismissPopUpWindow();
                                boolean add = true;
                                for (int i = 0; i < contacts.size(); i++) {
                                    if(contacts.get(i).getLocalUser()!=null){
                                        if(contacts.get(i).getLocalUser().getUsername().equals(contact1.getLocalUser().getUsername()))add = false;
                                    }
                                }
                                if(add)addReceivers(contact1);
                                else Toast.makeText(context,"用户已添加",Toast.LENGTH_SHORT).show();
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
                if(data.get(position).getLocalUser()!=null){
                    return 0;
                }else{
                    return 1;
                }
            }
        });

        contacts = new AdapterList<>();
        contacts.relevantAdapter(contactJAdapter.adapter);
        contacts.add(new Contact(null,false));
    }



    public void addReceivers(Contact contact) {
        contacts.add(contacts.size()-1,contact);
    }

    @Override
    protected void initEvent() {
        avatar.setOnClickListener(v->{

        });
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){

        }
        return super.onKeyDown(keyCode, event);
    }
}
