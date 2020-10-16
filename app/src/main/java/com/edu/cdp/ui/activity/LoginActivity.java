package com.edu.cdp.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.edu.cdp.R;
import com.edu.cdp.application.JApplication;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.base.BaseDialog;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Constants;
import com.edu.cdp.database.bean.LocalUser;
import com.edu.cdp.database.dao.UserDao;
import com.edu.cdp.databinding.ActivityLoginBinding;
import com.edu.cdp.eventbus.event.LoginEvent;
import com.edu.cdp.model.manager.ModelManager;
import com.edu.cdp.net.okhttp.OkHttpUtils;
import com.edu.cdp.request.Login;
import com.edu.cdp.ui.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private LoadingDialog loadingDialog;
    private UserDao userDao;
    private LocalUser login;

    @Override
    protected int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void setData(ActivityLoginBinding binding) {

    }

    @Override
    protected void initViews(final ActivityLoginBinding binding) {
        loadingDialog = new LoadingDialog(this);
        userDao = JApplication.getInstance().getDb().userDao();
        Intent intent = getIntent();

        login = (LocalUser) intent.getSerializableExtra("login");
        if(login!=null){


            binding.email.setText(login.getUsername());
            binding.password.setText(login.getPassword());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(LoginActivity.this)
                            .load(login.getAvatar())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    binding.avatar.setDrawable(resource);
                                }
                            });

                }
            });
        }
    }

    @Override
    protected void setListeners(final ActivityLoginBinding binding) {
        loadingDialog.setDialogListener(new BaseDialog.Listener() {
            @Override
            public void showListener(DialogInterface dialogInterface) {
                binding.loginbtn.setEnabled(false);
                Login();
            }

            @Override
            public void dismissListener(DialogInterface dialogInterface) {
                binding.loginbtn.setEnabled(true);
            }
        });

        binding.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.showDialog();
            }
        });
    }


    private void Login() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();


        if(email.equals("")){
            loadingDialog.dismissDialog();
            binding.email.setError("邮箱地址不能为空");
            return;
        }

        final List<LocalUser> allLocalUser = userDao.getAllUser();
        if(login==null)for (LocalUser u : allLocalUser) {
            if(u.getUsername().equals(email)){
                loadingDialog.dismissDialog();
                binding.email.setError("该账号已经登录");
                return;
            }
        }

        if(password.equals("")){
            loadingDialog.dismissDialog();
            binding.password.setError("密码不能为空");
            return;
        }

        OkHttpUtils.POST(Constants.LOGIN_URL, null, new Login(email, password), new OkHttpUtils.Jcallback() {
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
                    if(login == null){
                        final LocalUser localUser = new LocalUser(
                                json.getInteger("id"),
                                json.getString("username"),
                                json.getString("password"),
                                json.getString("nickname"),
                                json.getString("avatar"),
                                json.getString("uuid"),
                                allLocalUser.isEmpty()
                        );
                        userDao.insertOneUser(localUser);
                        new Handler(Looper.getMainLooper()).post(() -> Glide.with(LoginActivity.this)
                                .load(localUser.getAvatar())
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        binding.avatar.setDrawable(resource);
                                    }
                                }));
                    }else{
                        login.setUUID(json.getString("uuid"));
                        userDao.updateUsers(login);
                    }

                    return true;
                }else{
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return false;
            }

            @Override
            public void onSuccess() {
                loadingDialog.dismissDialog();
                binding.loginbtn.setEnabled(false);
                ModelManager.getManager().refreshAccountModel();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new LoginEvent("login"));
                        finish();
                    }
                },1000);
            }
        });
    }
}


