package com.joy.webview.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.widget.TextView;

import com.joy.inject.module.ActivityModule;
import com.joy.share.JoyShare;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebViewComponent;
import com.joy.webview.component.DaggerBaseWebViewComponent;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.presenter.BaseWebViewPresenter;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.ui.interfaces.KConstant;

import javax.inject.Inject;

/**
 * Created by Daisw on 16/8/14.
 */

public class BaseWebViewActivity extends BaseHttpUiActivity implements BaseViewWeb, KConstant {

    private String mUrl;
    private String mTitle;
    private TextView mTvTitle;

    @Inject
    BaseWebViewPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component().inject(this);
        setContentView(mPresenter.getWebView());
        mPresenter.load(mUrl);
    }

    private BaseWebViewComponent component() {
        return DaggerBaseWebViewComponent.builder()
                .activityModule(new ActivityModule(this))
                .baseWebViewModule(new BaseWebViewModule(this))
                .build();
    }

    protected IPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void initData() {
        mUrl = getIntent().getStringExtra(KEY_URL);
        mTitle = getIntent().getStringExtra(KEY_TITLE);
        mPresenter.setUserAgent(getIntent().getStringExtra(KEY_USER_AGENT));
    }

    @Override
    protected void initTitleView() {
        JoyShare joyShare = new JoyShare(this);
        joyShare.setData(joyShare.getDefaultItems());

        addTitleLeftBackView(R.drawable.ic_arrow_back_white_24dp);
        addTitleRightView(R.drawable.ic_more_vert_white_24dp, (v) -> joyShare.show());
        mTvTitle = addTitleMiddleView(mTitle);
    }

    @Override
    protected void doOnRetry() {
        mPresenter.reload();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(WebView view, String url) {
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (TextUtil.isEmpty(mTitle))
            mTvTitle.setText(title);
    }

    @Override
    public void onProgress(WebView view, int progress) {
    }

    @Override
    public boolean onOverrideUrl(WebView view, String url) {
        return false;
    }

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        startActivity(context, url, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable String title) {
        startActivity(context, url, title, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable String title, @Nullable String userAgent) {
        Intent intent = new Intent(context, BaseWebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_USER_AGENT, userAgent);
        context.startActivity(intent);
    }
}
