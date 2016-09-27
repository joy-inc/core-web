package com.joy.webview.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.joy.inject.module.ActivityModule;
import com.joy.share.ShareUtil;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebX5Component;
import com.joy.webview.component.DaggerBaseWebX5Component;
import com.joy.webview.module.BaseWebX5Module;
import com.joy.webview.presenter.BaseWebX5Presenter;
import com.joy.webview.ui.interfaces.BaseViewWebX5;
import com.tencent.smtt.sdk.WebView;

import javax.inject.Inject;

/**
 * Created by Daisw on 16/8/14.
 */

public class BaseWebX5Activity extends BaseHttpUiActivity implements BaseViewWebX5 {

    private static final String KEY_URL = "KEY_URL";
    private static final String KEY_TITLE = "KEY_TITLE";
    private String mUrl;
    private String mTitle;
    private TextView mTvTitle;

    @Inject
    BaseWebX5Presenter mPresenter;

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
    }

    @Override
    protected void initTitleView() {

        ShareUtil shareUtil = new ShareUtil(this);

        addTitleLeftBackView(R.drawable.ic_arrow_back_white_24dp);
        addTitleRightView(R.drawable.ic_more_vert_white_24dp, (v) -> shareUtil.show());
//        if (TextUtil.isNotEmpty(mTitle))
        mTvTitle = addTitleMiddleView(mTitle);
    }

    @Override
    protected void doOnRetry() {

        mPresenter.reload();
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {

        if (TextUtil.isEmpty(mTitle))
            mTvTitle.setText(title);
    }

    @Override
    public boolean overrideUrl(WebView view, String url) {

        return false;
    }

    public static void startActivity(Context context, String url) {

        startActivity(context, url, null);
    }

    public static void startActivity(Context context, String url, String title) {

        Intent intent = new Intent(context, BaseWebX5Activity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }
}
