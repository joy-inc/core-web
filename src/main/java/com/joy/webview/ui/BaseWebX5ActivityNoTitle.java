package com.joy.webview.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

import com.joy.inject.module.ActivityModule;
import com.joy.share.ShareUtil;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebX5Component;
import com.joy.webview.component.DaggerBaseWebX5Component;
import com.joy.webview.module.BaseWebX5Module;
import com.joy.webview.presenter.BaseWebX5Presenter;
import com.joy.webview.ui.interfaces.BaseViewWebX5;
import com.tencent.smtt.sdk.WebView;

import javax.inject.Inject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Daisw on 16/9/7.
 */

public class BaseWebX5ActivityNoTitle extends BaseHttpUiActivity implements BaseViewWebX5 {

    private static final String KEY_URL = "KEY_URL";
    private String mUrl;

    @Inject
    BaseWebX5Presenter mPresenter;

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

    private BaseWebX5Component component() {

        return DaggerBaseWebX5Component.builder()
                .activityModule(new ActivityModule(this))
                .baseWebX5Module(new BaseWebX5Module(this))
                .build();
    }

    @Override
    protected void initData() {

        mUrl = getIntent().getStringExtra(KEY_URL);
    }

    @Override
    protected void doOnRetry() {

        mPresenter.reload();
    }

    @Override
    public final void onReceivedTitle(WebView view, String title) {
    }

    @Override
    public boolean overrideUrl(WebView view, String url) {

        return false;
    }

    private View initNavigationBar() {

        ShareUtil shareUtil = new ShareUtil(this);

        View v = inflateLayout(R.layout.lib_view_web_navigation_bar);
        v.findViewById(R.id.ivBack).setOnClickListener((v1) -> mPresenter.goBack());
        v.findViewById(R.id.ivClose).setOnClickListener((v1) -> finish());
        v.findViewById(R.id.ivBrowser).setOnClickListener((v1) -> showToast("browser"));
        v.findViewById(R.id.icMore).setOnClickListener((v1) -> shareUtil.show());
        return v;
    }

    public static void startActivity(Context context, String url) {

        Intent intent = new Intent(context, BaseWebX5ActivityNoTitle.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }
}
