package com.edu.cdp.ui.activity;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.edu.cdp.R;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.custom.VoiceView;
import com.edu.cdp.database.bean.Email;
import com.edu.cdp.databinding.ActivityEmailBinding;
import com.edu.cdp.response.User;
import com.edu.cdp.ui.fragment.WebViewFragment;
import com.edu.cdp.utils.AndroidUtils;
import com.edu.cdp.utils.GsonUtil;

import java.io.IOException;
import java.util.Random;

public class EmailActivity extends BaseActivity<ActivityEmailBinding> {
    private Email email;
    private Random random;

    @Override
    protected int setContentView() {
        return R.layout.activity_email;
    }

    @Override
    protected void setData(ActivityEmailBinding binding) {

    }

    @Override
    protected void initViews(ActivityEmailBinding binding) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.topPanel.getLayoutParams();
        params.height = params.height + AndroidUtils.getStatusBarHeight(this);
        binding.topPanel.setLayoutParams(params);


        Intent intent = getIntent();
        email = (Email) intent.getSerializableExtra("email");

        binding.title.setText(email.getTitle());
        User user = GsonUtil.parserJsonToArrayBean(email.getSenduserinfo(), User.class);

        binding.username.setText(user.getNickname());

        random = new Random();
        binding.loadingView.setLoadingStyle(random.nextInt(4)+1);
//       res = random().nextInt(n-m+1)+m;  m,n

        
        jxContentAndSet(email.getContent());
        
        

    }

    private void jxContentAndSet(String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        for (String key:jsonObject.keySet()) {
            System.out.println("内容下子节点："+key);
            switch (key){
                case "text":
                    TextView textView = new TextView(this);
                    textView.setText(jsonObject.getString(key));
                    LinearLayout.LayoutParams  params =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 20;
                    params.topMargin = 20;
                    textView.setLayoutParams(params);
                    binding.other.addView(textView);
                    break;
                case "link":
                    handler.sendEmptyMessageDelayed(0,random.nextInt(2000)+3000);
                    break;
                case "voice":
                    VoiceView voiceView = new VoiceView(this);
                    LinearLayout.LayoutParams  params1 =  new LinearLayout.LayoutParams(100,100);
                    params1.leftMargin = 20;
                    params1.topMargin = 20;
                    voiceView.setLayoutParams(params1);
                    binding.other.addView(voiceView);
                    break;
            }
        }
        
        
        
    }


    private void LaunchAnimation(){
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(binding.back,"scaleX",1f,0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(binding.back,"scaleY",1f,0.5f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(binding.back,"translationY",0,-20);
        set.playTogether(scaleX,scaleY,translationY);
        set.setDuration(100);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });
        set.start();
    }

    @Override
    protected void setListeners(final ActivityEmailBinding binding) {
        binding.back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LaunchAnimation();
            }
        });
        binding.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开用户信息界面

            }
        });
//        binding.voiceview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RequestPermission(Manifest.permission.RECORD_AUDIO);
//                binding.voiceview.playAudio();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
//        binding.voiceview.stopPlay();
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.loadingView.setVisibility(View.INVISIBLE);
            JSONObject json = JSONObject.parseObject(email.getContent());
            String link = json.getString("link");
            if(!EmailActivity.this.isDestroyed())getSupportFragmentManager().beginTransaction().add(R.id.webViewParent,WebViewFragment.newInstance(link)).commit();
        }
    };
}