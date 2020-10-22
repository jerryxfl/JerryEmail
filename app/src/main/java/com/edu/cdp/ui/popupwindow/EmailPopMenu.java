package com.edu.cdp.ui.popupwindow;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BasePopupWindow;
import com.edu.cdp.bean.MenuItem;
import com.edu.cdp.utils.AdapterList;

import java.util.List;

public class EmailPopMenu extends BasePopupWindow {
    private RecyclerView menuRecyclerView;

    public EmailPopMenu(Context context) {
        super(context);
    }

    @Override
    public View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.email_menu_layout,null,false);
    }

    @Override
    public void initView(View view) {
        menuRecyclerView = view.findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        menuRecyclerView.setLayoutManager(layoutManager);

        JAdapter<MenuItem> menuItemJAdapter = new JAdapter<>(mContext, menuRecyclerView, new int[]{R.layout.menu_item_layout}, new JAdapter.DataListener<MenuItem>() {
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
        });


        AdapterList<MenuItem> menuItems = new AdapterList<>();
        menuItems.relevantAdapter(menuItemJAdapter.adapter);
        menuItems.add(new MenuItem(
                R.drawable.write_email,
                "写邮件",
                () -> {

                }
        ));

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
