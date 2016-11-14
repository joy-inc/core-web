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
import android.widget.ProgressBar;

import com.joy.inject.module.ActivityModule;
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
    UIDelegate mDelegate;

    private BaseWebViewComponent component() {
        return DaggerBaseWebViewComponent.builder()
                .activityModule(new ActivityModule(this))
                .baseWebViewModule(new BaseWebViewModule(this, getIntent().getBooleanExtra(KEY_CACHE_ENABLE, false)))
                .build();
    }

    public final UIDelegate getUIDelegate() {
        return mDelegate;
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
        getUIDelegate().initTitleView();
    }

    @Override
    protected void initContentView() {
        getUIDelegate().initContentView();
    }

    public final String getUrl() {
        return getUIDelegate().getUrl();
    }

    @Override
    public void setTitle(CharSequence title) {
        getUIDelegate().setTitle(title);
    }

    public void setTitle(CharSequence title, boolean stable) {
        getUIDelegate().setTitle(title, stable);
    }

    public String getTitleText() {
        return getUIDelegate().getTitle();
    }

    public void setTitleMoreEnable(boolean enalbe) {
        getUIDelegate().setTitleMoreEnable(enalbe);
    }

    @Override
    public void onTitleMoreClick() {
        getUIDelegate().onTitleMoreClick();
    }

    public void setTitleCloseEnable(boolean enable) {
        getUIDelegate().setTitleCloseEnable(enable);
    }

    @Override
    public void onTitleCloseClick() {
        getUIDelegate().onTitleCloseClick();
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
    public NavigationBar initNavigationBar() {
        return getUIDelegate().initNavigationBar();
    }

    public final NavigationBar getNavigationBar() {
        return getUIDelegate().getNavigationBar();
    }

    public final IPresenter getPresenter() {
        return getUIDelegate().getPresenter();
    }

    @Override
    public List<ShareItem> getShareItems() {
        return getUIDelegate().getShareItems();
    }

    @Override
    public void onShareItemClick(int position, View v, ShareItem item) {
        getUIDelegate().onShareItemClick(position, v, item);
    }

    @Override
    public void doOnRetry() {
        getUIDelegate().reload();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
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
    public void onReceivedTitle(String title) {
        getUIDelegate().onReceivedTitle(title);
    }

    @Override
    public void onProgress(int progress) {
        getUIDelegate().onProgress(progress);
    }

    @Override
    public boolean onOverrideUrl(String url) {
        return getUIDelegate().onOverrideUrl(url);
    }

    @Override
    public void onShowCustomView(View view) {
        getUIDelegate().onShowCustomView(view);
    }

    @Override
    public void onHideCustomView() {
        getUIDelegate().onHideCustomView();
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        getUIDelegate().onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
    }

    @Override
    @TargetApi(HONEYCOMB)
    public void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType) {
        getUIDelegate().onShowFileChooser(filePathCallback, acceptType);
    }

    @Override
    @TargetApi(LOLLIPOP)
    public boolean onShowFileChooser(ValueCallback<Uri[]> filePathCallback) {
        return getUIDelegate().onShowFileChooser(filePathCallback);
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        getUIDelegate().onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
    }

    @Override
    public void onBackPressed() {
        getUIDelegate().onBackPressed();
    }

    public static void startSelf(@NonNull Context context, @NonNull String url) {
        startSelf(context, url, false);
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
