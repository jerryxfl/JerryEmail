package com.edu.cdp.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.edu.cdp.ui.dialog.ConfirmDialog;
import com.edu.cdp.ui.dialog.PermissionDialog;
import com.edu.cdp.utils.InitApp;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public abstract class BaseActivity<DATABIND extends ViewDataBinding> extends AppCompatActivity implements LifecycleOwner {
    private LifecycleRegistry lifecycleRegistry;
    private ConfirmDialog confirmDialog;
    private String permission;
    private final int NOT_NOTICE = 2;
    private PermissionDialog permissionDialog;

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        if(lifecycleRegistry==null){
            lifecycleRegistry = new LifecycleRegistry(this);
        }
        return lifecycleRegistry;
    }
    //获得布局id
    protected abstract int setContentView();

    //设置布局数据
    protected abstract void setData(DATABIND binding);

    //初始化布局
    protected abstract void initViews(DATABIND binding);

    //设置监听器
    protected abstract void setListeners(DATABIND binding);

    protected DATABIND binding;

    protected MMKV mmkv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);

        //透明状态栏
        setWindow();
        binding = DataBindingUtil.setContentView(this, setContentView());
        getSupportActionBar().hide();

        InitApp.getInstance().addActivity(this);
        mmkv = MMKV.defaultMMKV();
        setData(binding);
        binding.setLifecycleOwner(this);
        initViews(binding);
        setListeners(binding);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);


        //透明状态栏
        setWindow();
        binding = DataBindingUtil.setContentView(this, setContentView());
        getSupportActionBar().hide();

        InitApp.getInstance().addActivity(this);
        mmkv = MMKV.defaultMMKV();
        setData(binding);
        binding.setLifecycleOwner(this);
        initViews(binding);
        setListeners(binding);
    }


    @Override
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    protected void onPause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        InitApp.getInstance().removeActivity(this);
        if (confirmDialog != null) {
            confirmDialog.dismissDialog();
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }


    private void setWindow() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);//可使用切换动画
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    protected void SignEventBus() {
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (InitApp.getInstance().isLast() && keyCode == KeyEvent.KEYCODE_BACK) {
            if (confirmDialog == null) {
                confirmDialog = new ConfirmDialog.Builder(this)
                        .setTitle("提示")
                        .setContent("是否退出APP？")
                        .setConfirmClickListener(new ConfirmDialog.ConfirmClickListener() {
                            @Override
                            public void onConfirmClick(ConfirmDialog confirmDialog) {
                                finish();
                            }
                        })
                        .setCancelClickListener(new ConfirmDialog.CancelClickListener() {
                            @Override
                            public void onCancelClick(ConfirmDialog confirmDialog) {
                                confirmDialog.dismissDialog();
                            }
                        })
                        .build();
            }
            confirmDialog.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public boolean RequestPermission(String permission) {
        this.permission = permission;
        if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
            return false;
        } else {
//            Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
            //权限已申请

            if(permissionDialog!=null)permissionDialog.dismissDialog();
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问
                        if(permissionDialog==null){
                            permissionDialog = new PermissionDialog(this);
                            permissionDialog.setListener(new PermissionDialog.Listener() {
                                @Override
                                public void onConfirm() {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                    intent.setData(uri);
                                    startActivityForResult(intent, NOT_NOTICE);
                                }
                            });
                        }
                    } else {//选择禁止
                        if(permissionDialog==null){
                            permissionDialog = new PermissionDialog(this);
                            permissionDialog.setListener(new PermissionDialog.Listener() {
                                @Override
                                public void onConfirm() {
                                    ActivityCompat.requestPermissions(BaseActivity.this,
                                            new String[]{permission}, 1);
                                }
                            });
                        }
                    }
                    permissionDialog.showDialog();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            RequestPermission(permission);//由于不知道是否选择了允许所以需要再次判断
        }
    }
}
