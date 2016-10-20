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
import android.widget.TextView;

import com.joy.inject.module.ActivityModule;
import com.joy.share.JoyShare;
import com.joy.share.ShareItem;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebX5Component;
import com.joy.webview.component.DaggerBaseWebX5Component;
import com.joy.webview.module.BaseWebX5Module;
import com.joy.webview.presenter.BaseWebX5Presenter;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWebX5;
import com.joy.webview.ui.interfaces.KConstant;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient.FileChooserParams;
import com.tencent.smtt.sdk.WebView;

import java.util.List;

import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 16/8/14.
 */

public class BaseWebX5Activity extends BaseHttpUiActivity implements BaseViewWebX5, KConstant {

    @Inject
    BaseWebX5Presenter mPresenter;

    protected String mUrl;
    protected String mTitle;
    protected TextView mTvTitle;
    protected JoyShare mJoyShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component().inject(this);
        setContentView(mPresenter.getWebView());
        mPresenter.load(mUrl);
    }

    private BaseWebX5Component component() {
        return DaggerBaseWebX5Component.builder()
                .activityModule(new ActivityModule(this))
                .baseWebX5Module(new BaseWebX5Module(this))
                .build();
    }

    @Override
    protected void initData() {
        mUrl = getIntent().getStringExtra(KEY_URL);
        mTitle = getIntent().getStringExtra(KEY_TITLE);
        mPresenter.setUserAgent(getIntent().getStringExtra(KEY_USER_AGENT));

        mJoyShare = new JoyShare(this);
        mJoyShare.setData(getShareItems());
        mJoyShare.setOnItemClickListener(this::onShareItemClick);
    }

    @Override
    protected void initTitleView() {
        addTitleLeftBackView(R.drawable.ic_arrow_back_white_24dp);
        addTitleRightView(R.drawable.ic_more_vert_white_24dp, (v) -> mJoyShare.show());
        mTvTitle = addTitleMiddleView(mTitle);
    }

    protected final IPresenter getPresenter() {
        return mPresenter;
    }

    protected List<ShareItem> getShareItems() {
        return mJoyShare.getDefaultItems();
    }

    protected void onShareItemClick(int position, View v, ShareItem item) {
        mJoyShare.dismiss();
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
    public void onReceivedError(WebView view, WebResourceRequest resourceRequest) {
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

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
    }

    @Override
    public void onHideCustomView() {
    }

    // file upload callback for 3.0 ~ 5.0
    @Override
    @TargetApi(HONEYCOMB)
    public void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, @Nullable String capture) {
    }

    // file upload callback for >= 5.0
    @Override
    @TargetApi(LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        return false;
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    }

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        startActivity(context, url, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable String title) {
        startActivity(context, url, title, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable String title, @Nullable String userAgent) {
        Intent intent = new Intent(context, BaseWebX5Activity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_USER_AGENT, userAgent);
        context.startActivity(intent);
    }
}
