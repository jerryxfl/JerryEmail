package com.edu.cdp.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseFragment;
import com.edu.cdp.databinding.FragmentWebViewBinding;
import com.edu.cdp.databinding.FragmentWebViewBindingImpl;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultWebClient;

public class WebViewFragment extends BaseFragment<FragmentWebViewBinding> {
    private AgentWeb agentWeb;
    private static final String ARG = "link";

    public static Fragment newInstance(String arg){
        WebViewFragment fragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString( ARG, arg);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int setContentView() {
        return R.layout.fragment_web_view;
    }

    @Override
    protected int setData(FragmentWebViewBinding binding) {
        return 0;
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView(FragmentWebViewBinding binding) {
        String url = getArguments().getString(ARG);
        System.out.println("访问的url:"+url);
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(binding.webParent,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT) )
                .useDefaultIndicator(-1,3)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);
        AgentWebConfig.debug();
        agentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        agentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        agentWeb.getWebCreator().getWebView().getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        agentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        agentWeb.getWebCreator().getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        agentWeb.getWebCreator().getWebView().getSettings().setLoadsImagesAutomatically(true);
        agentWeb.getWebCreator().getWebView().getSettings().setNeedInitialFocus(true);
        agentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        agentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        agentWeb.getWebCreator().getWebView().getSettings().setDomStorageEnabled(true);
        agentWeb.getWebCreator().getWebView().getSettings().setBuiltInZoomControls(true);
        agentWeb.getWebCreator().getWebView().getSettings().setSupportZoom(true);
        agentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccess(true);
        agentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        agentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);


        agentWeb.getWebCreator().getWebView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK && agentWeb.getWebCreator().getWebView().canGoBack()) { // 表示按返回键时的操作
                        agentWeb.getWebCreator().getWebView().goBack(); // 后退
                        // webview.goForward();//前进
                        return true; // 已处理
                    } else if (i == KeyEvent.KEYCODE_BACK) {
                        getActivity().moveTaskToBack(true);
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void setListeners(FragmentWebViewBinding binding) {

    }


    @Override
    public void onPause() {
        agentWeb.getWebLifeCycle().onPause(); //暂停应用内所有WebView ， 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        super.onPause();
    }

    @Override
    public void onResume() {
        agentWeb.getWebLifeCycle().onResume();//恢复
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        agentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }
}