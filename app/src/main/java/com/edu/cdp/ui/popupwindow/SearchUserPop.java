package com.edu.cdp.ui.popupwindow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BasePopupWindow;
import com.edu.cdp.bean.Constants;
import com.edu.cdp.bean.Contact;
import com.edu.cdp.custom.CircleOnlineAvatar;
import com.edu.cdp.custom.LoadingView;
import com.edu.cdp.model.manager.ModelManager;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.response.User;
import com.edu.cdp.utils.AdapterList;
import com.edu.cdp.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchUserPop extends BasePopupWindow {
    private LoadingView loading;
    private RecyclerView contactRecycler;
    private JAdapter<Contact> contactJAdapter;
    private AdapterList<Contact> contacts;
    private ContactOnSelectionListener contactOnSelectionListener;


    public SearchUserPop(Context context) {
        super(context);
    }

    @Override
    public View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.contacts_pop_layout,null,false);
    }

    @Override
    public void initView(View view) {
        loading = view.findViewById(R.id.loading);
        contactRecycler = view.findViewById(R.id.contactRecycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        contactRecycler.setLayoutManager(layoutManager);
        contactRecycler.setHasFixedSize(true);

        contactJAdapter = new JAdapter<>(mContext, contactRecycler, new int[]{R.layout.pop_contact_item_layout}, new JAdapter.DataListener<Contact>() {
            @Override
            public void initItem(BaseViewHolder holder, int position, List<Contact> data) {
                Contact contact = data.get(position);

                TextView username = holder.findViewById(R.id.username);
                CircleOnlineAvatar avatar = holder.findViewById(R.id.avatar);
                Glide.with(mContext)
                        .load(contact.getLocalUser().getAvatar())
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                avatar.setDrawable(resource);
                            }
                        });
                username.setText(contact.getLocalUser().getNickname());
                avatar.setOnClickListener(v->{
                    if(contactOnSelectionListener!=null)contactOnSelectionListener.onSelect(contact);
                });
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

    }

    public void searchContactInfo(String uNameText){
        System.out.println("搜索值："+uNameText);
        OkHttpUtils.GET(Constants.GETCONTACTINFODIM + uNameText, null, new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {
                searchContactInfoLocal(uNameText);
            }

            @Override
            public boolean onResponseAsync(JSONObject response) {
                int code = response.getInteger("code");
                if(code == 400){
                    List<User> data = GsonUtil.jsonToList(response.getString("data"), User.class);
                    if(data.size()==0){
                        searchContactInfoLocal(uNameText);
                    }else{
                        new Handler(Looper.getMainLooper()).post(() -> {
                            loading.setVisibility(View.GONE);
                            contactRecycler.setVisibility(View.VISIBLE);
                        });
                        for (User user : data) {
                            System.out.println(user.getNickname()+"  "+user.getAvatar());
                            if(!user.getUsername().equals(ModelManager.getManager().getMainAccountModel().getUser().getValue().getLocalUser().getUsername()))
                                contacts.add(0,new Contact(user, false));
                        }
                    }
                    return  true;
                }
                return false;
            }

            @Override
            public void onSuccess() {
            }
        });
    }

    private void searchContactInfoLocal(String uNameText){
        List<Contact> value = ModelManager.getManager().getContactModel().getContacts().getValue();
        for (Contact contact : value) {
            if(contact.getLocalUser()!=null)if(contact.getLocalUser().getUsername().equals(uNameText)){
                contacts.add(0,contact);
            }
        }

        if(contacts.size()==0){
            new Handler(Looper.getMainLooper()).postDelayed(this::dismissPopUpWindow,1000);
        }

    }

    @Override
    public boolean setCanceledOnTouchOutside() {
        return true;
    }

    @Override
    public int setAnimationStyle() {
        return R.style.anim_menu_bottombar;
    }

    public void setContactOnSelectionListener(ContactOnSelectionListener contactOnSelectionListener){
        this.contactOnSelectionListener = contactOnSelectionListener;
    }

    public interface ContactOnSelectionListener {
        void onSelect(Contact contact);
    }
}
