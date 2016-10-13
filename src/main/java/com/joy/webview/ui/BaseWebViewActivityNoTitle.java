package com.joy.webview.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;

import com.joy.inject.module.ActivityModule;
import com.joy.share.JoyShare;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebViewComponent;
import com.joy.webview.component.DaggerBaseWebViewComponent;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.presenter.BaseWebViewPresenter;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.ui.interfaces.KConstant;

import javax.inject.Inject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Daisw on 16/9/7.
 */

public class BaseWebViewActivityNoTitle extends BaseHttpUiActivity implements BaseViewWeb, KConstant {

    private String mUrl;

    @Inject
    BaseWebViewPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component().inject(this);
        LayoutParams contentLp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        contentLp.bottomMargin = TITLE_BAR_HEIGHT;
        setContentView(mPresenter.getWebView(), contentLp);

        LayoutParams navBarLp = new LayoutParams(MATCH_PARENT, TITLE_BAR_HEIGHT);
        navBarLp.gravity = Gravity.BOTTOM;
        addContentView(initNavigationBar(), navBarLp);

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
        mPresenter.setUserAgent(getIntent().getStringExtra(KEY_USER_AGENT));
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
    public final void onReceivedTitle(WebView view, String title) {
    }

    @Override
    public void onProgress(WebView view, int progress) {
    }

    @Override
    public boolean onOverrideUrl(WebView view, String url) {
        return false;
    }

    private View initNavigationBar() {
        JoyShare joyShare = new JoyShare(this);
        joyShare.setData(joyShare.getDefaultItems());

        View v = inflateLayout(R.layout.lib_view_web_navigation_bar);
        v.findViewById(R.id.ivBack).setOnClickListener((v1) -> mPresenter.goBack());
        v.findViewById(R.id.ivClose).setOnClickListener((v1) -> finish());
        v.findViewById(R.id.ivBrowser).setOnClickListener((v1) -> showToast("browser"));
        v.findViewById(R.id.icMore).setOnClickListener((v1) -> joyShare.show());
        return v;
    }

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        startActivity(context, url, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable String userAgent) {
        Intent intent = new Intent(context, BaseWebViewActivityNoTitle.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_USER_AGENT, userAgent);
        context.startActivity(intent);
    }
}
