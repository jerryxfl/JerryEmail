package com.edu.cdp.ui.activity;

import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.cdp.R;
import com.edu.cdp.adapter.BaseViewHolder;
import com.edu.cdp.adapter.JAdapter;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.bean.Setting2;
import com.edu.cdp.custom.SwitchButton;
import com.edu.cdp.databinding.ActivityGeneralSettingsBinding;
import com.edu.cdp.utils.AdapterList;
import com.edu.cdp.utils.AndroidUtils;
import com.edu.cdp.utils.DataCleanManager;

import java.io.File;
import java.util.List;

public class GeneralSettingsActivity extends BaseActivity<ActivityGeneralSettingsBinding> {

    @Override
    protected int setContentView() {
        return R.layout.activity_general_settings;
    }

    @Override
    protected void setData(ActivityGeneralSettingsBinding binding) {

    }

    @Override
    protected void initViews(ActivityGeneralSettingsBinding binding) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.topPanel.getLayoutParams();
        params.topMargin = AndroidUtils.getStatusBarHeight(this);
        binding.topPanel.setLayoutParams(params);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.settings.setLayoutManager(layoutManager);
        binding.settings.setHasFixedSize(true);
        binding.settings.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        JAdapter<Setting2> setting2JAdapter = new JAdapter<>(
                this,
                binding.settings,
                new int[]{R.layout.setting_null_layout, R.layout.setting_layout, R.layout.settinh_switch_layout},
                new JAdapter.DataListener<Setting2>() {
                    @Override
                    public void initItem(BaseViewHolder holder, int position, List<Setting2> data) {
                        Setting2 setting2 = data.get(position);

                        if (setting2.getType() != 0) {
                            TextView name = holder.findViewById(R.id.name);
                            name.setText(setting2.getName());
                        }

                        if (setting2.getType() == 2) {
                            SwitchButton switch_button = holder.findViewById(R.id.switch_button);
                            setting2.getInit().initialize(switch_button);
                        }

                        RelativeLayout container = holder.findViewById(R.id.container);
                        container.setOnClickListener(v -> setting2.getClickListener().onCLick());

                    }

                    @Override
                    public void updateItem(BaseViewHolder holder, int position, List<Setting2> data, String tag) {

                    }

                    @Override
                    public int getItemViewType(int position, List<Setting2> data) {
                        return data.get(position).getType();
                    }
                }
        );


        AdapterList<Setting2> settings = new AdapterList<>();
        settings.relevantAdapter(setting2JAdapter.adapter);

        settings.add(new Setting2(0));
        settings.add(new Setting2("清除已下载语音", 1, () -> {
            if (GeneralSettingsActivity.this.RequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && GeneralSettingsActivity.this.RequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String saveDir = getFilesDir().toString() + File.separator + "voice" + File.separator;
                DataCleanManager.cleanCustomCache(saveDir);
                String saveDir1 = getFilesDir().toString() + File.separator + "record" + File.separator;
                DataCleanManager.cleanCustomCache(saveDir1);
                Toast.makeText(GeneralSettingsActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
            }
        }));
        settings.add(new Setting2("发信音效", 2, () -> {

        }, view -> {
            SwitchButton switchButton = (SwitchButton) view;
            int voice = mmkv.decodeInt("voice");

            if (voice == 1) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> switchButton.setChecked(true), 50);
            }

            switchButton.addCheckListener(new SwitchButton.CheckListener() {
                @Override
                public void onClick() {

                }

                @Override
                public void onOpen() {
                    mmkv.encode("voice", 1);
                }

                @Override
                public void onClose() {
                    mmkv.encode("voice", 0);
                }
            });
        }));
    }

    @Override
    protected void setListeners(ActivityGeneralSettingsBinding binding) {
        binding.back.setOnClickListener(v -> {
            finish();
        });

    }
}