package com.joy.webview.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.joy.inject.module.ActivityModule;
import com.joy.share.ShareAdapter;
import com.joy.share.ShareItem;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.webview.component.BaseWebViewComponent;
import com.joy.webview.component.DaggerBaseWebViewComponent;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.ui.interfaces.KConstant;
import com.joy.webview.view.NavigationBar;

import java.util.List;

import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 16/9/7.
 */

public class BaseWebViewActivity extends BaseHttpUiActivity implements BaseViewWeb, KConstant {

    @Inject
    IPresenter mPresenter;

    @Inject
    UIDelegate mUIDelegate;

    private BaseWebViewComponent component() {
        return DaggerBaseWebViewComponent.builder()
                .activityModule(new ActivityModule(this))
                .baseWebViewModule(new BaseWebViewModule(this, getIntent().getBooleanExtra(KEY_CACHE_ENABLE, false)))
                .build();
    }

    @Override
    public final IPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public final UIDelegate getUIDelegate() {
        return mUIDelegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);
        getUIDelegate().onCreate();
    }

    @Override
    public void resolveThemeAttribute() {
        super.resolveThemeAttribute();
        getUIDelegate().resolveThemeAttribute();
    }

    @Override
    protected void initData() {
        getUIDelegate().initData();
    }

    @Override
    protected void initTitleView() {
        super.initTitleView();
        getUIDelegate().initTitleView();
    }

    @Override
    protected void initContentView() {
        getUIDelegate().initContentView();
    }

    public final String getInitialUrl() {
        return getUIDelegate().getInitialUrl();
    }

    public final String getUrl() {
        return getPresenter().getUrl();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        getUIDelegate().setTitle(title);
    }

    public void setTitle(CharSequence title, boolean fixed) {
        super.setTitle(title);
        getUIDelegate().setTitle(title, fixed);
    }

    public String getTitleText() {
        return getPresenter().getTitle();
    }

//    @Override
//    public void setTitleColor(@ColorInt int color) {
//        getUIDelegate().setTitleColor(color);
//    }

    @Override
    public void onTitleBackClick(View v) {
        getPresenter().goBack();
    }

//    public void setTitleMoreEnable(boolean enable) {
//        getUIDelegate().setTitleMoreEnable(enable);
//    }

    @Override
    public void onTitleMoreClick(View v) {
        if (v.getAlpha() == 1.f) {
            getUIDelegate().showShare();
        }
    }

    public void setTitleCloseEnable(boolean enable) {
        getUIDelegate().setTitleCloseEnable(enable);
    }

    @Override
    public void onTitleCloseClick() {
        finish();
    }

    @Override
    public boolean isProgressEnabled() {
        return getUIDelegate().isProgressEnabled();
    }

    @Override
    public ProgressBar initProgressBar() {
        return getUIDelegate().initProgressBar();
    }

    @Override
    @Nullable
    public NavigationBar initNavigationBar() {
        return getUIDelegate().initNavigationBar();
    }

    public final NavigationBar getNavigationBar() {
        return getUIDelegate().getNavigationBar();
    }

    public final void addNavigationBar(@NonNull NavigationBar navBar) {
        getUIDelegate().addNavigationBar(navBar);
    }

    public final void addNavigationBar(@NonNull NavigationBar navBar, @NonNull LayoutParams lp) {
        getUIDelegate().addNavigationBar(navBar, lp);
    }

    public final void addNavigationBar(@NonNull NavigationBar navBar, boolean animate) {
        getUIDelegate().addNavigationBar(navBar, animate);
    }

    public final void addNavigationBar(@NonNull NavigationBar navBar, @NonNull LayoutParams lp, boolean animate) {
        getUIDelegate().addNavigationBar(navBar, lp, animate);
    }

    public final void setNavigationBarVisible(boolean visible) {
        getUIDelegate().setNavigationBarVisible(visible);
    }

    @Override
    public void onNavCustomItemClick(ImageView view) {
    }

    @Override
    public final ShareAdapter getShareAdapter() {
        return getUIDelegate().getJoyShare().getAdapter();
    }

    @Override
    public List<ShareItem> getShareItems() {
        return getUIDelegate().getDefaultShareItems();
    }

    @Override
    public final void addShareItem(ShareItem item) {
        getUIDelegate().getJoyShare().add(item);
    }

    @Override
    public final void addShareItem(int position, ShareItem item) {
        getUIDelegate().getJoyShare().add(position, item);
    }

    @Override
    public final void addShareItems(List<ShareItem> items) {
        getUIDelegate().getJoyShare().addAll(items);
    }

    @Override
    public final void addShareItems(int position, List<ShareItem> items) {
        getUIDelegate().getJoyShare().addAll(position, items);
    }

    @Override
    public boolean onShareItemClick(int position, View v, ShareItem item) {
        return getUIDelegate().onShareItemClick(item);
    }

    @Override
    public void doOnRetry() {
        getPresenter().reload();
    }

    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        getUIDelegate().onPageStarted(url, favicon);
    }

    @Override
    public void onPageFinished(String url) {
        getUIDelegate().onPageFinished(url);
    }

    @Override
    public void onReceivedError(int errorCode, String description, String failingUrl) {
        getUIDelegate().onReceivedError(errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedTitle(WebView webView, String title) {
        getUIDelegate().onReceivedTitle(title);
    }

    @Override
    public void onProgress(WebView webView, int progress) {
        getUIDelegate().onProgress(progress);
    }

    @Override
    public boolean onOverrideUrl(WebView webView, String url) {
        return false;
    }

    @Override
    public WebResourceResponse onInterceptRequest(WebView webView, String url) {
        return null;
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
    }

    @Override
    public void onHideCustomView() {
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    @Override
    @TargetApi(HONEYCOMB)
    public void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType) {
    }

    @Override
    @TargetApi(LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback) {
        return false;
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        getUIDelegate().onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
    }

    @Override
    public void onBackPressed() {
        getPresenter().goBack();
    }

    public static void startSelf(@NonNull Context context, @NonNull String url) {
        startSelf(context, url, false);
    }

    public static void startSelf(@NonNull Context context, @NonNull String url, @Nullable CharSequence title) {
        startSelf(context, url, title, false);
    }

    public static void startSelf(@NonNull Context context, @NonNull String url, boolean cacheEnable) {
        startSelf(context, url, null, cacheEnable);
    }

    public static void startSelf(@NonNull Context context, @NonNull String url, @Nullable CharSequence title, boolean cacheEnable) {
        startTarget(BaseWebViewActivity.class, context, url, title, cacheEnable);
    }

    public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url) {
        startTarget(target, context, url, false);
    }

    public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url, @Nullable CharSequence title) {
        startTarget(target, context, url, title, false);
    }

    public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url, boolean cacheEnable) {
        startTarget(target, context, url, null, cacheEnable);
    }

    public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url, @Nullable CharSequence title, boolean cacheEnable) {
        Intent intent = new Intent(context, target);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_CACHE_ENABLE, cacheEnable);
        context.startActivity(intent);
    }
}
