package com.edu.cdp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.transition.Fade;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;

import androidx.annotation.NonNull;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseActivity;
import com.edu.cdp.databinding.ActivityGuideBinding;

@SuppressWarnings("all")
public class GuideActivity extends BaseActivity<ActivityGuideBinding> {
    private JCountdownTimer jCountdownTimer;


    class JCountdownTimer extends CountDownTimer{

        public JCountdownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            System.out.println("倒计时："+l);
        }

        @Override
        public void onFinish() {
            System.out.println("倒计时结束");
            LunchAnimate();
        }
    }

    Handler startNewActivityHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            GuideActivity.this.startActivity(new Intent(GuideActivity.this,HomeActivity.class), ActivityOptions.makeSceneTransitionAnimation(GuideActivity.this).toBundle());
            GuideActivity.this.finish();
        }
    };


    private void LunchAnimate(){

        //动画集合
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(binding.imageView,"rotation",0,360);
        rotationAnimator.setInterpolator(new BounceInterpolator());
        rotationAnimator.setDuration(2000);

        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(binding.imageView,"translationY",0,-1000);
        translationAnimator.setDuration(1000);
        translationAnimator.setInterpolator(new AnticipateInterpolator());

        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(binding.imageView,"alpha",1f,0f);
        fadeAnimator.setDuration(1000);
        fadeAnimator.setInterpolator(new AnticipateInterpolator());



        ObjectAnimator textfadeAnimator = ObjectAnimator.ofFloat(binding.textView,"alpha",1f,0f);
        textfadeAnimator.setDuration(1000);
        textfadeAnimator.setInterpolator(new AnticipateInterpolator());

        ObjectAnimator texttranslationAnimator = ObjectAnimator.ofFloat(binding.textView,"translationY",0,1000);
        texttranslationAnimator.setDuration(1000);
        texttranslationAnimator.setInterpolator(new AnticipateInterpolator());

        set.play(translationAnimator).with(fadeAnimator).with(textfadeAnimator).with(texttranslationAnimator).after(rotationAnimator);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startNewActivityHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_guide;
    }

    @Override
    protected void setData(ActivityGuideBinding binding) {

    }

    @Override
    protected void initViews(ActivityGuideBinding binding) {
        jCountdownTimer = new JCountdownTimer(1000,1000);
        jCountdownTimer.start();
        getWindow().setExitTransition(new Fade().setDuration(50));
        getWindow().setEnterTransition(new Fade().setDuration(50));
    }

    @Override
    protected void setListeners(ActivityGuideBinding binding) {

    }

}