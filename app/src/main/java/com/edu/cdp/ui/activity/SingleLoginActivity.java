package com.edu.cdp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.application.JApplication;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.base.BaseDialog;
import com.edu.cdp.bean.Constants;
import com.edu.cdp.database.bean.LocalUser;
import com.edu.cdp.databinding.ActivitySingleLoginBinding;
import com.edu.cdp.eventbus.event.LoginEvent;
import com.edu.cdp.model.manager.ModelManager;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.request.Login;
import com.edu.cdp.ui.dialog.LoadingDialog;
import com.edu.cdp.utils.AndroidUtils;
import com.edu.cdp.utils.BlurUtils;

import org.greenrobot.eventbus.EventBus;

import static android.graphics.Typeface.createFromAsset;

public class SingleLoginActivity extends BaseActivity<ActivitySingleLoginBinding> {
    private LocalUser login;
    private LoadingDialog loadingDialog;

    @Override
    protected int setContentView() {
        return R.layout.activity_single_login;
    }

    @Override
    protected void setData(ActivitySingleLoginBinding binding) {
        Intent intent = getIntent();
        login = (LocalUser) intent.getSerializableExtra("LOGIN");
    }

    @Override
    protected void initViews(ActivitySingleLoginBinding binding) {
        //设置顶部被状态栏遮挡的高度
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.topBar.getLayoutParams();
        params.height = AndroidUtils.getStatusBarHeight(this);
        binding.topBar.setLayoutParams(params);



        //设置顶部字体
        binding.text.setTypeface(createFromAsset(getAssets(), "fonts/Lobster-Regular.ttf"));
        binding.text.setText(getString(R.string.app_name));


        //设置数据
        if(login==null)finish();
        binding.username.setText(login.getUsername().substring(0,login.getUsername().lastIndexOf("@")));


        loadingDialog = new LoadingDialog(this);
    }

    @Override
    protected void setListeners(ActivitySingleLoginBinding binding) {
        binding.loginBtn.setOnClickListener(v->{
            loadingDialog.showDialog();
        });

        loadingDialog.setDialogListener(new BaseDialog.Listener() {
            @Override
            public void showListener(DialogInterface dialogInterface) {
                login();
            }

            @Override
            public void dismissListener(DialogInterface dialogInterface) {

            }
        });
    }

    private void login(){
        OkHttpUtils.POST(Constants.LOGIN_URL, null, new Login(login.getUsername(), login.getPassword()), new OkHttpUtils.Jcallback() {
            @Override
            public void onFailure() {
                loadingDialog.dismissDialog();
            }


            @Override
            public boolean onResponseAsync(JSONObject response){
                int code = response.getInteger("code");
                final String msg = response.getString("msg");

                if(code == 400){
                    String data = response.getString("data");
                    JSONObject json  = JSONObject.parseObject(data);
                    final LocalUser localUser = new LocalUser(
                            json.getInteger("id"),
                            json.getString("username"),
                            json.getString("password"),
                            json.getString("nickname"),
                            json.getString("avatar"),
                            json.getString("uuid"),
                            login.isMainAccount()
                    );
                    JApplication.getInstance().getDb().userDao().updateUsers(localUser);
                    return true;
                }else{
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SingleLoginActivity.this,msg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return false;
            }

            @Override
            public void onSuccess() {
                loadingDialog.dismissDialog();
                ModelManager.getManager().refreshAccountModel();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    EventBus.getDefault().post(new LoginEvent("login"));
                    finish();
                },1000);
            }
        });
    }

}