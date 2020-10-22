package com.edu.cdp.ui.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BaseDialog;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Contact;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.response.User;
import com.edu.cdp.utils.AdapterList;

import java.util.List;

public class WEmailDialog extends BaseDialog {
    private Account account;
    private CircleOnlineAvatar avatar;
    private RecyclerView recyclerView;
    private AdapterList<Contact> contacts;

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
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.b_dialog_animate);
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.receiveRecycler);

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

        JAdapter<Contact> contactJAdapter = new JAdapter<>(context, recyclerView, new int[]{R.layout.write_email_contact_item_layout}, new JAdapter.DataListener<Contact>() {
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
                    avatar.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.add));
                    avatar.setOnClickListener(v->{
                        //添加新的接收方


                    });
                }
            }

            @Override
            public void updateItem(BaseViewHolder holder, int position, List<Contact> data, String tag) {

            }

            @Override
            public int getItemViewType(int position, List<Contact> data) {
                return 0;
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
}
