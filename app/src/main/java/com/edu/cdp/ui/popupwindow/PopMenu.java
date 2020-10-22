package com.edu.cdp.ui.popupwindow;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BasePopupWindow;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Contact;
import com.edu.cdp.bean.MenuItem;
import com.edu.cdp.custom.AvatarView;
import com.edu.cdp.net.websocket.CommandType;
import com.edu.cdp.net.websocket.WebSocketManager;
import com.edu.cdp.net.websocket.bean.ServerRequest;

import org.java_websocket.client.WebSocketClient;

import java.util.ArrayList;
import java.util.List;

public class PopMenu extends BasePopupWindow {
    private View anchorView;
    private RecyclerView menuRecyclerView;
    private List<MenuItem> menuItems;
    private Account account;
    private Contact contact;

    public PopMenu(Context context,Account account,Contact contact,View anchorView) {
        super(context);
        this.account = account;
        this.contact = contact;
        this.anchorView = anchorView;
    }

    @Override
    public View setContentView() {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(mContext).inflate(R.layout.popmenu_layout, null, false);
        return v;
    }

    @Override
    public void initView(View view) {
        menuRecyclerView = view.findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        menuRecyclerView.setLayoutManager(layoutManager);
        menuRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        JAdapter<MenuItem> menuItemJAdapter = new JAdapter<>(
                mContext,
                menuRecyclerView,
                new int[]{R.layout.menu_item_layout},
                new JAdapter.DataListener<MenuItem>() {
                    @Override
                    public void initItem(BaseViewHolder holder, int position, List<MenuItem> data) {
                        final MenuItem menuItem = data.get(position);

                        ImageView icon = holder.findViewById(R.id.icon);
                        TextView name = holder.findViewById(R.id.name);
                        RelativeLayout container = holder.findViewById(R.id.container);


                        icon.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), menuItem.getIcon()));
                        name.setText(menuItem.getName());
                        container.setOnClickListener(view1 -> {
                            if (menuItem.getMenuOnClickListener() != null)
                                menuItem.getMenuOnClickListener().onClick();

                            dismissPopUpWindow();
                        });

                    }

                    @Override
                    public void updateItem(BaseViewHolder holder, int position, List<MenuItem> data, String tag) {

                    }

                    @Override
                    public int getItemViewType(int position, List<MenuItem> data) {
                        return 0;
                    }
                }
        );

        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(
                R.drawable.vibration,
                "摇一摇",
                () -> {
                    AvatarView avatarView = (AvatarView) anchorView;
                    if(avatarView.isOnLine()){
                        WebSocketClient client = WebSocketManager.getInstance().getClient();
                        if(client!=null){
                            if(client.isOpen()){
                                //发送震动命令
                                ServerRequest serverRequest = new ServerRequest(CommandType.VIBRATION,
                                        account.getLocalUser().getId()+"",
                                        contact.getLocalUser().getId()+"",
                                        "");
                                client.send(JSONObject.toJSONString(serverRequest));

                                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                                        anchorView,
                                        "translationX",
                                        0f,dip2px(mContext,10),-dip2px(mContext,10),dip2px(mContext,10),-dip2px(mContext,10),0f);
                                objectAnimator.setDuration(300);
                                objectAnimator.setInterpolator(new DecelerateInterpolator());
                                objectAnimator.start();
                            }
                        }
                    }
                }
        ));

        menuItems.add(new MenuItem(
                R.drawable.delete,
                "删除",
                () -> {

                }
        ));


        menuItemJAdapter.adapter.setData(menuItems);

    }


    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    public boolean setCanceledOnTouchOutside() {
        return true;
    }

    @Override
    public int setAnimationStyle() {
        return R.style.anim_menu_bottombar;
    }
}
