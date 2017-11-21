package com.joy.webview.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.joy.inject.module.ActivityModule;
import com.joy.share.ShareItem;
import com.joy.ui.fragment.BaseHttpUiFragment;
import com.joy.webview.component.BaseWebX5Component;
import com.joy.webview.component.DaggerBaseWebX5Component;
import com.joy.webview.module.BaseWebX5Module;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWebX5;
import com.joy.webview.ui.interfaces.KConstant;
import com.joy.webview.view.NavigationBar;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.List;

import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 2017/11/14.
 */

public class BaseWebX5Fragment extends BaseHttpUiFragment implements BaseViewWebX5<FragmentEvent>, KConstant {

    @Inject
    IPresenter mPresenter;

    @Inject
    UIDelegateX5 mUIDelegate;

    String mUrl;

    @Override
    public IPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public UIDelegateX5 getUIDelegate() {
        return mUIDelegate;
    }

    private BaseWebX5Component component() {
        return DaggerBaseWebX5Component.builder()
                .activityModule(new ActivityModule(getActivity()))
                .baseWebX5Module(new BaseWebX5Module(this, getActivity().getIntent().getBooleanExtra(KEY_CACHE_ENABLE, false)))
                .build();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        component().inject(this);
        setContentView(getPresenter().getWebView());
        getPresenter().load(mUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onLifecycleEvent(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().onLifecycleEvent(FragmentEvent.PAUSE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().onLifecycleEvent(FragmentEvent.DESTROY_VIEW);
        mPresenter = null;
        mUIDelegate = null;
        mUrl = null;
    }

    @Override
    public void resolveThemeAttribute() {
        super.resolveThemeAttribute();
        getUIDelegate().resolveThemeAttribute();
    }

    @Override
    protected void initData() {
        mUrl = getArguments().getString(KEY_URL);
    }

    @Override
    protected void initContentView() {
        getUIDelegate().initContentView();
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
    public final NavigationBar initNavigationBar() {
        return null;
    }

    @Override
    public final void onNavCustomItemClick(ImageView view) {
    }

    @Override
    public final List<ShareItem> getShareItems() {
        return null;
    }

    @Override
    public final boolean onShareItemClick(int position, View v, ShareItem item) {
        return false;
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
    public void onProgress(WebView webView, int progress) {
        getUIDelegate().onProgress(progress);
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onReceivedError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onReceivedTitle(WebView webView, String title) {
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
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
    }

    @Override
    public void onHideCustomView() {
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
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    }

    public static BaseWebX5Fragment instantiate(@NonNull Context context, @NonNull String url) {
        return instantiate(context, url, false);
    }

    public static BaseWebX5Fragment instantiate(@NonNull Context context, @NonNull String url, boolean cacheEnable) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putBoolean(KEY_CACHE_ENABLE, cacheEnable);
        return (BaseWebX5Fragment) Fragment.instantiate(context, BaseWebX5Fragment.class.getName(), bundle);
    }
}
