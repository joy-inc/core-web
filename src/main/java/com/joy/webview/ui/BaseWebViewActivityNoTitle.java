package com.joy.webview.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;

import com.joy.inject.module.ActivityModule;
import com.joy.share.ShareUtil;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebViewComponent;
import com.joy.webview.component.DaggerBaseWebViewComponent;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.presenter.BaseWebViewPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;

import javax.inject.Inject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Daisw on 16/9/7.
 */

public class BaseWebViewActivityNoTitle extends BaseHttpUiActivity implements BaseViewWeb {

    private static final String KEY_URL = "KEY_URL";
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

        Intent intent = new Intent(context, BaseWebViewActivityNoTitle.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }
}
