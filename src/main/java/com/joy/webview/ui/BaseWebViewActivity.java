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
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebView;
import android.widget.TextView;

import com.joy.inject.module.ActivityModule;
import com.joy.share.JoyShare;
import com.joy.share.ShareItem;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.ui.adapter.OnItemClickListener;
import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebViewComponent;
import com.joy.webview.component.DaggerBaseWebViewComponent;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.presenter.BaseWebViewPresenter;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.ui.interfaces.KConstant;

import java.util.List;

import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 16/8/14.
 */

public class BaseWebViewActivity extends BaseHttpUiActivity implements BaseViewWeb, KConstant {

    @Inject
    BaseWebViewPresenter mPresenter;

    private String mUrl;
    private String mTitle;
    private TextView mTvTitle;
    private JoyShare mJoyShare;

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

    @Override
    protected void initData() {
        mUrl = getIntent().getStringExtra(KEY_URL);
        mTitle = getIntent().getStringExtra(KEY_TITLE);
        mPresenter.setUserAgent(getIntent().getStringExtra(KEY_USER_AGENT));

        mJoyShare = new JoyShare(this);
        mJoyShare.setData(mJoyShare.getDefaultItems());
    }

    @Override
    protected void initTitleView() {
        addTitleLeftBackView(R.drawable.ic_arrow_back_white_24dp);
        addTitleRightView(R.drawable.ic_more_vert_white_24dp, (v) -> mJoyShare.show());
        mTvTitle = addTitleMiddleView(mTitle);
    }

    protected IPresenter getPresenter() {
        return mPresenter;
    }

    protected JoyShare getJoyShare() {
        return mJoyShare;
    }

    protected void setShareData(List<ShareItem> shareItems) {
        mJoyShare.setData(shareItems);
    }

    protected void setShareOnItemClickListener(OnItemClickListener<ShareItem> l) {
        mJoyShare.setOnItemClickListener(l);
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
        Intent intent = new Intent(context, BaseWebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_USER_AGENT, userAgent);
        context.startActivity(intent);
    }
}
